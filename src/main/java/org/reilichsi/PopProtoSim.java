package org.reilichsi;

import java.util.*;
import java.util.stream.Collectors;

public class PopProtoSim implements PopulationProtocol<String> {
    private Set<String> Q;
    private Map<Pair<String>, Set<Pair<String>>> transitions;
    private Set<String> I;
    private Map<String, Boolean> output;

    public PopProtoSim(String input) {
        String[] tokens = input.replace(" ", "").replace("\n", "").split(";");
        Q = Arrays.stream(tokens[0].split(",")).collect(Collectors.toSet());
        I = Arrays.stream(tokens[1].split(",")).collect(Collectors.toSet());
        output = new HashMap<>();
        Arrays.stream(tokens[2].split(",")).forEach(s -> output.put(s, true));
        Q.forEach(s -> output.putIfAbsent(s, false));
        transitions = new HashMap<>();
        for (int i = 2; i < tokens.length; i++) {
            String[] args = tokens[i].split(",|(->)");
            Set<Pair<String>> set = transitions.getOrDefault(new Pair<>(args[0], args[1]), Set.of());
            set.add(new Pair<>(args[2], args[3]));
            transitions.put(new Pair<>(args[0], args[1]), set);
        }
    }

    @Override
    public Set<String> getQ() {
        return Q;
    }

    @Override
    public Set<Pair<String>> delta(String x, String y) {
        return transitions.getOrDefault(new Pair<>(x, y), Set.of());
    }

    @Override
    public Set<String> getI() {
        return I;
    }

    @Override
    public boolean output(String state) {
        return output.getOrDefault(state, false);
    }
}
