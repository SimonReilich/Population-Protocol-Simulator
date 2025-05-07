package org.reilichsi.protocols.states;

import org.reilichsi.protocols.robustness.modulo.BigModulo;

import java.util.Arrays;

public class BigModState {

    public final int level;
    public final int[] tokens;
    public final boolean[] result;

    public final BigModulo protocol;

    public BigModState(BigModulo protocol, int level, int[] tokens, boolean[] result) {
        this.level = level;
        this.tokens = tokens;
        this.result = result;

        this.protocol = protocol;

        if (level < 0 || level > protocol.m * 2) {
            throw new IllegalArgumentException("level must be between 0 and " + (protocol.m * 2));
        } else if (tokens.length != protocol.m * 2) {
            throw new IllegalArgumentException("tokens.length must be " + (protocol.m * 2));
        } else if (result.length != protocol.m * 2) {
            throw new IllegalArgumentException("result length must be " + (protocol.m * 2));
        } else if (!Arrays.stream(tokens).allMatch(i -> i >= 0 && i < protocol.m)) {
            throw new IllegalArgumentException("tokens must be between 0 and " + (protocol.m * 2));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BigModState other) {
            return level == other.level && Arrays.equals(tokens, other.tokens) && Arrays.equals(result, other.result);
        }
        return false;
    }
}
