package org.reilichsi.protocols.monadic.predicates;

import java.util.HashSet;
import java.util.Set;

public class ModuloClause implements Predicate {

    public final int var;
    public final boolean leq;
    public final int m;
    public final int t;

    public ModuloClause(int var, boolean leq, int m, int t) {
        assert t < m;

        this.var = var;
        this.leq = leq;
        this.m = m;
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
        assert false;
    }

    @Override
    public void assertModulo() {
        assert true;
    }

    @Override
    public int cMax() {
        return 1;
    }

    @Override
    public boolean evaluate(int... x) {
        if (leq) {
            return x[var] % m <= t;
        } else {
            return x[var] % m >= t;
        }
    }

    @Override
    public String toString() {
        if (leq) {
            return "x_" + var + " <= " + t + " mod " + m;
        } else  {
            return "x_" + var + " >= " + t + " mod " + m;
        }
    }
}