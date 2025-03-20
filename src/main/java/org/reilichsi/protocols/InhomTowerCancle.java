package org.reilichsi.protocols;

import org.reilichsi.EitherOr;
import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class InhomTowerCancle extends WeakProtocol<EitherOr<Integer, Pair<Integer, Integer>>> {

    private final int t;
    private final int[] a;
    private int T;

    public InhomTowerCancle(int t, int... a) {

        super(a.length, n -> "");

        // generating String-representation for predicate
        Function<Integer, String> p = n -> "(" + a[0] + " * x_" + n + ")";
        for (int i = 1; i < a.length; i++) {
            Function<Integer, String> finalP = p;
            int finalI = i;
            p = n -> finalP.apply(n) + " + (" + a[finalI] + " * x_" + (n + finalI) + ")";
        }
        Function<Integer, String> finalP = p;
        super.PREDICATE = n -> finalP.apply(n) + " >= " + t;

        this.t = t;
        this.a = a;

        for (int ai : a) {
            this.T = Math.max(ai, this.T);
        }
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
    public Set<EitherOr<Integer, Pair<Integer, Integer>>> getQ() {
        Set<EitherOr<Integer, Pair<Integer, Integer>>> set = new HashSet<>();
        for (int i = -Arrays.stream(this.a).max().getAsInt(); i <= 0; i++) {
            set.add(new EitherOr<>(i, null));
        }
        for (int i = 0; i <= this.T; i++) {
            for (int j = i + 1; j <= this.T; j++) {
                set.add(new EitherOr<>(null, new Pair<>(i, j)));
            }
        }
        return set;
    }

    @Override
    public Set<EitherOr<Integer, Pair<Integer, Integer>>> getI() {
        Set<EitherOr<Integer, Pair<Integer, Integer>>> set = new HashSet<>();
        for (int ai : this.a) {
            set.add(new EitherOr<>(null, new Pair<>(0, ai)));
            set.add(new EitherOr<>(ai, null));
        }
        return set;
    }

    @Override
    public Set<Pair<EitherOr<Integer, Pair<Integer, Integer>>, EitherOr<Integer, Pair<Integer, Integer>>>> delta(EitherOr<Integer, Pair<Integer, Integer>> x, EitherOr<Integer, Pair<Integer, Integer>> y) {
        if (x.isU() && y.isU() && x.getU().first() <= y.getU().first() && x.getU().second() < this.T && y.getU().second() < this.T) {
            // step
            return Set.of(new Pair<>(x, new EitherOr<>(null, new Pair<>(y.getU().first() + 1, y.getU().second() + 1))));
        } else if (x.isU() && y.isT() && y.getT() != 0 && x.getU().first() < x.getU().second()) {
            if ((x.getU()).first() == x.getU().second() - 1) {
                // cancel with empty intervall
                return Set.of(new Pair<>(new EitherOr<>(0, null), new EitherOr<>(y.getT() + 1, null)));
            } else {
                // cancel
                return Set.of(new Pair<>(new EitherOr<>(null, new Pair<>(x.getU().first(), x.getU().second() - 1)), new EitherOr<>(y.getT() + 1, null)));
            }
        }
        return Set.of();
    }

    @Override
    public Optional<Boolean> output(EitherOr<Integer, Pair<Integer, Integer>> state) {
        if (state.isT() && state.getT() != 0) {
            return Optional.of(false);
        } else if (state.isU() && state.getU().second() >= this.t) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<EitherOr<Integer, Pair<Integer, Integer>>> config) {
        if (config.stream().filter(EitherOr::isT).map(EitherOr::getT).anyMatch(s -> s != 0)) {
            if (config.stream().anyMatch(EitherOr::isU)) {
                return Optional.empty();
            }
            return Optional.of(false);
        }
        return Optional.of(true);
    }

    @Override
    public Population<EitherOr<Integer, Pair<Integer, Integer>>> genConfig(int... x) {
        Population<EitherOr<Integer, Pair<Integer, Integer>>> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            if (this.a[i] > 0) {
                for (int j = 0; j < x[i]; j++) {
                    config.add(new EitherOr<>(null, new Pair<>(0, this.a[i])));
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
    public EitherOr<Integer, Pair<Integer, Integer>> stateFromString(String s) {
        try {
            return new EitherOr<>(Integer.parseInt(s), null);
        } catch (NumberFormatException e) {
            s = s.trim();
            for (int i = s.indexOf(';'); i < s.length(); i++) {
                String first = s.substring(1, i).trim();
                String second = s.substring(i + 2, s.length() - 1).trim();
                if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                    return new EitherOr<>(null, new Pair<>(Integer.parseInt(first), Integer.parseInt(second)));
                }
            }
            throw new IllegalArgumentException("Invalid state: " + s);
        }
    }
}
