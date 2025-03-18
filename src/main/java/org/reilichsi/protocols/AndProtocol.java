package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.*;
import java.util.stream.Collectors;

public class AndProtocol<T, U> extends PopulationProtocol<Pair<T, U>> {

    private final PopulationProtocol<T> p1;
    private final PopulationProtocol<U> p2;

    public AndProtocol(PopulationProtocol<T> protocol1, PopulationProtocol<U> protocol2) {
        super(protocol1.ARG_LEN * protocol2.ARG_LEN, n -> "(" + protocol1.PREDICATE.apply(n) + ") & (" + protocol2.PREDICATE.apply(n + protocol1.ARG_LEN) + ")");
        this.p1 = protocol1;
        this.p2 = protocol2;
    }

    @Override
    public boolean predicate(int... x) {
        super.assertArgLength(x);
        int[] x1 = new int[this.p1.ARG_LEN];
        int[] x2 = new int[this.p2.ARG_LEN];
        for (int i = 0; i < this.p1.ARG_LEN; i++) {
            for (int j = 0; j < this.p2.ARG_LEN; j++) {
                x1[i] += x[i * this.p1.ARG_LEN + j];
                x2[j] += x[i * this.p1.ARG_LEN + j];
            }
        }
        return this.p1.predicate(x1) && this.p2.predicate(x2);
    }

    @Override
    public Set<Pair<T, U>> getQ() {
        // calculating the cross product of Q1 and Q2
        return this.p1.getQ().stream().flatMap(s -> this.p2.getQ().stream().map(t -> new Pair<>(s, t))).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<T, U>> getI() {
        // calculating the cross product of I1 and I2
        return this.p1.getI().stream().flatMap(s -> this.p2.getI().stream().map(t -> new Pair<>(s, t))).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<Pair<T, U>, Pair<T, U>>> delta(Pair<T, U> x, Pair<T, U> y) {
        Set<Pair<T, T>> delta1 = new HashSet<>(this.p1.delta(x.first(), y.first()));
        Set<Pair<U, U>> delta2 = new HashSet<>(this.p2.delta(x.second(), y.second()));

        // result is the cross product of delta1 and delta2
        Set<Pair<Pair<T, U>, Pair<T, U>>> result = new HashSet<>();
        for (Pair<T, T> p1 : delta1) {
            for (Pair<U, U> p2 : delta2) {
                result.add(new Pair<>(new Pair<>(p1.first(), p2.first()), new Pair<>(p1.second(), p2.second())));
            }
        }
        // if delta1 or delta2 are empty, the result would also be empty, although a transition only on one half of the states is possible
        if (result.isEmpty()) {
            for (Pair<T, T> p1 : delta1) {
                result.add(new Pair<>(new Pair<>(p1.first(), x.second()), new Pair<>(p1.second(), y.second())));
            }
            for (Pair<U, U> p2 : delta2) {
                result.add(new Pair<>(new Pair<>(x.first(), p2.first()), new Pair<>(y.first(), p2.second())));
            }
        }
        return result;
    }

    @Override
    public boolean output(Pair<T, U> state) {
        return this.p1.output(state.first()) && this.p2.output(state.second());
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<T, U>> config) {
        Population<T> config1 = new Population<>(config.stream().map(Pair::first).filter(Objects::nonNull).collect(Collectors.toSet()));
        Population<U> config2 = new Population<>(config.stream().map(Pair::second).filter(Objects::nonNull).collect(Collectors.toSet()));
        if (this.p1.consensus(config1).isPresent() && this.p2.consensus(config2).isPresent()) {
            return Optional.of(this.p1.consensus(config1).get() && this.p2.consensus(config2).get());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Population<Pair<T, U>> genConfig(int... x) {
        Population<Pair<T, U>> config = new Population<>();
        for (int i = 0; i < this.p1.ARG_LEN; i++) {
            for (int j = 0; j < this.p2.ARG_LEN; j++) {
                for (int k = 0; k < x[i * this.p1.ARG_LEN + j]; k++) {
                    config.add(new Pair<>(this.p1.genConfig(i).get(0), this.p2.genConfig(j).get(0)));
                }
            }
        }
        return config;
    }

    @Override
    public Pair<T, U> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new Pair<>(this.p1.stateFromString(first), this.p2.stateFromString(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}
