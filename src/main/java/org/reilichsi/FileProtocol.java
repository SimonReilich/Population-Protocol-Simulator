package org.reilichsi;

import java.util.*;
import java.util.stream.Collectors;

public class FileProtocol extends PopulationProtocol<String> {
    private final Set<String> Q;
    private final Map<String, Map<String, Set<Pair<String, String>>>> transitions;
    private final Set<String> I;
    private final Map<String, Boolean> output;

    public FileProtocol(String input) {
        super();

        // different parts of the input are seperated by ";"
        String[] tokens = input.replace(" ", "").replace("\n", "").replace("\r", "").split(";");

        // the set of states is the first line
        Q = Arrays.stream(tokens[0].split(",")).collect(Collectors.toSet());

        // the set of initial states is the second line
        I = Arrays.stream(tokens[1].split(",")).collect(Collectors.toSet());
        output = new HashMap<>();

        // the set of positive outputs Q_+ is the third line
        Arrays.stream(tokens[2].split(",")).forEach(s -> output.put(s, true));
        // (Q_- = Q \ Q_+)
        Q.forEach(s -> output.putIfAbsent(s, false));

        transitions = new HashMap<>();
        Q.forEach(s -> transitions.put(s, new HashMap<>()));
        for (int i = 3; i < tokens.length; i++) {
            // each transition has the structure p, q -> p', q' with p, q, p', q' in Q
            String[] args = tokens[i].split(",|(->)");
            Set<Pair<String, String>> set = new HashSet<>(transitions.getOrDefault(args[0], Map.of()).getOrDefault(args[1], Set.of()));
            set.add(new Pair<>(args[2], args[3]));
            transitions.get(args[0]).put(args[1], set);
        }
    }

    @Override
    public Set<String> getQ() {
        return Q;
    }

    @Override
    public Set<Pair<String, String>> delta(String x, String y) {
        return transitions.getOrDefault(x, Map.of()).getOrDefault(y, Set.of());
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