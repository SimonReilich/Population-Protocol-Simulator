package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.util.Objects;

public class Tower extends PopulationProtocol<Integer> {

    private final int t;

    public Tower(int t) {
        super(1, "x_ß >= " + t);
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
        if (Objects.equals(x, y) && x < this.t) {
            // push
            return new Pair<>(x + 1, y);
        } else if (Boolean.logicalXor(x == this.t, y == this.t)) {
            // pull
            return new Pair<>(this.t, this.t);
        }
        return new Pair<>(x, y);
    }

    @Override
    public boolean hasConsensus(Population<Integer> config) {
        if (config.count(this.t) == config.size()) {
            return true;
        } else return !config.contains(this.t) && config.count(0) >= config.size() - 1;
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
