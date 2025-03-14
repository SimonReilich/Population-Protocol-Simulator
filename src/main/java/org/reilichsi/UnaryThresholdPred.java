package org.reilichsi;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UnaryThresholdPred {

    private final EitherOr<Pair<Boolean, Integer>, Pair<Boolean, Pair<UnaryThresholdPred, UnaryThresholdPred>>> value;

    public UnaryThresholdPred(boolean leq, int c) {
        this.value = new EitherOr<>(new Pair<>(leq, c), null);
    }

    public UnaryThresholdPred(UnaryThresholdPred p1, boolean and, UnaryThresholdPred p2) {
        this.value = new EitherOr<>(null, new Pair<>(and, new Pair<>(p1, p2)));
    }

    public boolean apply(int x) {
        if (this.value.isT()) {
            return this.value.getT().first() ? x <= this.value.getT().second() : x >= this.value.getT().second();
        } else {
            return this.value.getU().first() ? this.value.getU().second().first().apply(x) && this.value.getU().second().second().apply(x) : this.value.getU().second().first().apply(x) || this.value.getU().second().second().apply(x);
        }
    }

    public Set<Integer> getConstants() {
        if (this.value.isT()) {
            Set<Integer> result = new HashSet<>();
            result.add(this.value.getT().second());
            return result;
        } else {
            Set<Integer> result = this.value.getU().second().first().getConstants();
            result.addAll(this.value.getU().second().second().getConstants());
            return result;
        }
    }

    public Function<Integer, String> toStringFunc() {
        if (this.value.isT()) {
            return n -> "x_" + n + (this.value.getT().first() ? " <= " : " >= ") + this.value.getT().second();
        } else {
            return n -> "(" + this.value.getU().second().first().toStringFunc().apply(n) + (this.value.getU().first() ? " && " : " || ") + this.value.getU().second().second().toStringFunc().apply(n) + ")";
        }
    }
}
