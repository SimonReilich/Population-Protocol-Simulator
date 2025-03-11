package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileProtocol extends PopulationProtocol<String> {
    private final String[] Q;
    private final Map<String, Map<String, Set<Pair<String, String>>>> transitions;
    private final String[] I;
    private final Map<String, Boolean> output;

    public FileProtocol(BufferedReader r) throws IOException {
        System.out.print("File to read from: ");
        // different parts of the input are seperated by ";"
        String[] tokens = Files.readString(Path.of(r.readLine())).replace(" ", "").replace("\n", "").replace("\r", "").split(";");

        // the set of states is the first line
        String[] Q_temp = tokens[0].split(",");

        for (int i = 0; i < Q_temp.length; i++) {
            Q_temp[i] = Q_temp[i].trim();
        }

        // the set of initial states is the second line
        String[] I_temp = tokens[1].split(",");

        for (int i = 0; i < I_temp.length; i++) {
            I_temp[i] = I_temp[i].trim();
        }

        super(I_temp.length, "Custom Predicate");

        Q = Q_temp;
        I = I_temp;
        output = new HashMap<>();

        // the set of positive outputs Q_+ is the third line
        Arrays.stream(tokens[2].split(",")).forEach(s -> output.put(s, true));
        // (Q_- = Q \ Q_+)
        Arrays.stream(Q).forEach(s -> output.putIfAbsent(s, false));

        transitions = new HashMap<>();
        Arrays.stream(Q).forEach(s -> transitions.put(s, new HashMap<>()));
        for (int i = 3; i < tokens.length; i++) {
            // each transition has the structure p, q -> p', q' with p, q, p', q' in Q
            String[] args = tokens[i].split(",|(->)");
            Set<Pair<String, String>> set = new HashSet<>(transitions.getOrDefault(args[0], Map.of()).getOrDefault(args[1], Set.of()));
            set.add(new Pair<>(args[2], args[3]));
            transitions.get(args[0]).put(args[1], set);
        }
    }

    @Override
    public boolean predicate(int... x) {
        throw new UnsupportedOperationException("Calculating the predicate for custom protocols is not supported");
    }

    @Override
    public Set<String> getQ() {
        return Arrays.stream(Q).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<String, String>> delta(String x, String y) {
        return transitions.getOrDefault(x, Map.of()).getOrDefault(y, Set.of());
    }

    @Override
    public Set<String> getI() {
        return Arrays.stream(I).collect(Collectors.toSet());
    }


    @Override
    public boolean output(String state) {
        return output.getOrDefault(state, false);
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
        if (x.length != super.ARG_LEN) {
            throw new IllegalArgumentException("The number of arguments must be the same as the number of arguments");
        }
        Population<String> config = new Population<>();
        for (int i = 0; i < super.ARG_LEN; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(I[i]);
            }
        }
        return config;
    }

    @Override
    public String stateFromString(String s) {
        return s;
    }
}