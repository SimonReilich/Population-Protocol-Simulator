package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.EitherOr;
import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.WeakProtocol;
import org.reilichsi.protocols.states.Interval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InhomTowerCancle extends WeakProtocol<EitherOr<Integer, Interval>> {

    private final int t;
    private final int[] a;
    private int T;

    public InhomTowerCancle(int t, int... a) {

        super(a.length, "");

        // generating String-representation for function
        StringBuilder p = new StringBuilder("(" + a[0] + " * x_" + 0 + ")");
        for (int i = 1; i < a.length; i++) {
            p.append(" + (").append(a[i]).append(" * x_").append(i).append(")");
        }
        super.FUNCTION = p + " >= " + t;

        this.t = t;
        this.a = a;

        for (int ai : a) {
            this.T = Math.max(ai, this.T);
        }
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        int c = 0;
        for (int i = 0; i < x.length; i++) {
            c += this.a[i] * x[i];
        }
        return (c >= this.t) ? 1 : 0;
    }

    @Override
    public EitherOr<Integer, Interval> I(int x) {
        if (a[x] < 0) {
            return new EitherOr<>(a[x], null);
        } else {
            return new EitherOr<>(null, new Interval(0, a[x]));
        }
    }

    @Override
    public Optional<Integer> O(EitherOr<Integer, Interval> state) {
        if (state.isT() && state.getT() != 0) {
            return Optional.of(0);
        } else if (state.isU() && state.getU().end() >= this.t) {
            return Optional.of(1);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Pair<EitherOr<Integer, Interval>, EitherOr<Integer, Interval>> delta(EitherOr<Integer, Interval> x, EitherOr<Integer, Interval> y) {
        if (x.isU() && y.isU() && x.getU().overlaps(y.getU()) && x.getU().start() <= y.getU().start() && x.getU().end() < this.T && y.getU().end() < this.T) {
            // step
            return new Pair<>(x, new EitherOr<>(null, new Interval(y.getU().start() + 1, y.getU().end() + 1)));
        } else if (x.isU() && y.isT() && y.getT() != 0) {
            if (x.getU().start() == x.getU().end() - 1) {
                // cancel with empty intervall
                return new Pair<>(new EitherOr<>(0, null), new EitherOr<>(y.getT() + 1, null));
            } else {
                // cancel
                return new Pair<>(new EitherOr<>(null, new Interval(x.getU().start(), x.getU().end() - 1)), new EitherOr<>(y.getT() + 1, null));
            }
        }
        return new Pair<>(x, y);
    }

    @Override
    public boolean hasConsensus(Population<EitherOr<Integer, Interval>> config) {
        if (config.stream().filter(EitherOr::isT).map(EitherOr::getT).anyMatch(s -> s != 0)) {
            return config.stream().noneMatch(EitherOr::isU);
        }
        return true;
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
                    return new EitherOr<>(null, new Interval(Integer.parseInt(first), Integer.parseInt(second)));
                }
            }
            throw new IllegalArgumentException("Invalid state: " + s);
        }
    }
}
