package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Objects;
import java.util.stream.Collectors;

public class PlusProtocol<T, U> extends PopulationProtocol<Pair<T, U>> {

    private final PopulationProtocol<T> p1;
    private final PopulationProtocol<U> p2;

    public PlusProtocol(PopulationProtocol<T> protocol1, PopulationProtocol<U> protocol2) {
        super(protocol1.ARG_LEN, "(" + protocol1.FUNCTION + ") + (" + protocol2.FUNCTION + ")");
        if (protocol1.ARG_LEN != protocol2.ARG_LEN) {
            throw new IllegalArgumentException("The protocols must have the same argument length.");
        }
        this.p1 = protocol1;
        this.p2 = protocol2;
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        return this.p1.function(x) + this.p2.function(x);
    }

    @Override
    public Pair<T, U> I(int x) {
        return new Pair<>(p1.I(x), p2.I(x));
    }

    @Override
    public int O(Pair<T, U> state) {
        return this.p1.O(state.first()) + this.p2.O(state.second());
    }

    @Override
    public Pair<Pair<T, U>, Pair<T, U>> delta(Pair<T, U> x, Pair<T, U> y) {
        Pair<T, T> delta1 = this.p1.delta(x.first(), y.first());
        Pair<U, U> delta2 = this.p2.delta(x.second(), y.second());
        return new Pair<>(new Pair<>(delta1.first(), delta2.first()), new Pair<>(delta1.second(), delta2.second()));
    }

    @Override
    public boolean hasConsensus(Population<Pair<T, U>> config) {
        Population<T> config1 = new Population<>(this.p1, config.stream().map(Pair::first).filter(Objects::nonNull).collect(Collectors.toSet()));
        Population<U> config2 = new Population<>(this.p2, config.stream().map(Pair::second).filter(Objects::nonNull).collect(Collectors.toSet()));
        return this.p1.hasConsensus(config1) && this.p2.hasConsensus(config2);
    }

    @Override
    public Pair<T, U> parseString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new Pair<>(this.p1.parseString(first), this.p2.parseString(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}
