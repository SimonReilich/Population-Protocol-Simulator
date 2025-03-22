package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class InhomTower extends PopulationProtocol<Pair<Integer, Integer>> {

    private final int[] a;
    private final int t;

    public InhomTower(int t, int... a) {

        super(a.length, n -> "");

        // generating String-representation for predicate
        Function<Integer, String> p = n -> "(" + a[0] + " * x_" + n + ")";
        for (int i = 1; i < a.length; i++) {
            Function<Integer, String> finalP = p;
            int finalI = i;
            p = n -> finalP.apply(n) + "(" + a[finalI] + " * x_" + (n + finalI) + ")";
        }
        Function<Integer, String> finalP = p;
        super.PREDICATE = n -> finalP.apply(n) + " >= " + t;

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
    public Set<Pair<Integer, Integer>> getQ() {
        HashSet<Pair<Integer, Integer>> Q = new HashSet<>();
        for (int ai : this.a) {
            for (int j = 0; j + ai <= this.t + 1; j++) {
                Q.add(new Pair<>(j, j + ai));
            }
        }
        return Q;
    }

    @Override
    public Set<Pair<Integer, Integer>> getI() {
        HashSet<Pair<Integer, Integer>> I = new HashSet<>();
        for (int ai : this.a) {
            I.add(new Pair<>(0, ai));
        }
        return I;
    }

    @Override
    public Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> delta(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        if (Helper.arePairsJoint(x, y) && x.first() <= y.first() && x.second() <= this.t && y.second() <= this.t) {
            // step
            return Set.of(new Pair<>(x, new Pair<>(y.first() + 1, y.second() + 1)));
        } else if (x.second() == this.t + 1 && y.second() <= this.t) {
            // accum
            return Set.of(new Pair<>(x, new Pair<>(this.t + 1 - (y.second() - y.first()), this.t + 1)));
        }
        return Set.of();
    }

    @Override
    public boolean output(Pair<Integer, Integer> state) {
        return state.second() == this.t + 1;
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Integer, Integer>> config) {
        if (config.stream().anyMatch(this::output)) {
            return config.stream().filter(this::output).count() < config.sizeActive() ? Optional.empty() : Optional.of(true);
        } else {
            return config.stream().anyMatch(s -> config.stream().filter(s2 -> Helper.arePairsJoint(s, s2)).count() > 1) ? Optional.empty() : Optional.of(false);
        }
    }

    @Override
    public Population<Pair<Integer, Integer>> genConfig(int... x) {
        super.assertArgLength(x);
        Population<Pair<Integer, Integer>> config = new Population<>(this);
        for (int i = 0; i < super.ARG_LEN; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(new Pair<>(0, this.a[i]));
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        return x.equals(y);
    }

    public Pair<Integer, Integer> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new Pair<>(Integer.parseInt(first), Integer.parseInt(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}
