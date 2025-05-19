package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.WeakProtocol;

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
        super.FUNCTION = p + " >= 1";

        this.a = a;
    }

    private static int min(int[] a) {
        return Arrays.stream(a).min().getAsInt();
    }

    private static int max(int[] a) {
        return Arrays.stream(a).max().getAsInt();
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        int n = 0;
        for (int i = 0; i < x.length; i++) {
            n += this.a[i] * x[i];
        }
        return (n >= 1) ? 1 : 0;
    }

    @Override
    public Integer I(int x) {
        return a[x];
    }

    @Override
    public Optional<Integer> O(Integer state) {
        if (state == 0) {
            return Optional.empty();
        } else if (state > 0) {
            return Optional.of(1);
        } else {
            return Optional.of(0);
        }
    }

    @Override
    public Pair<Integer, Integer> delta(Integer x, Integer y) {
        if (x < 0 && 0 < y) {
            // balancing
            return new Pair<>(x + y, 0);
        }
        return new Pair<>(x, y);
    }

    @Override
    public boolean hasConsensus(Population<Integer> config) {
        if (config.stream().noneMatch(s -> s > 0)) {
            return true;
        } else {
            return config.stream().noneMatch(s -> s < 0);
        }
    }

    @Override
    public boolean statesEqual(Integer x, Integer y) {
        return Objects.equals(x, y);
    }

    @Override
    public Integer parseString(String s) {
        return Integer.parseInt(s);
    }
}
