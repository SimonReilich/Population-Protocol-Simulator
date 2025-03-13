package org.reilichsi.sniper;

import org.reilichsi.Population;

import java.io.OutputStream;
import java.io.PrintStream;

public class MultiSniper<T> extends Sniper<T> {

    private final Sniper<T>[] snipers;

    public MultiSniper(int maxSnipes, Sniper<T>... snipers) {
        super(maxSnipes);
        this.snipers = snipers;
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim, PrintStream ps) throws InterruptedException {
        super.matchPopulationSize(config);

        boolean out = false;
        for (Sniper<T> sniper : this.snipers) {
            if (super.canSnipe() && sniper.snipe(config, fastSim, new PrintStream(OutputStream.nullOutputStream()))) {
                super.decreaseSnipes();
                out = true;
                if (!super.canSnipe()) {
                    break;
                }
            }
        }
        if (out) {
            ps.println("\n" + config.toString());
        }
        return out;
    }
}
