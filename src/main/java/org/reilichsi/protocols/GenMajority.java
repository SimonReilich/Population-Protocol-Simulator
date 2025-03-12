package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class GenMajority extends WeakProtocol<Integer> {

    private final int[] a;

    public GenMajority(int... a) {

        super(a.length, n -> "");

        // generating String-representation for predicate
        Function<Integer, String> p = n -> "(" + a[0] + " * x_" + n + ")";
        for (int i = 1; i < a.length; i++) {
            Function<Integer, String> finalP = p;
            int finalI = i;
            p = n -> finalP.apply(n) + "(" + a[finalI] + " * x_" + (n + finalI) + ")";
        }
        Function<Integer, String> finalP = p;
        super.PREDICATE = n -> finalP.apply(n) + " >= 1";

        this.a = a;
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        int n = 0;
        for (int i = 0; i < x.length; i++) {
            n += this.a[i] * x[i];
        }
        return n >= 1;
    }

    @Override
    public Set<Integer> getQ() {
        HashSet<Integer> Q = new HashSet<>();
        for (int i = Arrays.stream(this.a).min().getAsInt(); i <= Arrays.stream(this.a).max().getAsInt(); i++) {
            Q.add(i);
        }
        return Q;
    }

    @Override
    public Set<Integer> getI() {
        HashSet<Integer> I = new HashSet<>();
        for (int ai : this.a) {
            I.add(ai);
        }
        return I;
    }

    @Override
    public Set<Pair<Integer, Integer>> delta(Integer x, Integer y) {
        if (x < 0 && 0 < y) {
            // balancing
            return Set.of(new Pair<>(x + y, 0));
        }
        return Set.of();
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
    public Population<Integer> genConfig(int... x) {
        assertArgLength(x);
        Population<Integer> config = new Population<>();
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(this.a[i]);
            }
        }
        return config;
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
