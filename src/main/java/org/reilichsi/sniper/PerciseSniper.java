package org.reilichsi.sniper;

import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.io.BufferedReader;
import java.io.IOException;

public class PerciseSniper<T> extends Sniper<T> {

    private T target;
    private int maxSnipes;

    public PerciseSniper(BufferedReader r, PopulationProtocol<T> protocol) throws IOException {
        super();
        System.out.print("Target state: ");
        target = protocol.stateFromString(r.readLine());
        System.out.print("Maximum number of snipes (-1 for no limit): ");
        maxSnipes = Integer.parseInt(r.readLine());
    }

    @Override
    public void snipe(Population<T> config, boolean fastSim) throws InterruptedException {

        if (maxSnipes >= config.sizeActive()) {
            maxSnipes = config.sizeActive() - 1;
        }

        while (maxSnipes != 0 &&config.killState(target)) {
            maxSnipes--;
        }
    }
}
