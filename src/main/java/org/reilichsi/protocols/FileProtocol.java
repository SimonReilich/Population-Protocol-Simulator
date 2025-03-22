package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FileProtocol extends PopulationProtocol<String> {
    private final String[] Q;
    private final String[] I;
    private final Map<String, Map<String, Set<Pair<String, String>>>> transitions;
    private final Map<String, Boolean> output;

    public FileProtocol(String[] tokens) throws IOException {
        // different parts of the input are seperated by ";"

        super(tokens[1].split(",").length, n -> "Custom Predicate");

        // the set of states is the first line
        this.Q = tokens[0].split(",");

        for (int i = 0; i < this.Q.length; i++) {
            this.Q[i] = this.Q[i].trim();
        }

        // the set of initial states is the second line
        this.I = tokens[1].split(",");

        for (int i = 0; i < this.I.length; i++) {
            this.I[i] = this.I[i].trim();
        }
        this.output = new HashMap<>();

        // the set of positive outputs Q_+ is the third line
        Arrays.stream(tokens[2].split(",")).forEach(s -> this.output.put(s, true));
        // (Q_- = Q \ Q_+)
        Arrays.stream(this.Q).forEach(s -> this.output.putIfAbsent(s, false));

        this.transitions = new HashMap<>();
        Arrays.stream(this.Q).forEach(s -> this.transitions.put(s, new HashMap<>()));
        for (int i = 3; i < tokens.length; i++) {
            // each transition has the structure p, q -> p', q' with p, q, p', q' in Q
            String[] args = tokens[i].split(",|(->)");
            Set<Pair<String, String>> set = new HashSet<>(this.transitions.getOrDefault(args[0], Map.of()).getOrDefault(args[1], Set.of()));
            set.add(new Pair<>(args[2], args[3]));
            this.transitions.get(args[0]).put(args[1], set);
        }
    }

    @Override
    public boolean predicate(int... x) {
        throw new UnsupportedOperationException("Calculating the predicate for custom protocols is not supported");
    }

    @Override
    public Set<String> getQ() {
        return Arrays.stream(this.Q).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getI() {
        return Arrays.stream(this.I).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<String, String>> delta(String x, String y) {
        return this.transitions.getOrDefault(x, Map.of()).getOrDefault(y, Set.of());
    }

    @Override
    public boolean output(String state) {
        return this.output.getOrDefault(state, false);
    }

    @Override
    public Optional<Boolean> consensus(Population<String> config) {
        if (config.stream().map(this::output).distinct().count() > 1) {
            return Optional.empty();
        } else {
            return config.stream().map(this::output).findFirst();
        }
    }

    @Override
    public Population<String> genConfig(int... x) {
        super.assertArgLength(x);
        Population<String> config = new Population<>(this);
        for (int i = 0; i < super.ARG_LEN; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(this.I[i]);
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(String x, String y) {
        return x.equals(y);
    }

    @Override
    public String stateFromString(String s) {
        return s;
    }
}