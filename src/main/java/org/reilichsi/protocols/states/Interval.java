package org.reilichsi.protocols.states;

public record Interval(int start, int end) {

    public static Interval parse(String s) {
        s = s.trim();
        if (!s.startsWith("[")) {
            throw new IllegalArgumentException("invalid interval: " + s);
        } else if (!s.endsWith(")")) {
            throw new IllegalArgumentException("invalid interval: " + s);
        }

        String[] intervall = s.substring(1, s.length() - 1).trim().split(",");
        if (intervall.length != 2) {
            throw new IllegalArgumentException("invalid interval: " + s);
        }
        return new Interval(Integer.parseInt(intervall[0]), Integer.parseInt(intervall[1]));
    }

    public boolean overlaps(Interval interval) {
        if (this.start < interval.start) {
            return this.end > interval.end;
        } else if (interval.start < this.start) {
            return interval.end > this.start;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Interval other) {
            return this.start == other.start && this.end == other.end;
        }
        return false;
    }
}
