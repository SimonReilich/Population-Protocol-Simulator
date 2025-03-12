package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class InhomTowerCancle extends WeakProtocol<Object> {

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
            p = n -> finalP.apply(n) + "(" + a[finalI] + " * x_" + (n + finalI) + ")";
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
        return false;
    }

    @Override
    public Set<Object> getQ() {
        Set<Object> set = new HashSet<>();
        for (int i = -Arrays.stream(this.a).max().getAsInt(); i <= 0; i++) {
            set.add(i);
        }
        for (int i = 0; i <= this.T; i++) {
            for (int j = i + 1; j <= this.T; j++) {
                set.add(new Pair<>(i, j));
            }
        }
        return set;
    }

    @Override
    public Set<Object> getI() {
        Set<Object> set = new HashSet<>();
        for (int ai : this.a) {
            set.add(new Pair<>(0, ai));
            set.add(ai);
        }
        return set;
    }

    @Override
    public Set<Pair<Object, Object>> delta(Object x, Object y) {
        if (x instanceof Pair && y instanceof Pair && ((Pair<Integer, Integer>) x).first() <= ((Pair<Integer, Integer>) y).first() && ((Pair<Integer, Integer>) x).second() < this.T && ((Pair<Integer, Integer>) y).second() < this.T) {
            // step
            return Set.of(new Pair<>(x, new Pair<>(((Pair<Integer, Integer>) y).first() + 1, ((Pair<Integer, Integer>) y).second() + 1)));
        } else if (x instanceof Pair && y instanceof Integer && ((int) y) != 0 && ((Pair<Integer, Integer>) x).first() < ((Pair<Integer, Integer>) x).second()) {
            if (((Pair<Integer, Integer>) x).first() == ((Pair<Integer, Integer>) x).second() - 1) {
                // cancel with empty intervall
                return Set.of(new Pair<>(0, ((int) y) + 1));
            } else {
                // cancel
                return Set.of(new Pair<>(new Pair<>(((Pair<Integer, Integer>) x).first(), ((Pair<Integer, Integer>) x).second() - 1), ((int) y) + 1));
            }
        }
        return Set.of();
    }

    @Override
    public Optional<Boolean> output(Object state) {
        if (state instanceof Integer && ((int) state) != 0) {
            return Optional.of(false);
        } else if (state instanceof Pair && ((Pair<Integer, Integer>) state).second() >= this.t) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<Object> config) {
        if (config.stream().filter(s -> s instanceof Integer).anyMatch(s -> ((int) s) != 0)) {
            if (config.stream().anyMatch(s -> s instanceof Pair)) {
                return Optional.empty();
            }
            return Optional.of(false);
        }
        return Optional.of(true);
    }

    @Override
    public Population<Object> genConfig(int... x) {
        Population<Object> config = new Population<>();
        for (int i = 0; i < x.length; i++) {
            if (this.a[i] > 0) {
                for (int j = 0; j < x[i]; j++) {
                    config.add(new Pair<>(0, this.a[i]));
                }
            } else {
                for (int j = 0; j < x[i]; j++) {
                    config.add(this.a[i]);
                }
            }
        }
        return config;
    }

    @Override
    public Object stateFromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
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
}
