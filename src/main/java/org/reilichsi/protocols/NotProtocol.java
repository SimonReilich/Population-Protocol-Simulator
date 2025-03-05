package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class NotProtocol extends PopulationProtocol<Object> {

    private PopulationProtocol protocol;

    public NotProtocol(BufferedReader r) throws IOException {
        super();
        System.out.println("Pick a protocol to negate: ");
        protocol = PopulationProtocol.getProtocol(r);
    }

    @Override
    public Set<Object> getQ() {
        return protocol.getQ();
    }

    @Override
    public Set<Pair<Object, Object>> delta(Object x, Object y) {
        return protocol.delta(x, y);
    }

    @Override
    public Set<Object> getI() {
        return protocol.getI();
    }

    @Override
    public Population<Object> initializeConfig(BufferedReader r) throws IOException {
        return protocol.initializeConfig(r);
    }

    @Override
    public boolean output(Object state) {
        return !protocol.output(state);
    }

    @Override
    public Optional<Boolean> consensus(Population<Object> config) {
        if (protocol.consensus(config).isPresent()) {
            return Optional.of(!((Boolean) protocol.consensus(config).get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Object stateFromString(String s) {
        return protocol.stateFromString(s);
    }
}
