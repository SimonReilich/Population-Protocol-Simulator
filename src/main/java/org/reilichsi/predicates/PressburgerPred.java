package org.reilichsi.predicates;

import java.util.Map;
import java.util.Set;

public interface PressburgerPred {

    public static boolean AND = true;
    public static boolean OR = false;

    public static boolean UB = false;
    public static boolean LB = true;

    public Boolean apply(Map<Integer, Integer> input);

    public String toString();

    public Set<Integer> getLimits();
}
