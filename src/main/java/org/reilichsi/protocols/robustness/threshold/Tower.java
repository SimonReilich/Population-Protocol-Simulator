package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Tower extends PopulationProtocol<Integer> {

    private final int t;

    public Tower(int t) {
        super(1, "x_ß >= " + t);
        this.t = t;
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        return x[0] >= this.t;
    }

    @Override
    public Set<Integer> getQ() {
        Set<Integer> Q = new HashSet<>();
        for (int i = 1; i <= this.t; i++) {
            Q.add(i);
        }
        return Q;
    }

    @Override
    public Set<Integer> getI() {
        return Set.of(1);
    }

    @Override
    public Set<Pair<Integer, Integer>> delta(Integer x, Integer y) {
        if (Objects.equals(x, y) && x < this.t) {
            // push
            return Set.of(new Pair<>(x + 1, y));
        } else if (Boolean.logicalXor(x == this.t, y == this.t)) {
            // pull
            return Set.of(new Pair<>(this.t, this.t));
        }
        return Set.of();
    }

    @Override
    public boolean output(Integer state) {
        return state >= this.t;
    }

    @Override
    public Optional<Boolean> consensus(Population<Integer> config) {
        if (config.count(this.t) == config.size()) {
            return Optional.of(true);
        } else if (config.contains(this.t) || config.count(0) < config.size() - 1) {
            return Optional.empty();
        } else {
            return Optional.of(false);
        }
    }

    @Override
    public Population<Integer> genConfig(int... x) {
        assertArgLength(x);
        Population<Integer> config = new Population<>(this);
        for (int i = 0; i < x[0]; i++) {
            config.add(1);
        }
        return config;
    }

    @Override
    public boolean statesEqual(Integer x, Integer y) {
        return Objects.equals(x, y);
    }

    @Override
    public Integer parseString(String s) {
        int state = Integer.parseInt(s);
        if (state < 0 || state > this.t) {
            throw new IllegalArgumentException("Invalid state: " + s + ". Must be between 0 and " + this.t);
        } else {
            return state;
        }
    }
}
