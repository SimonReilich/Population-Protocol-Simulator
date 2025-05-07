package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.robustness.WeakProtocol;

import java.util.*;

public class GenMajority extends WeakProtocol<Integer> {

    private final int[] a;

    public GenMajority(int... a) {

        super(a.length, "");

        // generating String-representation for predicate
        StringBuilder p = new StringBuilder("(" + a[0] + " * x_" + 0 + ")");
        for (int i = 1; i < a.length; i++) {
            p.append("(").append(a[i]).append(" * x_").append(i).append(")");
        }
        super.PREDICATE = p + " >= 1";

        this.a = a;
    }

    private static int min(int[] a) {
        return Arrays.stream(a).min().getAsInt();
    }

    private static int max(int[] a) {
        return Arrays.stream(a).max().getAsInt();
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
        for (int i = min(a); i <= max(a); i++) {
            Q.add(i);
        }
        return Q;
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
        if (config.stream().noneMatch(s -> s > 0)) {
            return Optional.of(false);
        } else if (config.stream().noneMatch(s -> s < 0)) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Population<Integer> genConfig(int... x) {
        assertArgLength(x);
        Population<Integer> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(this.a[i]);
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(Integer x, Integer y) {
        return Objects.equals(x, y);
    }

    @Override
    public Integer parseString(String s) {
        int v = Integer.parseInt(s);
        if (getQ().contains(v)) {
            return v;
        } else {
            throw new IllegalArgumentException("Invalid state: " + s);
        }
    }
}
