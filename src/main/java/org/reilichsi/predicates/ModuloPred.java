package org.reilichsi.predicates;

import org.reilichsi.Pair;

import java.util.Map;
import java.util.Set;

public class ModuloPred implements Condition {

    private final Pair<Integer, Integer>[] expr;
    private final boolean bound;
    private final int m;
    private final int t;

    public ModuloPred(int t, int m, boolean bound, Pair<Integer, Integer>... expr) {
        this.expr = expr;
        this.bound = bound;
        this.m = m;
        this.t = t;
    }

    @Override
    public Boolean apply(Map<Integer, Integer> input) {
        int value = 0;
        for (Pair<Integer, Integer> addend : expr) {
            value += (addend.first() * input.getOrDefault(addend.second(), 0));
        }
        if (this.bound == LB) {
            return value % m >= t;
        } else {
            return value % m <= t;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<Integer, Integer> addend : expr) {
            sb.append(addend.first()).append(" * x_").append(addend.second()).append(" + ");
        }
        String expression = sb.substring(0, sb.length() - 3);
        if (this.bound == LB) {
            return expression + " mod " + m + " >= " + t;
        } else {
            return expression + " mod " + m + " <= " + t;
        }
    }

    @Override
    public Set<Integer> getLimits() {
        return Set.of(this.t);
    }
}
