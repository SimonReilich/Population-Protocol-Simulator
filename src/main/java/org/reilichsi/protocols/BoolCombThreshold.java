package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.predicates.BooleanCombination;
import org.reilichsi.predicates.UnaryCondition;

import java.util.*;

public class BoolCombThreshold extends PopulationProtocol<Pair<Integer, Integer>> {

    private final BooleanCombination<UnaryCondition> p;
    private final int cm;

    public BoolCombThreshold(BooleanCombination<UnaryCondition> predicate) {
        super(1, "(" + predicate.toString() + ")");
        this.p = predicate;
        this.cm = predicate.getLimits().stream().max(Integer::compareTo).get();
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, x[0]);
        return this.p.apply(map);
    }

    @Override
    public Set<Pair<Integer, Integer>> getQ() {
        HashSet<Pair<Integer, Integer>> Q = new HashSet<>();
        for (int k = 1; k <= this.cm; k++) {
            for (int l = k; l <= this.cm; l++) {
                Q.add(new Pair<>(k, l));
            }
        }
        return Q;
    }

    @Override
    public Set<Pair<Integer, Integer>> getI() {
        return Set.of(new Pair<>(1, 1));
    }

    @Override
    public Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> delta(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        if (x.first() == y.first()) {
            return Set.of(new Pair<>(new Pair<>(x.first(), Math.max(x.first() + 1, Math.max(x.second(), y.second()))), new Pair<>(x.first() + 1, Math.max(x.first() + 1, Math.max(x.second(), y.second())))));
        } else if (x.second() != y.second()) {
            return Set.of(new Pair<>(new Pair<>(x.first(), Math.max(x.second(), y.second())), new Pair<>(y.first(), Math.max(x.second(), y.second()))));
        } else {
            return Set.of();
        }
    }

    @Override
    public boolean output(Pair<Integer, Integer> state) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, state.second());
        return p.apply(map);
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Integer, Integer>> config) {
        if (config.stream().map(Pair::first).distinct().count() == config.size()) {
            if (config.stream().map(Pair::second).distinct().count() == 1) {
                Map<Integer, Integer> map = new HashMap<>();
                map.put(1, config.stream().findFirst().get().second());
                return Optional.of(this.p.apply(map));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Population<Pair<Integer, Integer>> genConfig(int... x) {
        assertArgLength(x);
        Population<Pair<Integer, Integer>> config = new Population<>(this);
        for (int i = 0; i < x[0]; i++) {
            config.add(new Pair<>(1, 1));
        }
        return config;
    }

    @Override
    public boolean statesEqual(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        return x.equals(y);
    }

    public Pair<Integer, Integer> parseString(String s) {
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