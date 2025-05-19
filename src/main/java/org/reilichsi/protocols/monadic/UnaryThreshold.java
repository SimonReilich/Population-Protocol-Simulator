package org.reilichsi.protocols.monadic;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class UnaryThreshold extends PopulationProtocol<Pair<Integer, Integer>> {

    int cMax;

    public UnaryThreshold(int cMax) {
        super(1, "x_0 if x_0 < " + cMax + "and " + cMax + " else");
        this.cMax = cMax;
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        return Math.min(x[0], cMax);
    }

    @Override
    public Pair<Integer, Integer> I(int x) {
        return new Pair<>(1, 1);
    }

    @Override
    public int O(Pair<Integer, Integer> state) {
        return state.second();
    }

    @Override
    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> delta(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        if (Objects.equals(x.first(), y.first())) {
            int lMax = Math.max(Math.max(y.second(), x.second()), y.first() + 1);
            return new Pair<>(new Pair<>(x.first(), lMax), new Pair<>(y.first() + 1, lMax));
        } else {
            int lMax = Math.max(y.second(), x.second());
            return new Pair<>(new Pair<>(x.first(), lMax), new Pair<>(y.first(), lMax));
        }
    }

    @Override
    public boolean hasConsensus(Population<Pair<Integer, Integer>> config) {
        return config.stream().map(Pair::second).allMatch(l -> l >= config.size());
    }

    @Override
    public Pair<Integer, Integer> parseString(String s) {
        return null;
    }
}
