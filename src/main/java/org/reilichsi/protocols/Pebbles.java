package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

public class Pebbles extends PopulationProtocol<Integer> {

    private final int t;

    public Pebbles(int t) {
        super(1, "x_0 >= " + t);
        this.t = t;
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        return (x[0] >= this.t) ? 1 : 0;
    }

    @Override
    public Integer I(int x) {
        return 1;
    }

    @Override
    public int O(Integer state) {
        return (state >= this.t) ? 1 : 0;
    }

    @Override
    public Pair<Integer, Integer> delta(Integer x, Integer y) {
        if (x + y < this.t) {
            if (x == 0 || y == 0) {
                return new Pair<>(x, y);
            } else {
                // collect
                return new Pair<>(x + y, 0);
            }
        } else {
            if (x == this.t && y == this.t) {
                return new Pair<>(x, y);
            } else {
                // pull
                return new Pair<>(t, t);
            }
        }
    }

    @Override
    public boolean hasConsensus(Population<Integer> config) {
        return hasTransition(config);
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
