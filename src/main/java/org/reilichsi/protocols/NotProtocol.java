package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Optional;
import java.util.Set;

public class NotProtocol<T> extends PopulationProtocol<T> {

    private final PopulationProtocol<T> p;

    public NotProtocol(PopulationProtocol<T> protocol) {
        super(protocol.ARG_LEN, protocol.PREDICATE);
        this.p = protocol;
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        return !this.p.predicate(x);
    }

    @Override
    public Set<T> getQ() {
        return this.p.getQ();
    }

    @Override
    public Set<T> getI() {
        return this.p.getI();
    }

    @Override
    public Set<Pair<T, T>> delta(T x, T y) {
        return this.p.delta(x, y);
    }

    @Override
    public boolean output(T state) {
        return !this.p.output(state);
    }

    @Override
    public Optional<Boolean> consensus(Population<T> config) {
        return this.p.consensus(config).map(b -> !b);
    }

    @Override
    public Population<T> genConfig(int... x) {
        assertArgLength(x);
        return this.p.genConfig(x);
    }

    @Override
    public boolean statesEqual(T x, T y) {
        return this.p.statesEqual(x, y);
    }

    @Override
    public T parseString(String s) {
        return this.p.parseString(s);
    }
}
