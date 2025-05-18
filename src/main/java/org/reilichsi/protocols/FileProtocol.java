package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.IOException;
import java.util.*;

public class FileProtocol extends PopulationProtocol<String> {

    private final String[] I;
    private final Map<String, Map<String, Pair<String, String>>> transitions;
    private final Map<String, Integer> output;

    public FileProtocol(String[] tokens) {
        // different parts of the input are seperated by ";"

        super(tokens[1].split(",").length, "Custom Predicate");

        // the set of states is the first line
        String[] Q = tokens[0].split(",");

        for (int i = 0; i < Q.length; i++) {
            Q[i] = Q[i].trim();
        }

        // the set of initial states is the second line
        this.I = tokens[1].split(",");

        for (int i = 0; i < this.I.length; i++) {
            this.I[i] = this.I[i].trim();
        }
        this.output = new HashMap<>();

        // the set of positive outputs Q_+ is the third line
        Arrays.stream(tokens[2].split(",")).forEach(s -> this.output.put(s, 1));
        // (Q_- = Q \ Q_+)
        Arrays.stream(Q).forEach(s -> this.output.putIfAbsent(s, 0));

        this.transitions = new HashMap<>();
        Arrays.stream(Q).forEach(s -> this.transitions.put(s, new HashMap<>()));
        for (int i = 3; i < tokens.length; i++) {
            // each transition has the structure p, q -> p', q' with p, q, p', q' in Q
            String[] args = tokens[i].split(",|(->)");
            this.transitions.get(args[0]).put(args[1], new Pair<>(args[2], args[3]));
        }
    }

    @Override
    public int function(int... x) {
        throw new UnsupportedOperationException("Calculating the predicate for custom protocols is not supported");
    }

    @Override
    public String I(int x) {
        return this.I[x];
    }

    @Override
    public int O(String state) {
        return this.output.getOrDefault(state, 0);
    }

    @Override
    public Pair<String, String> delta(String x, String y) {
        return this.transitions.getOrDefault(x, Map.of()).getOrDefault(y, new Pair<>(x, y));
    }

    @Override
    public boolean hasConsensus(Population<String> config) {
        if (config.stream().map(this::O).distinct().count() > 1) {
            return false;
        } else {
            return config.stream().map(this::O).findFirst().isPresent();
        }
    }

    @Override
    public String parseString(String s) {
        return s;
    }
}