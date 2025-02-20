package org.reilichsi;

import com.sun.source.tree.BreakTree;

import java.util.*;
import java.util.stream.Collectors;

public class PopProtoSim implements PopulationProtocol<String> {
    private final Set<String> Q;
    private final Map<String, Map<String, Set<Pair<String, String>>>> transitions;
    private final Set<String> I;
    private final Map<String, Boolean> output;

    public PopProtoSim(String input) {
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

    private boolean stateAgrees(List<String> config) {
        Optional<Boolean> last = Optional.empty();

        for (String s : config) {
            if (last.isEmpty()) {
                last = Optional.of(output(s));
            } else if (last.get() != output(s)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasConsensus(List<String> config, boolean[] alive) {
        List<String> configCopy = new ArrayList<>();
        for (int i = 0; i < config.size(); i++) {
            if (alive[i]) {
                configCopy.add(config.get(i));
            }
        }

        if (!stateAgrees(configCopy)) {
            return false;
        }

        List<Set<List<String>>> reached = new ArrayList<Set<List<String>>>();
        reached.add(new HashSet<>());
        reached.getFirst().add(configCopy);
        int round = 0;

        while (round < reached.size()) {
            for (List<String> reachedConfig : reached.get(round)) {
                for (int i = 0; i < configCopy.size(); i++) {
                    for (int j = i + 1; j < configCopy.size(); j++) {
                        int finalI = i;
                        int finalJ = j;
                        Set<List<String>> newConfigs = delta(reachedConfig.get(i), reachedConfig.get(j)).stream()
                                .map(pair -> {
                                    List<String> newConfig = new ArrayList<>(reachedConfig);
                                    newConfig.set(finalI, pair.getFirst());
                                    newConfig.set(finalJ, pair.getSecond());
                                    return newConfig;
                                }).filter(c -> !isConfigInSet(c, reached)).collect(Collectors.toSet());
                        if (!newConfigs.isEmpty()) {
                            reached.add(newConfigs);
                            if (newConfigs.stream().anyMatch(c -> !stateAgrees(c) || (output(configCopy.getFirst()) != output(c.getFirst())))) {
                                return false;
                            }
                        }
                    }
                }
            }
            round++;
        }
        return true;
    }

    private static boolean areConfigsEquiv(List<String> l1, List<String> l2) {
        List<String> l2Temp = new ArrayList<>();
        l2Temp.addAll(l2);
        for (String s : l1) {
            if (!l2Temp.contains(s)) {
                return false;
            } else {
                l2Temp.remove(s);
            }
        }
        return l2Temp.isEmpty();
    }

    private static boolean isConfigInSet(List<String> config, List<Set<List<String>>> sets) {
        for (Set<List<String>> set : sets) {
            if (set.stream().anyMatch(l -> areConfigsEquiv(config, l))) {
                return true;
            }
        }
        return false;
    }
}
