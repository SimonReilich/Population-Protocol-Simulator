package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SignedNumbers extends PopulationProtocol<Pair<Boolean, Integer>> {

    public SignedNumbers() {

    }

    @Override
    public Set<Pair<Boolean, Integer>> getQ() {
        return Set.of(new Pair<>(true, 1), new Pair<>(false, 1), new Pair<>(false, 2), new Pair<>(true, 0), new Pair<>(false, 0));
    }

    @Override
    public Set<Pair<Pair<Boolean, Integer>, Pair<Boolean, Integer>>> delta(Pair<Boolean, Integer> x, Pair<Boolean, Integer> y) {
        if (Objects.equals(x, new Pair<>(false, 2))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                return Set.of(new Pair<>(new Pair<>(false, 1), new Pair<>(true, 0)));
            } else {
                return Set.of();
            }
        } else if (Objects.equals(x, new Pair<>(false, 1))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 0)));
            } else {
                return Set.of();
            }
        } else if (Objects.equals(x, new Pair<>(false, 0))) {
            if (Objects.equals(y, new Pair<>(true, 1))) {
                return Set.of(new Pair<>(new Pair<>(true, 0), new Pair<>(true, 1)));
            } else {
                return Set.of();
            }
        } else if (Objects.equals(x, new Pair<>(true, 0))) {
            if (Objects.equals(y, new Pair<>(false, 2))) {
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 2)));
            } else if (Objects.equals(y, new Pair<>(false, 1))) {
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 1)));
            } else if (Objects.equals(y, new Pair<>(false, 0))) {
                return Set.of(new Pair<>(new Pair<>(false, 0), new Pair<>(false, 0)));
            } else {
                return Set.of();
            }
        } else {
            return Set.of();
        }
    }

    @Override
    public Set<Pair<Boolean, Integer>> getI() {
        return Set.of(new Pair<>(false, 2), new Pair<>(true, 1));
    }

    @Override
    public Population<Pair<Boolean, Integer>> initializeConfig(BufferedReader r) throws IOException {
        Population<Pair<Boolean, Integer>> p = new Population<>();
        System.out.println("x - 2y >= 1; x, y >= 0");
        System.out.print("x = ");
        int x = Integer.parseInt(r.readLine());
        for (int i = 0; i < x; i++) {
            p.add(new Pair<>(true, 1));
        }
        System.out.print("y = ");
        int y = Integer.parseInt(r.readLine());
        for (int i = 0; i < y; i++) {
            p.add(new Pair<>(false, 2));
        }
        return p;
    }

    @Override
    public boolean output(Pair<Boolean, Integer> state) {
        return Objects.equals(state, new Pair<>(true, 1)) || Objects.equals(state, new Pair<>(true, 0));
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Boolean, Integer>> config) {
        if (config.stream().allMatch(Pair::getFirst)) {
            return Optional.of(true);
        } else if (config.stream().noneMatch(Pair::getFirst)) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
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
