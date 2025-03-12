package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SignedNumbers extends PopulationProtocol<Pair<Boolean, Integer>> {

    public SignedNumbers() {
        super(2, n -> "x_" + n + " - 2x_" + (n + 1) + " >= 1");
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        return x[0] - (2 * x[1]) >= 1;
    }

    @Override
    public Set<Pair<Boolean, Integer>> getQ() {
        return Set.of(new Pair<>(true, 1), new Pair<>(false, 1), new Pair<>(false, 2), new Pair<>(true, 0), new Pair<>(false, 0));
    }

    @Override
    public Set<Pair<Boolean, Integer>> getI() {
        return Set.of(new Pair<>(false, 2), new Pair<>(true, 1));
    }

    @Override
    public Set<Pair<Pair<Boolean, Integer>, Pair<Boolean, Integer>>> delta(Pair<Boolean, Integer> x, Pair<Boolean, Integer> y) {
        if (Objects.equals(x, new Pair<>(false, 2))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                // from derived
                return Set.of(new Pair<>(new Pair<>(false, 1), new Pair<>(true, 0)));
            }
        } else if (Objects.equals(x, new Pair<>(false, 1))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                // from derived
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 0)));
            }
        } else if (Objects.equals(x, new Pair<>(false, 0))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                // from witnessPos
                return Set.of(new Pair<>(new Pair<>(true, 0), new Pair<>(true, 1)));
            }
        } else if (Objects.equals(x, new Pair<>(true, 0))) {
            if (Objects.equals(y, new Pair<>(false, 2))) {
                // from witnessNeg
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 2)));
            } else if (Objects.equals(y, new Pair<>(false, 1))) {
                // from witnessNeg
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 1)));
            } else if (Objects.equals(y, new Pair<>(false, 0))) {
                // from convince
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 0)));
            }
        }
        return Set.of();
    }

    @Override
    public boolean output(Pair<Boolean, Integer> state) {
        return Objects.equals(state, new Pair<>(true, 1)) || Objects.equals(state, new Pair<>(true, 0));
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Boolean, Integer>> config) {
        if (config.stream().allMatch(Pair::first)) {
            return Optional.of(true);
        } else if (config.stream().noneMatch(Pair::first)) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Population<Pair<Boolean, Integer>> genConfig(int... x) {
        Population<Pair<Boolean, Integer>> config = new Population<>();
        for (int i = 0; i < x[0]; i++) {
            config.add(new Pair<>(true, 1));
        }
        for (int i = 0; i < x[1]; i++) {
            config.add(new Pair<>(false, 2));
        }
        return config;
    }

    @Override
    public Pair<Boolean, Integer> stateFromString(String s) {
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
