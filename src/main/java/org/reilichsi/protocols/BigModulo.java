package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class BigModulo extends PopulationProtocol<Pair<Integer, Pair<Integer[], Boolean[]>>> {

    private final int t;
    private final int m;
    private final int[] a;

    public BigModulo(int t, int m, int... a) {
        super(a.length , n -> "");

        // generating String-representation for predicate
        Function<Integer, String> p = n -> "(" + a[0] + " * x_" + n + ")";
        for (int i = 1; i < a.length; i++) {
            Function<Integer, String> finalP = p;
            int finalI = i;
            p = n -> finalP.apply(n) + " + (" + a[finalI] + " * x_" + (n + finalI) + ")";
        }
        Function<Integer, String> finalP = p;
        super.PREDICATE = n -> finalP.apply(n) + " mod " + m + " >= " + t;

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
        return count % m >= t;
    }

    @Override
    public Set<Pair<Integer, Pair<Integer[], Boolean[]>>> getQ() {
        Set<Pair<Integer, Pair<Integer[], Boolean[]>>> Q = new HashSet<>();
        for (int i = 0; i <= 2 * m; i++) {
            for (int j = 0; j < Math.pow(m, 2 * m); j++) {
                Integer[] v = new Integer[2 * m];
                Arrays.fill(v, 0);
                int jMod = j;
                for (int k = 0; k < 2 * m; k++) {
                    v[k] = jMod % m;
                    jMod /= m;
                }
                for (int k = 0; k < Math.pow(2, 2 * m); k++) {
                    Boolean[] r = new Boolean[2 * m];
                    Arrays.fill(r, false);
                    for (int l = 0; l < 2 * m; l++) {
                        r[l] = (k % 2) == 1;
                    }
                    Q.add(new Pair<>(i, new Pair<>(v, r)));
                }
            }
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
            int kv1 = v[y.first() - 1];
            int kv2 = v[x.first() - 1];
            int kw1 = w[x.first() - 1];
            int kw2 = w[y.first() - 1];
            v[y.first() - 1] = 0;
            v[x.first() - 1] = (v[x.first() - 1] + w[x.first() - 1]) % m;
            w[x.first() - 1] = 0;
            w[y.first() - 1] = (w[y.first() - 1] + kv1) % m;
            if (kv1 != v[y.first() - 1] || kv2 != v[x.first() - 1] || kw1 != w[x.first() - 1] || kw2 != w[y.first() - 1]) {
                // steal
                result.add(new Pair<>(new Pair<>(x.first(), new Pair<>(v, x.second().second())), new Pair<>(y.first(), new Pair<>(w, y.second().second()))));
            }
        } else if (x.first() == y.first()) {
            Integer[] v = Arrays.copyOfRange(x.second().first(), 0, 2 * m);
            Integer[] w = Arrays.copyOfRange(y.second().first(), 0, 2 * m);
            if (w[x.first() - 1] != 0) {
                v[x.first() - 1] = (v[x.first() - 1] + w[x.first() - 1]) % m;
                w[x.first() - 1] = 0;
                // retire
                result.add(new Pair<>(new Pair<>(x.first(), new Pair<>(v, x.second().second())), new Pair<>(0, new Pair<>(w, y.second().second()))));
            }
        }

        if (1 <= x.first() && x.first() <= 2 * m && 0 <= y.first() && y.first() <= 2 * m) {
            Boolean[] r = Arrays.copyOfRange(x.second().second(), 0, 2 * m);
            Boolean[] s = Arrays.copyOfRange(y.second().second(), 0, 2 * m);
            r[x.first() - 1] = x.second().first()[x.first() - 1] >= this.t;
            s[x.first() - 1] = x.second().first()[x.first() - 1] >= this.t;
            if (! Arrays.equals(r, x.second().second()) || ! Arrays.equals(s, y.second().second())) {
                // result
                result.add(new Pair<>(new Pair<>(x.first(), new Pair<>(x.second().first(), r)), new Pair<>(y.first(), new Pair<>(y.second().first(), s))));
            }
        }

        return result;
    }

    @Override
    public boolean output(Pair<Integer, Pair<Integer[], Boolean[]>> state) {
        return Arrays.stream(state.second().second()).filter(s -> s).count() > this.m;
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Integer, Pair<Integer[], Boolean[]>>> config) {
        if (config.stream().allMatch(s1 -> {
            Population<Pair<Integer, Pair<Integer[], Boolean[]>>> config2 = new Population<>(this);
            for (int i = 0; i < config.sizeAll(); i++) {
                if (config.isActive(i)) {
                    config2.add(config.get(i));
                }
            }
            config2.killState(s1);
            return config2.stream().allMatch(s2 -> this.delta(s1, s2).isEmpty());
        })) {
            return Optional.of(this.output(config.get(0)));
        } else {
            return Optional.empty();
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
    public boolean statesEqual(Pair<Integer, Pair<Integer[], Boolean[]>> x, Pair<Integer, Pair<Integer[], Boolean[]>> y) {
        return x.first() == y.first() && Arrays.equals(x.second().first(), y.second().first()) && Arrays.equals(x.second().second(), y.second().second());
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