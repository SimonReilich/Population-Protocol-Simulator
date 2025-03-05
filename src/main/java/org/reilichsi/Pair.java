package org.reilichsi;

public class Pair<T, S> {
    private final T first;
    private final S second;

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair<?, ?> p) {
            return first.equals(p.first) && second.equals(p.second);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + first + "; " + second + ")";
    }
}
