package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;
import org.reilichsi.protocols.states.Interval;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InhomTower extends PopulationProtocol<Interval> {

    public final int[] a;
    public final int t;

    public InhomTower(int t, int... a) {

        super(a.length, "");

        // generating String-representation for predicate
        StringBuilder p = new StringBuilder("(" + a[0] + " * x_" + 0 + ")");
        for (int i = 1; i < a.length; i++) {
            p.append("(").append(a[i]).append(" * x_").append(i).append(")");
        }
        super.FUNCTION = p + " >= " + t;

        this.a = a;
        this.t = t;
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        int n = 0;
        for (int i = 0; i < x.length; i++) {
            n += this.a[i] * x[i];
        }
        return (n >= this.t) ? 1 : 0;
    }

    @Override
    public Interval I(int x) {
        return new Interval(0, a[x]);
    }

    @Override
    public int O(Interval state) {
        return (state.end() == this.t + 1) ? 1 : 0;
    }

    @Override
    public Pair<Interval, Interval> delta(Interval x, Interval y) {
        if (x.overlaps(y) && x.start() <= y.end() && x.end() <= this.t && y.end() <= this.t) {
            // step
            return new Pair<>(x, new Interval(y.start() + 1, y.end() + 1));
        } else if (x.end() == this.t + 1 && y.end() <= this.t) {
            // accum
            return new Pair<>(x, new Interval(this.t + 1 - (y.end() - y.start()), this.t + 1));
        }
        return new Pair<>(x, y);
    }

    @Override
    public boolean hasConsensus(Population<Interval> config) {
        if (config.stream().anyMatch(s -> this.O(s) == 1)) {
            return config.stream().filter(s -> this.O(s) == 1).count() >= config.size();
        } else {
            return config.stream().noneMatch(i1 -> config.stream().filter(i1::overlaps).count() > 1);
        }
    }

    public Interval parseString(String s) {
        return Interval.parse(s);
    }
}

