package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AndProtocol extends PopulationProtocol<Pair<Object, Object>> {

    private PopulationProtocol<Object> protocol1;
    private PopulationProtocol<Object> protocol2;

    public AndProtocol(BufferedReader r) throws IOException {
        super();
        System.out.println("Pick first protocol for conjunction: ");
        protocol1 = PopulationProtocol.getProtocol(r);
        System.out.println("Pick second protocol for conjunction: ");
        protocol2 = PopulationProtocol.getProtocol(r);
    }

    @Override
    public Set<Pair<Object, Object>> getQ() {
        return protocol1.getQ().stream().flatMap(s -> protocol2.getQ().stream().map(t -> new Pair<>(s, t))).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<Pair<Object, Object>, Pair<Object, Object>>> delta(Pair<Object, Object> x, Pair<Object, Object> y) {
        Set<Pair<Object, Object>> delta1 = new HashSet<>(protocol1.delta(x.getFirst(), y.getFirst()));
        Set<Pair<Object, Object>> delta2 = new HashSet<>(protocol2.delta(x.getSecond(), y.getSecond()));

        Set<Pair<Pair<Object, Object>, Pair<Object, Object>>> result = new HashSet<>();
        for (Pair<Object, Object> p1 : delta1) {
            for (Pair<Object, Object> p2 : delta2) {
                result.add(new Pair<>(new Pair<>(p1.getFirst(), p2.getFirst()), new Pair<>(p1.getSecond(), p2.getSecond())));
            }
        }
        if (result.isEmpty()) {
            for (Pair<Object, Object> p1 : delta1) {
                result.add(new Pair<>(new Pair<>(p1.getFirst(), x.getSecond()), new Pair<>(p1.getSecond(), y.getSecond())));
            }
            for (Pair<Object, Object> p2 : delta2) {
                result.add(new Pair<>(new Pair<>(x.getFirst(), p2.getFirst()), new Pair<>(y.getFirst(), p2.getSecond())));
            }
        }
        return result;
    }

    @Override
    public Set<Pair<Object, Object>> getI() {
        return protocol1.getI().stream().flatMap(s -> protocol2.getI().stream().map(t -> new Pair<>(s, t))).collect(Collectors.toSet());
    }

    @Override
    public Population<Pair<Object, Object>> initializeConfig(BufferedReader r) throws IOException {
        Population<Pair<Object, Object>> config = new Population<>();

        // Prompt the user for the number of agents in each initial state
        for (Pair<Object, Object> state : getI()) {
            System.out.print("How many agents in state (" + state.getFirst() + "; " + state.getSecond() + ")?: ");
            int count = Integer.parseInt(r.readLine());
            for (int i = 0; i < count; i++) {
                config.add(state);
            }
        }
        return config;
    }

    @Override
    public boolean output(Pair<Object, Object> state) {
        return protocol1.output(state.getFirst()) && protocol2.output(state.getSecond());
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Object, Object>> config) {
        Population<Object> config1 = new Population<>(config.stream().map(Pair::getFirst).toArray(Object[]::new));
        Population<Object> config2 = new Population<>(config.stream().map(Pair::getSecond).toArray(Object[]::new));
        if (protocol1.consensus(config1).isPresent() && protocol2.consensus(config2).isPresent()) {
            return Optional.of(protocol1.consensus(config1).get() && protocol2.consensus(config2).get());
        } else {
            return Optional.empty();
        }
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
    public Pair<Object, Object> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (countChar(first, '(') - countChar(first, ')') == 0 && countChar(second, '(') - countChar(second, ')') == 0) {
                return new Pair<>(first, second);
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}
