package org.reilichsi.protocols.robustness.threshold;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.util.Objects;

public class SignedNumbers extends PopulationProtocol<Pair<Boolean, Integer>> {

    public SignedNumbers() {
        super(2, "x_0 - 2x_1 >= 1");
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        return (x[0] - (2 * x[1]) >= 1) ? 1 : 0;
    }

    @Override
    public Pair<Boolean, Integer> I(int x) {
        if (x == 0) {
            return new Pair<>(true, 1);
        } else {
            return new Pair<>(false, 2);
        }
    }

    @Override
    public int O(Pair<Boolean, Integer> state) {
        return (Objects.equals(state, new Pair<>(true, 1)) || Objects.equals(state, new Pair<>(true, 0))) ? 1 : 0;
    }

    @Override
    public Pair<Pair<Boolean, Integer>, Pair<Boolean, Integer>> delta(Pair<Boolean, Integer> x, Pair<Boolean, Integer> y) {
        if (Objects.equals(x, new Pair<>(false, 2))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                // from derived
                return new Pair<>(new Pair<>(false, 1), new Pair<>(true, 0));
            }
        } else if (Objects.equals(x, new Pair<>(false, 1))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                // from derived
                return new Pair<>(new Pair<>(false, 0), new Pair<>(false, 0));
            }
        } else if (Objects.equals(x, new Pair<>(false, 0))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                // from witnessPos
                return new Pair<>(new Pair<>(true, 0), new Pair<>(true, 1));
            }
        } else if (Objects.equals(x, new Pair<>(true, 0))) {
            if (Objects.equals(y, new Pair<>(false, 2))) {
                // from witnessNeg
                return new Pair<>(new Pair<>(false, 0), new Pair<>(false, 2));
            } else if (Objects.equals(y, new Pair<>(false, 1))) {
                // from witnessNeg
                return new Pair<>(new Pair<>(false, 0), new Pair<>(false, 1));
            } else if (Objects.equals(y, new Pair<>(false, 0))) {
                // from convince
                return new Pair<>(new Pair<>(false, 0), new Pair<>(false, 0));
            }
        }
        return new Pair<>(x, y);
    }

    @Override
    public boolean hasConsensus(Population<Pair<Boolean, Integer>> config) {
        if (config.stream().allMatch(Pair::first)) {
            return true;
        } else {
            return config.stream().noneMatch(Pair::first);
        }
    }

    @Override
    public Pair<Boolean, Integer> parseString(String s) {
        s = s.trim();
        return switch (s) {
            case "+1", "1" -> new Pair<>(true, 1);
            case "-1" -> new Pair<>(false, 1);
            case "-2" -> new Pair<>(false, 2);
            case "+0" -> new Pair<>(true, 0);
            case "-0" -> new Pair<>(false, 0);
            default -> throw new IllegalArgumentException("Not a valid state: " + s);
        };
    }
}
