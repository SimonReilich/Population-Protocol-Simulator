package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BigModulo extends PopulationProtocol<Pair<Integer, Pair<Integer[], Boolean[]>>> {

    private final int t;
    private final int m;
    private final int[] a;

    public BigModulo(int t, int m, int... a) {
        super(a.length , n -> "");
        this.t = t;
        this.m = m;
        this.a = a;
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            count += a[i] * x[i];
        }
        return count % m >= 0;
    }

    @Override
    public Set<Pair<Integer, Pair<Integer[], Boolean[]>>> getQ() {
        Set<Pair<Integer, Pair<Integer[], Boolean[]>>> Q = new HashSet<>();
        for (int i = 0; i <= 2 * m; i++) {

        }
        return Q;
    }

    @Override
    public Set<Pair<Integer, Pair<Integer[], Boolean[]>>> getI() {
        Set<Pair<Integer, Pair<Integer[], Boolean[]>>> I = new HashSet<>();
        for (int ai : a) {
            Integer[] v = new Integer[2 * m];
            Arrays.fill(v, ai);
            Boolean[] r = new Boolean[2 * m];
            Arrays.fill(r, false);
            I.add(new Pair<>(0, new Pair<>(v, r)));
        }
        return I;
    }

    @Override
    public Set<Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Pair<Integer[], Boolean[]>>>> delta(Pair<Integer, Pair<Integer[], Boolean[]>> x, Pair<Integer, Pair<Integer[], Boolean[]>> y) {
        Set<Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Pair<Integer[], Boolean[]>>>> result = new HashSet<>();
        if (x.first() == 0) {
            for (int i = 1; i <= 2 * m; i++) {
                if (x.second().first()[i - 1] >= 1) {
                    // distrib
                    result.add(new Pair<>(new Pair<>(i, x.second()), y));
                }
            }
        } else if (1 <= x.first() && y.first() <= 2 * m && x.first() < y.first()) {
            Integer[] v = Arrays.copyOfRange(x.second().first(), 0, 2 * m);
            Integer[] w = Arrays.copyOfRange(y.second().first(), 0, 2 * m);
            int k1 = v[y.first() - 1];
            v[y.first() - 1] = 0;
            v[x.first() - 1] = (v[x.first() - 1] + w[x.first() - 1]) % m;
            w[x.first() - 1] = 0;
            w[y.first() - 1] = (w[y.first() - 1] + k1) % m;
            // steal
            result.add(new Pair<>(new Pair<>(x.first(), new Pair<>(v, x.second().second())), new Pair<>(y.first(), new Pair<>(w, y.second().second()))));
        } else if (x.first() == y.first()) {
            Integer[] v = Arrays.copyOfRange(x.second().first(), 0, 2 * m);
            Integer[] w = Arrays.copyOfRange(y.second().first(), 0, 2 * m);
            v[x.first() - 1] += w[x.first() - 1];
            w[x.first() - 1] = 0;
            // retire
            result.add(new Pair<>(new Pair<>(x.first(), new Pair<>(v, x.second().second())), new Pair<>(0, new Pair<>(w, y.second().second()))));
        }

        if (1 <= x.first() && x.first() <= 2 * m && 0 <= y.first() && y.first() <= 2 * m) {
            Boolean[] r = Arrays.copyOfRange(x.second().second(), 0, 2 * m);
            Boolean[] s = Arrays.copyOfRange(y.second().second(), 0, 2 * m);
            r[x.first() - 1] = x.second().first()[x.first() - 1] >= this.t;
            s[x.first() - 1] = x.second().first()[x.first() - 1] >= this.t;
            // result
            result.add(new Pair<>(new Pair<>(x.first(), new Pair<>(x.second().first(), r)), new Pair<>(y.first(), new Pair<>(y.second().first(), s))));
        }

        return result;
    }

    @Override
    public boolean output(Pair<Integer, Pair<Integer[], Boolean[]>> state) {
        return Arrays.stream(state.second().second()).filter(s -> s).count() > this.m;
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Integer, Pair<Integer[], Boolean[]>>> config) {
        if (config.stream().anyMatch(s -> config.stream().anyMatch(p -> !this.delta(s, p).isEmpty()))) {
            return Optional.empty();
        } else {
            return Optional.of(this.output(config.get(0)));
        }
    }

    @Override
    public Population<Pair<Integer, Pair<Integer[], Boolean[]>>> genConfig(int... x) {
        Population<Pair<Integer, Pair<Integer[], Boolean[]>>> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            Integer[] v = new Integer[2 * m];
            Arrays.fill(v, a[i]);
            Boolean[] r = new Boolean[2 * m];
            Arrays.fill(r, false);
            for (int j = 0; j < x[i]; j++) {
                config.add(new Pair<>(0, new Pair<>(v, r)));
            }
        }
        return config;
    }

    @Override
    public String stateToString(Pair<Integer, Pair<Integer[], Boolean[]>> state) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(state.first()).append(", (").append(state.second().first()[0]);
        for (int i = 1; i < state.second().first().length; i++) {
            sb.append(", ").append(state.second().first()[i]);
        }
        sb.append("), ");
        for (boolean r : state.second().second()) {
            sb.append(r ? "+" : "-");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Pair<Integer, Pair<Integer[], Boolean[]>> stateFromString(String s) {
        return null;
    }
}