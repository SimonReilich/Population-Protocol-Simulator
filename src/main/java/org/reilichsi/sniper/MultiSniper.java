package org.reilichsi.sniper;

import org.reilichsi.Population;

public class MultiSniper<T> extends Sniper<T> {

    private final Sniper<T>[] snipers;

    public MultiSniper(int maxSnipes, Sniper<T>... snipers) {
        super(maxSnipes);
        this.snipers = snipers;
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim) throws InterruptedException {
        super.matchPopulationSize(config);

        boolean out = false;
        for (Sniper<T> sniper : this.snipers) {
            if (super.canSnipe() && sniper.snipe(config, fastSim)) {
                super.decreaseSnipes();
                out = true;
                if (!super.canSnipe()) {
                    break;
                }
            }
        }
        return out;
    }
}
