package org.reilichsi.sniper;

import org.reilichsi.Population;

import java.io.PrintStream;

public class NoSniper<T> extends Sniper<T> {

    public NoSniper() {
        super(0);
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim, PrintStream ps) throws InterruptedException {
        return false;
    }
}
