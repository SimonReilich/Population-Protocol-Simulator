package org.reilichsi.sniper;

import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.io.PrintStream;

public abstract class Sniper<T> {

    private final PopulationProtocol<T> protocol;

    private int maxSnipes;

    public Sniper(PopulationProtocol<T> protocol, int maxSnipes) {
        this.protocol = protocol;
        this.maxSnipes = maxSnipes;
    }

    public abstract boolean snipe(Population<T> config, boolean fastSim, PrintStream ps) throws InterruptedException;

    public boolean canSnipe() {
        return maxSnipes > 0;
    }

    public void decreaseSnipes() {
        if (this.maxSnipes >= 1) {
            this.maxSnipes--;
        }
    }

    public void matchPopulationSize(Population<T> population) {
        if (population.sizeAll() <= this.maxSnipes || this.maxSnipes < 0) {
            this.maxSnipes = population.sizeAll() - 2;
        }
    }
}
