package org.reilichsi.sniper;

import org.reilichsi.Population;

public abstract class Sniper<T> {
    public abstract boolean snipe(Population<T> config, boolean fastSim) throws InterruptedException;
}
