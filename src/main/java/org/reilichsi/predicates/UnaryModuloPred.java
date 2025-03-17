package org.reilichsi.predicates;

import org.reilichsi.Pair;

public class UnaryModuloPred extends ModuloPred implements UnaryCondition {

    public UnaryModuloPred(int t, int m, boolean bound, int coefficient, int variable) {
        super(t, m, bound, new Pair<>(coefficient, variable));
    }
}
