package org.reilichsi.sniper;

import org.reilichsi.Population;

import java.io.IOException;
import java.io.PrintStream;

public class PerciseSniper<T> extends Sniper<T> {

    private final T target;

    public PerciseSniper(int maxSnipes, T target) throws IOException {
        super(maxSnipes);
        this.target = target;
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim, PrintStream ps) throws InterruptedException {
        super.matchPopulationSize(config);

        boolean out = false;
        while (super.canSnipe() && config.killState(this.target)) {
            super.decreaseSnipes();
            if (!fastSim) {
                Thread.sleep(1000);
            }
            ps.println("\n" + config);
            out = true;
        }
        return out;
    }
}
