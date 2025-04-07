package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
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
        super.PREDICATE = p + " >= " + t;

        this.a = a;
        this.t = t;
    }

    @Override
    public boolean predicate(int... x) {
        super.assertArgLength(x);
        int n = 0;
        for (int i = 0; i < x.length; i++) {
            n += this.a[i] * x[i];
        }
        return n >= this.t;
    }

    @Override
    public Set<Interval> getQ() {
        HashSet<Interval> Q = new HashSet<>();
        for (int ai : this.a) {
            for (int j = 0; j + ai <= this.t + 1; j++) {
                Q.add(new Interval(this, j, j + ai));
            }
        }
        return Q;
    }

    @Override
    public Set<Interval> getI() {
        HashSet<Interval> I = new HashSet<>();
        for (int ai : this.a) {
            I.add(new Interval(this, 0, ai));
        }
        return I;
    }

    @Override
    public Set<Pair<Interval, Interval>> delta(Interval x, Interval y) {
        if (x.overlaps(y) && x.start <= y.end && x.end <= this.t && y.end <= this.t) {
            // step
            return Set.of(new Pair<>(x, new Interval(this, y.start + 1, y.end + 1)));
        } else if (x.end == this.t + 1 && y.end <= this.t) {
            // accum
            return Set.of(new Pair<>(x, new Interval(this, this.t + 1 - (y.end - y.start), this.t + 1)));
        }
        return Set.of();
    }

    @Override
    public boolean output(Interval state) {
        return state.end == this.t + 1;
    }

    @Override
    public Optional<Boolean> consensus(Population<Interval> config) {
        if (config.stream().anyMatch(this::output)) {
            return config.stream().filter(this::output).count() < config.size() ? Optional.empty() : Optional.of(true);
        } else {
            return config.stream().anyMatch(i1 -> config.stream().filter(i1::overlaps).count() > 1) ? Optional.empty() : Optional.of(false);
        }
    }

    @Override
    public Population<Interval> genConfig(int... x) {
        super.assertArgLength(x);
        Population<Interval> config = new Population<>(this);
        for (int i = 0; i < super.ARG_LEN; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(new Interval(this, 0, this.a[i]));
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(Interval x, Interval y) {
        return x.equals(y);
    }

    public Interval parseString(String s) {
        return Interval.parse(this, s);
    }
}

