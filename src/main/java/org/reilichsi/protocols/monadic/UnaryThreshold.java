package org.reilichsi.protocols.monadic;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;
import org.reilichsi.protocols.monadic.predicates.Predicate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class UnaryThreshold extends PopulationProtocol<Pair<Integer, Integer>> {

    private final Predicate p;

    public UnaryThreshold(Predicate p) {
        super(1, p.toString());
        p.assertUnary();
        p.assertThreshold();
        this.p = p;
    }

    @Override
    public boolean O(Pair<Integer, Integer> state) {
        return p.evaluate(state.second());
    }

    @Override
    public String stateToString(Pair<Integer, Integer> state) {
        return state.toString();
    }

    @Override
    public boolean function(int... x) {
        return p.evaluate(x);
    }

    @Override
    public Set<Pair<Integer, Integer>> getQ() {
        Set<Pair<Integer, Integer>> set = new HashSet<>();
        for (int k = 1; k <= p.cMax(); k++) {
            for (int l = k; l <= p.cMax(); l++) {
                set.add(new Pair<>(k, l));
            }
        }
        return set;
    }

    @Override
    public Set<Pair<Integer, Integer>> I() {
        return Set.of(new Pair<>(1, 1));
    }

    @Override
    public Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> delta(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        if (Objects.equals(x.first(), y.first())) {
            int lMax = Math.max(Math.max(y.second(), x.second()), y.first() + 1);
            return Set.of(new Pair<>(new Pair<>(x.first(), lMax), new Pair<>(y.first() + 1, lMax)));
        } else {
            int lMax = Math.max(y.second(), x.second());
            return Set.of(new Pair<>(new Pair<>(x.first(), lMax), new Pair<>(y.first(), lMax)));
        }
    }

    @Override
    public Population<Pair<Integer, Integer>> genConfig(int... x) {
        assert x.length == 1;

        Population<Pair<Integer, Integer>> config = new Population<>(this);
        for (int i = 0; i < x[0]; i++) {
            config.add(new Pair<>(1, 1));
        }
        return config;
    }

    @Override
    public Optional<Boolean> hasConsensus(Population<Pair<Integer, Integer>> config) {
        if (!config.stream().map(Pair::second).allMatch(l -> l >= config.size())) {
            return Optional.empty();
        } else {
            return Optional.of(function(config.get(0).second()));
        }
    }

    @Override
    public boolean statesEqual(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        return x.equals(y);
    }

    @Override
    public Pair<Integer, Integer> parseString(String s) {
        return null;
    }
}
