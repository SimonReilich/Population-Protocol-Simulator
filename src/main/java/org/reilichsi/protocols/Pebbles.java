package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Pebbles extends PopulationProtocol<Integer> {

    private final int t;

    public Pebbles(int t) {
        super(1, n -> "x_" + n + " >= " + t);
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
        for (int i = 0; i <= this.t; i++) {
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
        if (x + y < this.t) {
            if (x == 0 || y == 0) {
                return Set.of();
            } else {
                // collect
                return Set.of(new Pair<>(x + y, 0));
            }
        } else {
            if (x == this.t && y == this.t) {
                return Set.of();
            } else {
                // pull
                return Set.of(new Pair<>(this.t, this.t));
            }
        }
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
        return x == y;
    }

    @Override
    public Integer stateFromString(String s) {
        int state = Integer.parseInt(s);
        if (state < 0 || state > this.t) {
            throw new IllegalArgumentException("Invalid state: " + s + ". Must be between 0 and " + this.t);
        } else {
            return state;
        }
    }
}
