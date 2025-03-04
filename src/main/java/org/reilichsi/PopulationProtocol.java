package org.reilichsi;

import java.util.*;

public abstract class PopulationProtocol<T extends Comparable<T>> {
    private final Map<Population<T>, Population<T>> reachable;
    private final Map<Population<T>, Optional<Boolean>> ambiguity;

    public PopulationProtocol() {
        reachable = new HashMap<>();
        ambiguity = new HashMap<>();
    }

    public abstract Set<T> getQ();
    public abstract Set<Pair<T, T>> delta (T x, T y);
    public abstract Set<T> getI();
    public abstract boolean output(T state);

    public boolean hasConsensus(Population<T> config) {
        if (config.consensus(this::output).isEmpty()) {
            return false;
        }

        return true;
    }
}
