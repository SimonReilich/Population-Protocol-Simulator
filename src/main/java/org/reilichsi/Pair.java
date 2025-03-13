package org.reilichsi;

public record Pair<T, S>(T first, S second) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair<?, ?>(Object first1, Object second1)) {
            return this.first.equals(first1) && this.second.equals(second1);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + this.first + "; " + this.second + ")";
    }
}
