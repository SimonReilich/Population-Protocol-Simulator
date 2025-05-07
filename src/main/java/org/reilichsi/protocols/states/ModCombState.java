package org.reilichsi.protocols.states;

import org.reilichsi.protocols.robustness.modulo.ModuloCombined;

public class ModCombState {
    public final BigModState bigMod;
    public final Interval inhomTower;
    public final int h;
    private final ModuloCombined protocol;

    public ModCombState(ModuloCombined protocol, Interval inhomTower, int h, BigModState bigMod) {
        this.protocol = protocol;
        this.inhomTower = inhomTower;
        this.h = h;
        this.bigMod = bigMod;

//        if (h < 0 || h > Math.pow(protocol.m, 2) * 3) {
//            throw new IllegalArgumentException("h must be between 0 and " + (Math.pow(protocol.m, 2) * 3) + ", but was " + h);
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ModCombState other) {
            return bigMod.equals(other.bigMod) && inhomTower.equals(other.inhomTower) && h == other.h;
        }
        return false;
    }
}
