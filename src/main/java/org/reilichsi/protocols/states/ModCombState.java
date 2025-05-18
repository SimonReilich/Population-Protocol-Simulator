package org.reilichsi.protocols.states;

public record ModCombState(Interval inhomTower, int h, BigModState bigMod) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof ModCombState(Interval tower, int h1, BigModState mod)) {
            return bigMod.equals(mod) && inhomTower.equals(tower) && h == h1;
        }
        return false;
    }
}
