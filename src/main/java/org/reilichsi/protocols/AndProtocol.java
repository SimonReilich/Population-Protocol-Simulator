package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AndProtocol<T, U> extends PopulationProtocol<Pair<T, U>> {

    private final PopulationProtocol<T> proto1;
    private final PopulationProtocol<U> proto2;

    public AndProtocol(PopulationProtocol<T> protocol1, PopulationProtocol<U> protocol2) throws IOException {
        super(protocol1.ARG_LEN + protocol2.ARG_LEN, "(" + protocol1.PREDICATE + ") & (" + protocol2.PREDICATE + ")");
        this.proto1 = protocol1;
        this.proto2 = protocol2;
    }

    @Override
    public boolean predicate(int... x) {
        super.assertArgLength(x);
        return this.proto1.predicate(Arrays.copyOfRange(x, 0, this.proto1.ARG_LEN)) && this.proto2.predicate(Arrays.copyOfRange(x, this.proto1.ARG_LEN, this.proto1.ARG_LEN + this.proto2.ARG_LEN));
    }

    @Override
    public Set<Pair<T, U>> getQ() {
        // calculating the cross product of Q1 and Q2
        return this.proto1.getQ().stream().flatMap(s -> this.proto2.getQ().stream().map(t -> new Pair<>(s, t))).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<T, U>> getI() {
        // calculating the cross product of I1 and I2
        return this.proto1.getI().stream().flatMap(s -> this.proto2.getI().stream().map(t -> new Pair<>(s, t))).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<Pair<T, U>, Pair<T, U>>> delta(Pair<T, U> x, Pair<T, U> y) {
        Set<Pair<T, T>> delta1 = new HashSet<>(this.proto1.delta(x.getFirst(), y.getFirst()));
        Set<Pair<U, U>> delta2 = new HashSet<>(this.proto2.delta(x.getSecond(), y.getSecond()));

        // result is the cross product of delta1 and delta2
        Set<Pair<Pair<T, U>, Pair<T, U>>> result = new HashSet<>();
        for (Pair<T, T> p1 : delta1) {
            for (Pair<U, U> p2 : delta2) {
                result.add(new Pair<>(new Pair<>(p1.getFirst(), p2.getFirst()), new Pair<>(p1.getSecond(), p2.getSecond())));
            }
        }
        // if delta1 or delta2 are empty, the result would also be empty, although a transition only on one half of the states is possible
        if (result.isEmpty()) {
            for (Pair<T, T> p1 : delta1) {
                result.add(new Pair<>(new Pair<>(p1.getFirst(), x.getSecond()), new Pair<>(p1.getSecond(), y.getSecond())));
            }
            for (Pair<U, U> p2 : delta2) {
                result.add(new Pair<>(new Pair<>(x.getFirst(), p2.getFirst()), new Pair<>(y.getFirst(), p2.getSecond())));
            }
        }
        return result;
    }

    @Override
    public boolean output(Pair<T, U> state) {
        return this.proto1.output(state.getFirst()) && this.proto2.output(state.getSecond());
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<T, U>> config) {
        Population<T> config1 = new Population<>(config.stream().map(Pair::getFirst).filter(Objects::nonNull).collect(Collectors.toSet()));
        Population<U> config2 = new Population<>(config.stream().map(Pair::getSecond).filter(Objects::nonNull).collect(Collectors.toSet()));
        if (this.proto1.consensus(config1).isPresent() && this.proto2.consensus(config2).isPresent()) {
            return Optional.of(this.proto1.consensus(config1).get() && this.proto2.consensus(config2).get());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Population<Pair<T, U>> genConfig(int... x) {
        throw new UnsupportedOperationException("The configuration should be created by the calling method using proto1 and proto2");
    }

    @Override
    public Pair<T, U> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new Pair<>(proto1.stateFromString(first), proto2.stateFromString(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}
