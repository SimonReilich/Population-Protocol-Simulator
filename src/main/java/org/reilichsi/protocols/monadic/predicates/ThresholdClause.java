package org.reilichsi.protocols.monadic.predicates;

import java.util.HashSet;
import java.util.Set;

public class ThresholdClause implements Predicate {

    public final int var;
    public final boolean leq;
    public final int t;

    public ThresholdClause(int var, boolean leq, int t) {
        this.var = var;
        this.leq = leq;
        this.t = t;
    }

    @Override
    public Set<Integer> getVariables() {
        Set<Integer> set = new HashSet<>();
        set.add(var);
        return set;
    }

    @Override
    public void assertUnary() {
        assert true;
    }

    @Override
    public void assertDistinct() {
        assert true;
    }

    @Override
    public void assertThreshold() {
        assert true;
    }

    @Override
    public void assertModulo() {
        assert false;
    }

    @Override
    public int cMax() {
        return t;
    }

    @Override
    public boolean evaluate(int... x) {
        if (leq) {
            return x[var] <= t;
        } else {
            return x[var] >= t;
        }
    }

    @Override
    public String toString() {
        if (leq) {
            return "x_" + var + " <= " + t;
        } else  {
            return "x_" + var + " >= " + t;
        }
    }
}
