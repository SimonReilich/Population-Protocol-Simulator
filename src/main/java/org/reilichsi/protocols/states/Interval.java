package org.reilichsi.protocols.states;

import org.reilichsi.protocols.robustness.threshold.InhomTower;

import java.util.Arrays;

public class Interval {

    public final int start;
    public final int end;
    private final InhomTower protocol;

    public Interval(InhomTower protocol, int start, int end) {
        this.protocol = protocol;
        this.start = start;
        this.end = end;
    }

    public Interval(int t, int[] a, int start, int end) {
        this.protocol = new InhomTower(t, a);
        this.start = start;
        this.end = end;
    }

    public static Interval parse(InhomTower protocol, String s) {
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
        return new Interval(protocol, Integer.parseInt(intervall[0]), Integer.parseInt(intervall[1]));
    }

    private void check() {
        if (this.start < 0) {
            throw new IllegalArgumentException("start is out of bounds");
        } else if (this.end > this.protocol.t + 1) {
            throw new IllegalArgumentException("end is out of bounds");
        } else if (Arrays.stream(this.protocol.a).noneMatch(i -> i == this.end - this.start)) {
            throw new IllegalArgumentException("invalid shape");
        }
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
