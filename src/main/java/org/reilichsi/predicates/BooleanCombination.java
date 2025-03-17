package org.reilichsi.predicates;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BooleanCombination<T extends PressburgerPred> implements PressburgerPred {

    public final static boolean AND = true;
    public final static boolean OR = false;

    public final static boolean UB = false;
    public final static boolean LB = false;

    private final BooleanCombination<T> child1;
    private final boolean operator;
    private final BooleanCombination<T> child2;

    private final T pred;

    public BooleanCombination(BooleanCombination<T> child1, boolean operator, BooleanCombination<T> child2) {
        this.child1 = child1;
        this.operator = operator;
        this.child2 = child2;
        pred = null;
    }

    public BooleanCombination(T pred) {
        this.pred = pred;

        child1 = null;
        child2 = null;
        operator = AND;
    }

    public Boolean apply(Map<Integer, Integer> input) {
        if (this.child1 != null && this.child2 != null) {
            if (this.operator == AND) {
                return this.child1.apply(input) && this.child2.apply(input);
            } else {
                return this.child1.apply(input) || this.child2.apply(input);
            }
        } else {
            return this.pred.apply(input);
        }
    }

    public String toString() {
        if (this.child1 != null && this.child2 != null) {
            if (this.operator == AND) {
                return "(" + this.child1.toString() + ") && (" + this.child2.toString() + ")";
            } else {
                return "(" + this.child1.toString() + ") || (" + this.child2.toString() + ")";
            }
        } else {
            return this.pred.toString();
        }
    }

    @Override
    public Set<Integer> getLimits() {
        if (this.child1 != null && this.child2 != null) {
            Set<Integer> limits = new HashSet<Integer>();
            limits.addAll(this.child1.getLimits());
            limits.addAll(this.child2.getLimits());
            return limits;
        } else {
            return this.pred.getLimits();
        }
    }
}
