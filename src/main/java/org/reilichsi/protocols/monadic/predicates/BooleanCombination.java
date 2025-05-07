package org.reilichsi.protocols.monadic.predicates;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BooleanCombination implements Predicate {

    public final boolean and;
    public final Predicate left;
    public final Predicate right;

    public BooleanCombination(boolean and, Predicate left, Predicate right) {
        this.and = and;
        this.left = left;
        this.right = right;
    }

    @Override
    public Set<Integer> getVariables() {
        Set<Integer> set = new HashSet<>();
        set.addAll(left.getVariables());
        set.addAll(right.getVariables());
        return set;
    }

    @Override
    public void assertUnary() {
        assert left.getVariables().containsAll(right.getVariables()) && right.getVariables().containsAll(left.getVariables()) && left.getVariables().size() == 1;
    }

    @Override
    public void assertDistinct() {
        assert Collections.disjoint(left.getVariables(), right.getVariables());
    }

    @Override
    public void assertThreshold() {
        left.assertThreshold();
        right.assertThreshold();
    }

    @Override
    public void assertModulo() {
        left.assertModulo();
        right.assertModulo();
    }

    @Override
    public int cMax() {
        return Math.max(left.cMax(), right.cMax());
    }

    @Override
    public boolean evaluate(int... x) {
        if (and) {
            return left.evaluate(x) && right.evaluate(x);
        } else {
            return left.evaluate(x) || right.evaluate(x);
        }
    }

    @Override
    public String toString() {
        if (and) {
            return "(" + left.toString() + ") && (" + right.toString() + ")";
        } else {
            return "(" + left.toString() + ") || (" + right.toString() + ")";
        }
    }
}
