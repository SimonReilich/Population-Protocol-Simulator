package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.states.BigModState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BigModulo extends PopulationProtocol<BigModState> {

    public final int t;
    public final int m;
    private final int[] a;

    public BigModulo(int t, int m, int... a) {
        super(a.length, "");

        // generating String-representation for predicate
        StringBuilder p = new StringBuilder("(" + a[0] + " * x_" + 0 + ")");
        for (int i = 1; i < a.length; i++) {
            p.append(" + (").append(a[i]).append(" * x_").append(i).append(")");
        }
        super.PREDICATE = p + " mod " + m + " >= " + t;

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
    public Set<BigModState> getQ() {
        Set<BigModState> Q = new HashSet<>();
        for (int i = 0; i <= 2 * m; i++) {
            for (int j = 0; j < Math.pow(m, 2 * m); j++) {
                int[] v = new int[2 * m];
                Arrays.fill(v, 0);
                int jMod = j;
                for (int k = 0; k < 2 * m; k++) {
                    v[k] = jMod % m;
                    jMod /= m;
                }
                for (int k = 0; k < Math.pow(2, 2 * m); k++) {
                    boolean[] r = new boolean[2 * m];
                    Arrays.fill(r, false);
                    for (int l = 0; l < 2 * m; l++) {
                        r[l] = (k % 2) == 1;
                    }
                    Q.add(new BigModState(this, i, v, r));
                }
            }
        }
        return Q;
    }

    @Override
    public Set<BigModState> getI() {
        Set<BigModState> I = new HashSet<>();
        for (int ai : a) {
            int[] v = new int[2 * m];
            Arrays.fill(v, ai);
            boolean[] r = new boolean[2 * m];
            Arrays.fill(r, false);
            I.add(new BigModState(this, 0, v, r));
        }
        return I;
    }

    @Override
    public Set<Pair<BigModState, BigModState>> delta(BigModState x, BigModState y) {
        Set<Pair<BigModState, BigModState>> result = new HashSet<>();
        if (x.level == 0) {
            for (int i = 1; i <= 2 * m; i++) {
                if (x.tokens[i - 1] >= 1) {
                    // distrib
                    result.add(new Pair<>(new BigModState(this, i, x.tokens, x.result), y));
                }
            }
        } else if (1 <= x.level && y.level <= 2 * m && x.level < y.level) {
            int[] v = Arrays.copyOfRange(x.tokens, 0, 2 * m);
            int[] w = Arrays.copyOfRange(y.tokens, 0, 2 * m);
            int kv1 = v[y.level - 1];
            int kv2 = v[x.level - 1];
            int kw1 = w[x.level - 1];
            int kw2 = w[y.level - 1];
            v[y.level - 1] = 0;
            v[x.level - 1] = (v[x.level - 1] + w[x.level - 1]) % m;
            w[x.level - 1] = 0;
            w[y.level - 1] = (w[y.level - 1] + kv1) % m;
            if (kv1 != v[y.level - 1] || kv2 != v[x.level - 1] || kw1 != w[x.level - 1] || kw2 != w[y.level - 1]) {
                // steal
                result.add(new Pair<>(new BigModState(this, x.level, v, x.result), new BigModState(this, y.level, w, y.result)));
            }
        } else if (x.level == y.level) {
            int[] v = Arrays.copyOfRange(x.tokens, 0, 2 * m);
            int[] w = Arrays.copyOfRange(y.tokens, 0, 2 * m);
            if (w[x.level - 1] != 0) {
                v[x.level - 1] = (v[x.level - 1] + w[x.level - 1]) % m;
                w[x.level - 1] = 0;
                // retire
                result.add(new Pair<>(new BigModState(this, x.level, v, x.result), new BigModState(this, 0, w, y.result)));
            }
        }

        if (1 <= x.level && x.level <= 2 * m && 0 <= y.level && y.level <= 2 * m) {
            boolean[] r = Arrays.copyOfRange(x.result, 0, 2 * m);
            boolean[] s = Arrays.copyOfRange(y.result, 0, 2 * m);
            r[x.level - 1] = x.tokens[x.level - 1] >= this.t;
            s[x.level - 1] = x.tokens[x.level - 1] >= this.t;
            if (!Arrays.equals(r, x.result) || !Arrays.equals(s, y.result)) {
                // result
                result.add(new Pair<>(new BigModState(this, x.level, x.tokens, r), new BigModState(this, y.level, y.tokens, s)));
            }
        }

        return result;
    }

    @Override
    public boolean output(BigModState state) {
        int x = 0;
        for (boolean r : state.result) {
            x = r ? 1 : 0;
        }
        return x > state.result.length / 2;
    }

    @Override
    public Optional<Boolean> consensus(Population<BigModState> config) {
        if (config.stream().allMatch(s1 -> {
            Population<BigModState> config2 = new Population<>(this);
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
    public Population<BigModState> genConfig(int... x) {
        Population<BigModState> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            int[] v = new int[2 * m];
            Arrays.fill(v, a[i]);
            boolean[] r = new boolean[2 * m];
            Arrays.fill(r, false);
            for (int j = 0; j < x[i]; j++) {
                config.add(new BigModState(this, 0, v, r));
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(BigModState x, BigModState y) {
        return x.equals(y);
    }

    @Override
    public String stateToString(BigModState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(state.level).append(", (").append(state.tokens[0]);
        for (int i = 1; i < state.tokens.length; i++) {
            sb.append(", ").append(state.tokens[i]);
        }
        sb.append("), ");
        for (boolean r : state.result) {
            sb.append(r ? "+" : "-");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public BigModState parseString(String s) {
        return null;
    }
}