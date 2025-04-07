package org.reilichsi.protocols;

import org.reilichsi.EitherOr;
import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.states.Interval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InhomTowerCancle extends WeakProtocol<EitherOr<Integer, Interval>> {

    private final int t;
    private final int[] a;
    private final InhomTower dummy;
    private int T;

    public InhomTowerCancle(int t, int... a) {

        super(a.length, "");

        // generating String-representation for predicate
        StringBuilder p = new StringBuilder("(" + a[0] + " * x_" + 0 + ")");
        for (int i = 1; i < a.length; i++) {
            p.append(" + (").append(a[i]).append(" * x_").append(i).append(")");
        }
        super.PREDICATE = p + " >= " + t;

        this.t = t;
        this.a = a;

        for (int ai : a) {
            this.T = Math.max(ai, this.T);
        }

        this.dummy = new InhomTower(t, Arrays.stream(a).map(i -> i < 0 ? -i : i).toArray());
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        int c = 0;
        for (int i = 0; i < x.length; i++) {
            c += this.a[i] * x[i];
        }
        return c >= this.t;
    }

    @Override
    public Set<EitherOr<Integer, Interval>> getQ() {
        Set<EitherOr<Integer, Interval>> set = new HashSet<>();
        for (int i = -Arrays.stream(this.a).max().getAsInt(); i <= 0; i++) {
            set.add(new EitherOr<>(i, null));
        }
        for (int i = 0; i <= this.T; i++) {
            for (int j = i + 1; j <= this.T; j++) {
                set.add(new EitherOr<>(null, new Interval(dummy, i, j)));
            }
        }
        return set;
    }

    @Override
    public Set<EitherOr<Integer, Interval>> getI() {
        Set<EitherOr<Integer, Interval>> set = new HashSet<>();
        for (int ai : this.a) {
            set.add(new EitherOr<>(null, new Interval(dummy, 0, ai)));
            set.add(new EitherOr<>(ai, null));
        }
        return set;
    }

    @Override
    public Set<Pair<EitherOr<Integer, Interval>, EitherOr<Integer, Interval>>> delta(EitherOr<Integer, Interval> x, EitherOr<Integer, Interval> y) {
        if (x.isU() && y.isU() && x.getU().overlaps(y.getU()) && x.getU().start <= y.getU().start && x.getU().end < this.T && y.getU().end < this.T) {
            // step
            return Set.of(new Pair<>(x, new EitherOr<>(null, new Interval(dummy, y.getU().start + 1, y.getU().end + 1))));
        } else if (x.isU() && y.isT() && y.getT() != 0) {
            if (x.getU().start == x.getU().end - 1) {
                // cancel with empty intervall
                return Set.of(new Pair<>(new EitherOr<>(0, null), new EitherOr<>(y.getT() + 1, null)));
            } else {
                // cancel
                return Set.of(new Pair<>(new EitherOr<>(null, new Interval(dummy, x.getU().start, x.getU().end - 1)), new EitherOr<>(y.getT() + 1, null)));
            }
        }
        return Set.of();
    }

    @Override
    public Optional<Boolean> output(EitherOr<Integer, Interval> state) {
        if (state.isT() && state.getT() != 0) {
            return Optional.of(false);
        } else if (state.isU() && state.getU().end >= this.t) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<EitherOr<Integer, Interval>> config) {
        if (config.stream().filter(EitherOr::isT).map(EitherOr::getT).anyMatch(s -> s != 0)) {
            if (config.stream().anyMatch(EitherOr::isU)) {
                return Optional.empty();
            }
            return Optional.of(false);
        }
        return Optional.of(true);
    }

    @Override
    public Population<EitherOr<Integer, Interval>> genConfig(int... x) {
        Population<EitherOr<Integer, Interval>> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            if (this.a[i] > 0) {
                for (int j = 0; j < x[i]; j++) {
                    config.add(new EitherOr<>(null, new Interval(dummy, 0, this.a[i])));
                }
            } else {
                for (int j = 0; j < x[i]; j++) {
                    config.add(new EitherOr<>(this.a[i], null));
                }
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(EitherOr<Integer, Interval> x, EitherOr<Integer, Interval> y) {
        return x.equals(y);
    }

    @Override
    public EitherOr<Integer, Interval> parseString(String s) {
        try {
            return new EitherOr<>(Integer.parseInt(s), null);
        } catch (NumberFormatException e) {
            s = s.trim();
            for (int i = s.indexOf(';'); i < s.length(); i++) {
                String first = s.substring(1, i).trim();
                String second = s.substring(i + 2, s.length() - 1).trim();
                if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                    return new EitherOr<>(null, new Interval(dummy, Integer.parseInt(first), Integer.parseInt(second)));
                }
            }
            throw new IllegalArgumentException("Invalid state: " + s);
        }
    }
}
