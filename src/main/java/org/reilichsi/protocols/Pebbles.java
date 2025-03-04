package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Pebbles extends PopulationProtocol<Integer> {

    private int t;

    public Pebbles(BufferedReader r) throws IOException {
        super();
        System.out.print("Number of Ninjas required (threshold c): ");
        t = Integer.parseInt(r.readLine());
    }

    @Override
    public Set<Integer> getQ() {
        Set<Integer> Q = new HashSet<>();
        for (int i = 0; i <= t; i++) {
            Q.add(i);
        }
        return Q;
    }

    @Override
    public Set<Pair<Integer, Integer>> delta(Integer x, Integer y) {
        if (x + y < t) {
            if (x == 0 || y == 0) {
                return Set.of();
            } else {
                return Set.of(new Pair<>(x + y, 0));
            }
        } else {
            if (x == t && y == t) {
                return Set.of();
            } else {
                return Set.of(new Pair<>(t, t));
            }
        }
    }

    @Override
    public Set<Integer> getI() {
        return Set.of(1);
    }

    @Override
    public Population<Integer> initializeConfig(BufferedReader r) throws IOException {
        Population<Integer> config = new Population<>();

        // Prompt the user for the number of agents in initial state
        System.out.print("How many ninjas are present?: ");
        int count = Integer.parseInt(r.readLine());
        for (int i = 0; i < count; i++) {
            config.add(1);
        }
        return config;
    }

    @Override
    public boolean output(Integer state) {
        return state >= t;
    }

    @Override
    public Optional<Boolean> consensus(Population<Integer> config) {
        if (config.countActive(t) == config.sizeActive()) {
            return Optional.of(true);
        } else if (config.contains(t) || config.countActive(0) < config.sizeActive() - 1) {
            return Optional.empty();
        } else {
            return Optional.of(false);
        }
    }
}
