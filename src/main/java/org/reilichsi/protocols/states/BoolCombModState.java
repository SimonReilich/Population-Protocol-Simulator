package org.reilichsi.protocols.states;

import org.reilichsi.protocols.BoolCombModulo;

public class BoolCombModState {

    public final int floor;
    public final int[] storage;
    private final BoolCombModulo protocol;

    public BoolCombModState(BoolCombModulo protocol, int floor, int... storage) {
        this.floor = floor;
        this.storage = storage;
        this.protocol = protocol;
    }
}
