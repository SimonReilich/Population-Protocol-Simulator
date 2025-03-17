package org.reilichsi.predicates;

import org.reilichsi.Pair;

import java.util.Map;
import java.util.Set;

public class ThresholdPred implements Condition {

    private final Pair<Integer, Integer>[] expr;
    private final boolean bound;
    private final int t;

    public ThresholdPred(int t, boolean bound, Pair<Integer, Integer>... expr) {
        this.expr = expr;
        this.bound = bound;
        this.t = t;
    }

    @Override
    public Boolean apply(Map<Integer, Integer> input) {
        int value = 0;
        for (Pair<Integer, Integer> addend : expr) {
            value += (addend.first() * input.getOrDefault(addend.second(), 0));
        }
        if (this.bound == LB) {
            return value >= t;
        } else {
            return value <= t;
        }
    }

    public int getT() {
        return this.t;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<Integer, Integer> addend : expr) {
            sb.append(addend.first()).append(" * x_").append(addend.second()).append(" + ");
        }
        String expression = sb.substring(0, sb.length() - 3);
        if (this.bound == LB) {
            return expression + " >= " + t;
        } else {
            return expression + " <= " + t;
        }
    }

    @Override
    public Set<Integer> getLimits() {
        return Set.of(this.t);
    }
}
