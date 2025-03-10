package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GenMajority extends WeakProtocol<Integer>{

    private int[] a;

    public GenMajority(BufferedReader r) throws IOException {
        super();
        System.out.print("How many factors?: ");
        int count = Integer.parseInt(r.readLine());
        a = new int[count];
        for (int i = 0; i < count; i++) {
            System.out.print("Factor " + i + ": ");
            a[i] = Integer.parseInt(r.readLine());
        }
    }

    @Override
    public Set<Integer> getQ() {
        HashSet<Integer> Q = new HashSet<>();
        for (int i = Arrays.stream(a).min().getAsInt(); i <= Arrays.stream(a).max().getAsInt(); i++) {
            Q.add(i);
        }
        return Q;
    }

    @Override
    public Set<Pair<Integer, Integer>> delta(Integer x, Integer y) {
        if (x < 0 && 0 < y) {
            return Set.of(new Pair<>(x + y, 0));
        }
        return Set.of();
    }

    @Override
    public Set<Integer> getI() {
        HashSet<Integer> I = new HashSet<>();
        for (int ai : a) {
            I.add(ai);
        }
        return I;
    }

    @Override
    public Population<Integer> initializeConfig(BufferedReader r) throws IOException {
        Population<Integer> config = new Population<>();
        for (int ai : a) {
            System.out.print("factor for skalar " + ai + ": ");
            int count = Integer.parseInt(r.readLine());
            for (int j = 0; j < count; j++) {
                config.add(ai);
            }
        }
        return config;
    }

    @Override
    public Optional<Boolean> output(Integer state) {
        if (state == 0) {
            return Optional.empty();
        } else if (state > 0) {
            return Optional.of(true);
        } else {
            return Optional.of(false);
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<Integer> config) {
        if (config.stream().noneMatch(s -> output(s).isPresent() && output(s).get())) {
            return Optional.of(false);
        } else if (config.stream().noneMatch(s -> output(s).isPresent() && !output(s).get())) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Integer stateFromString(String s) {
        int v = Integer.parseInt(s);
        if (getQ().contains(v)) {
            return v;
        } else {
            throw new IllegalArgumentException("Invalid state: " + s);
        }
    }
}
