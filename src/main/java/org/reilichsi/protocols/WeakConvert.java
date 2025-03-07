package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: weak convert, currently not working

public class WeakConvert extends PopulationProtocol<Pair<Object, Boolean>> {

    private WeakProtocol weakProtocol;

    public WeakConvert(BufferedReader r) throws IOException {
        weakProtocol = WeakProtocol.getWeakProtocol(r);
    }

    @Override
    public Set<Pair<Object, Boolean>> getQ() {
        return (Set<Pair<Object, Boolean>>) weakProtocol.getQ().stream().flatMap(s -> Set.of(new Pair<>(s, true), new Pair<>(s, false)).stream()).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<Pair<Object, Boolean>, Pair<Object, Boolean>>> delta(Pair<Object, Boolean> x, Pair<Object, Boolean> y) {
        HashSet<Pair<Pair<Object, Boolean>, Pair<Object, Boolean>>> result = new HashSet<>();
        if (weakProtocol.output(x.getFirst()).isPresent() && weakProtocol.output(y.getFirst()).isEmpty()) {
            result.add(new Pair<>(new Pair<>(x.getFirst(), (Boolean) weakProtocol.output(x.getFirst()).get()), new Pair<>(y.getFirst(), (Boolean) weakProtocol.output(x.getFirst()).get())));
        } else if (weakProtocol.output(y.getFirst()).isPresent() && weakProtocol.output(x.getFirst()).isEmpty()) {
            result.add(new Pair<>(new Pair<>(x.getFirst(), (Boolean) weakProtocol.output(y.getFirst()).get()), new Pair<>(y.getFirst(), (Boolean) weakProtocol.output(y.getFirst()).get())));
        } else if (weakProtocol.output(x.getFirst()).isEmpty() && weakProtocol.output(y.getFirst()).isEmpty() && x.getSecond() != y.getSecond()) {
            result.add(new Pair<>(new Pair<>(x.getFirst(), false), new Pair<>(y.getFirst(), false)));
        }
        result.addAll((Set) weakProtocol.delta(x.getFirst(), y.getFirst()).stream().map(s -> new Pair<>(new Pair<>(((Pair) s).getFirst(), (weakProtocol.output(((Pair) s).getFirst()).isPresent() && (Boolean) weakProtocol.output(((Pair) s).getFirst()).get())), new Pair<>(((Pair) s).getSecond(), (weakProtocol.output(((Pair) s).getSecond()).isPresent() && (Boolean) weakProtocol.output(((Pair) s).getSecond()).get())))).collect(Collectors.toSet()));
        return result;
    }

    @Override
    public Set<Pair<Object, Boolean>> getI() {
        return (Set<Pair<Object, Boolean>>) weakProtocol.getI().stream().map(s -> weakProtocol.output(s).isPresent() ? new Pair<>(s, weakProtocol.output(s).get()) : new Pair<>(s, false)).collect(Collectors.toSet());
    }

    @Override
    public Population<Pair<Object, Boolean>> initializeConfig(BufferedReader r) throws IOException {
        List<Pair<Object, Boolean>> weakPopulation = (List<Pair<Object, Boolean>>) weakProtocol.initializeConfig(r).stream().map(s -> weakProtocol.output(s).isPresent() ? new Pair<>(s, weakProtocol.output(s).get()) : new Pair<>(s, false)).collect(Collectors.toList());
        return new Population<>(weakPopulation.toArray(new Pair[0]));
    }

    @Override
    public boolean output(Pair<Object, Boolean> state) {
        return weakProtocol.output(state.getFirst()).isPresent() ? (boolean) weakProtocol.output(state.getFirst()).get() : state.getSecond();
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Object, Boolean>> config) {
        return weakProtocol.consensus(new Population(config.stream().map(Pair::getFirst).collect(Collectors.toList()).toArray(Object[]::new)));
    }

    private static int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Pair<Object, Boolean> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (countChar(first, '(') - countChar(first, ')') == 0 && countChar(second, '(') - countChar(second, ')') == 0) {
                return new Pair<>(weakProtocol.stateFromString(first), Boolean.parseBoolean(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}