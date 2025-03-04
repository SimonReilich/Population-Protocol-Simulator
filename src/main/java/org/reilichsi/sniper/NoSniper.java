package org.reilichsi.sniper;

import org.reilichsi.Population;

public class NoSniper<T> extends Sniper<T>{

    public NoSniper() {
        super();
    }

    @Override
    public void snipe(Population<T> config, boolean fastSim) throws InterruptedException {
        return;
    }
}
