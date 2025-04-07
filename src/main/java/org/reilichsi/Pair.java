package org.reilichsi;

public record Pair<T, S>(T first, S second) {

    public static boolean arePairsJoint(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        if (p1.first() < p2.first()) {
            return p1.second() > p2.first();
        } else if (p2.first() < p1.first()) {
            return p2.second() > p1.first();
        } else {
            return true;
        }
    }

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
