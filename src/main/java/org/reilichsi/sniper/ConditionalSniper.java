package org.reilichsi.sniper;

import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.io.BufferedReader;
import java.io.IOException;

public class ConditionalSniper<T> extends Sniper<T> {

    private Sniper<T> conditional;
    private Sniper<T> effectiveSniper;

    public ConditionalSniper(BufferedReader r, PopulationProtocol<T> protocol) throws IOException {
        System.out.println("Pick first sniper (conditional sniper): ");
        conditional = protocol.initializeSniper(r);
        System.out.println("Pick second sniper (effective sniper): ");
        effectiveSniper = protocol.initializeSniper(r);
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim) throws InterruptedException {
        Population<T> copy = (Population<T>) new Population<>(config.stream().toArray(Object[]::new));
        if (conditional.snipe(copy, fastSim)) {
            return effectiveSniper.snipe(config, fastSim);
        }
        return false;
    }
}
