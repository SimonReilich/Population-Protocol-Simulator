package org.reilichsi.sniper;

import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.io.BufferedReader;
import java.io.IOException;

public class MultiSniper<T> extends Sniper<T> {

    private Sniper<T>[] snipers;
    private int maxSnipes;

    public MultiSniper(BufferedReader r, PopulationProtocol<T> protocol) throws IOException {
        System.out.print("Maximum number of snipes (-1 for no limit): ");
        maxSnipes = Integer.parseInt(r.readLine());
        System.out.print("How many snipers: ");
        int count = Integer.parseInt(r.readLine());
        snipers = new Sniper[count];
        for (int i = 0; i < count; i++) {
            snipers[i] = protocol.initializeSniper(r);
        }
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim) throws InterruptedException {

        if (maxSnipes >= config.sizeActive()) {
            maxSnipes = config.sizeActive() - 1;
        }

        boolean out = false;
        for (Sniper<T> sniper : snipers) {
            if (maxSnipes != 0 && sniper.snipe(config, fastSim)) {
                maxSnipes--;
                out = true;
                if (maxSnipes == 0) {
                    break;
                }
            }
        }
        return out;
    }
}
