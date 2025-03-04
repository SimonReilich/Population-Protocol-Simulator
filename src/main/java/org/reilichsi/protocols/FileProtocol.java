package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileProtocol extends PopulationProtocol<String> {
    private final Set<String> Q;
    private final Map<String, Map<String, Set<Pair<String, String>>>> transitions;
    private final Set<String> I;
    private final Map<String, Boolean> output;

    public FileProtocol(BufferedReader r) throws IOException {
        super();

        System.out.print("File to read from: ");
        // different parts of the input are seperated by ";"
        String[] tokens = Files.readString(Path.of(r.readLine())).replace(" ", "").replace("\n", "").replace("\r", "").split(";");

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
    public Population<String> initializeConfig(BufferedReader r) throws IOException {
        Population<String> config = new Population<>();

        // Prompt the user for the number of agents in each initial state
        for (String state : I) {
            System.out.print("How many agents in state " + state + "?: ");
            int count = Integer.parseInt(r.readLine());
            for (int i = 0; i < count; i++) {
                config.add(state);
            }
        }
        return config;
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
}