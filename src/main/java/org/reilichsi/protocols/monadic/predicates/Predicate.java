package org.reilichsi.protocols.monadic.predicates;

import java.util.Set;

public interface Predicate {

    public final boolean AND = true;
    public final boolean OR = false;
    public final boolean LEQ = true;
    public final boolean GEQ = true;

    Set<Integer> getVariables();

    void assertUnary();

    void assertDistinct();

    void assertThreshold();

    void assertModulo();

    int cMax();

    boolean evaluate(int... x);

    @Override
    String toString();
}
