package org.reilichsi.sniper;

import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.io.BufferedReader;
import java.io.IOException;

public class StackedSniper<T> extends Sniper<T> {

    private Sniper<T> first;
    private Sniper<T> second;

    public StackedSniper(BufferedReader r, PopulationProtocol<T> protocol) throws IOException {
        System.out.println("Pick first sniper (conditional sniper): ");
        first = protocol.initializeSniper(r);
        System.out.println("Pick second sniper (effective sniper): ");
        second = protocol.initializeSniper(r);
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim) throws InterruptedException {
        Population<T> copy = (Population<T>) new Population<>(config.stream().toArray(Object[]::new));
        if (first.snipe(copy, fastSim)) {
            return second.snipe(config, fastSim);
        }
        return false;
    }
}
