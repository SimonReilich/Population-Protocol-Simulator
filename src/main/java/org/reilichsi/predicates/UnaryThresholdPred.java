package org.reilichsi.predicates;

import org.reilichsi.Pair;

public class UnaryThresholdPred extends ThresholdPred implements UnaryCondition {

    public UnaryThresholdPred(int t, boolean bound, int coefficient, int variable) {
        super(t, bound, new Pair<>(coefficient, variable));
    }
}
