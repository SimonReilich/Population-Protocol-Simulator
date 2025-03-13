package org.reilichsi.sniper;

import org.reilichsi.Population;

import java.io.PrintStream;

public abstract class Sniper<T> {

    private int maxSnipes;

    public Sniper(int maxSnipes) {
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
        if (population.size() <= this.maxSnipes || this.maxSnipes < 0) {
            this.maxSnipes = population.size() - 1;
        }
    }
}
