package org.reilichsi;

import java.util.Set;

public interface PopulationProtocol<T> {
    public Set<T> getQ();
    public Set<Pair<T>> delta (T x, T y);
    public Set<T> getI();
    public boolean output(T state);
}
