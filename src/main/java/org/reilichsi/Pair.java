package org.reilichsi;

public class Pair<T> {
    private final T first;
    private final T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair<?> p) {
            return first.equals(p.first) && second.equals(p.second);
        }
        return false;
    }
}
