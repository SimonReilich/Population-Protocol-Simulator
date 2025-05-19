package org.reilichsi.protocols.robustness.modulo;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;
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
        super.FUNCTION = p + " mod " + m + " >= " + t;

        this.t = t;
        this.m = m;
        this.a = a;
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            count += a[i] * x[i];
        }
        return (count % m >= t) ? 1 : 0;
    }

    @Override
    public BigModState I(int x) {
        int[] v = new int[2 * m];
        Arrays.fill(v, a[x]);
        boolean[] r = new boolean[2 * m];
        Arrays.fill(r, false);
        return new BigModState(this, 0, v, r);
    }

    @Override
    public int O(BigModState state) {
        int x = 0;
        for (boolean r : state.result()) {
            if (r) {
                x++;
            }
        }
        return (x > m) ? 1 : 0;
    }

    @Override
    public Pair<BigModState, BigModState> delta(BigModState x, BigModState y) {
        if (x.level() == 0) {
            for (int i = 1; i <= 2 * m; i++) {
                if (x.tokens()[i - 1] >= 1) {
                    // distrib
                    return new Pair<>(new BigModState(this, i, x.tokens(), x.result()), y);
                }
            }
        } else if (1 <= x.level() && y.level() <= 2 * m && x.level() < y.level()) {
            int[] v = Arrays.copyOfRange(x.tokens(), 0, 2 * m);
            int[] w = Arrays.copyOfRange(y.tokens(), 0, 2 * m);
            int kv1 = v[y.level() - 1];
            int kv2 = v[x.level() - 1];
            int kw1 = w[x.level() - 1];
            int kw2 = w[y.level() - 1];
            v[y.level() - 1] = 0;
            v[x.level() - 1] = (v[x.level() - 1] + w[x.level() - 1]) % m;
            w[x.level() - 1] = 0;
            w[y.level() - 1] = (w[y.level() - 1] + kv1) % m;
            if (kv1 != v[y.level() - 1] || kv2 != v[x.level() - 1] || kw1 != w[x.level() - 1] || kw2 != w[y.level() - 1]) {
                // steal
                return new Pair<>(new BigModState(this, x.level(), v, x.result()), new BigModState(this, y.level(), w, y.result()));
            }
        } else if (x.level() == y.level()) {
            int[] v = Arrays.copyOfRange(x.tokens(), 0, 2 * m);
            int[] w = Arrays.copyOfRange(y.tokens(), 0, 2 * m);
            if (w[x.level() - 1] != 0) {
                v[x.level() - 1] = (v[x.level() - 1] + w[x.level() - 1]) % m;
                w[x.level() - 1] = 0;
                // retire
                return new Pair<>(new BigModState(this, x.level(), v, x.result()), new BigModState(this, 0, w, y.result()));
            }
        }

        if (1 <= x.level() && x.level() <= 2 * m && 0 <= y.level() && y.level() <= 2 * m) {
            boolean[] r = Arrays.copyOfRange(x.result(), 0, 2 * m);
            boolean[] s = Arrays.copyOfRange(y.result(), 0, 2 * m);
            r[x.level() - 1] = x.tokens()[x.level() - 1] >= this.t;
            s[x.level() - 1] = x.tokens()[x.level() - 1] >= this.t;
            if (!Arrays.equals(r, x.result()) || !Arrays.equals(s, y.result())) {
                // result
                return new Pair<>(new BigModState(this, x.level(), x.tokens(), r), new BigModState(this, y.level(), y.tokens(), s));
            }
        }
        return new Pair<>(x, y);
    }

    @Override
    public boolean hasConsensus(Population<BigModState> config) {
        return config.stream().allMatch(s1 -> {
            Population<BigModState> config2 = new Population<>(this);
            for (int i = 0; i < config.sizeAll(); i++) {
                if (config.isActive(i)) {
                    config2.add(config.get(i));
                }
            }
            config2.killState(s1);
            return this.hasConsensus(config2);
        });
    }

    @Override
    public BigModState parseString(String s) {
        return null;
    }
}