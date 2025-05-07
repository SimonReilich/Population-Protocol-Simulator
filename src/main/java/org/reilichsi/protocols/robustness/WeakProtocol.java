package org.reilichsi.protocols.robustness;

import org.reilichsi.protocols.Protocol;

import java.util.Optional;

public abstract class WeakProtocol<T> implements Protocol<T> {

    public final int ARG_LEN;
    public String PREDICATE;

    public WeakProtocol(int ARG_LEN, String PREDICATE) {
        this.ARG_LEN = ARG_LEN;
        this.PREDICATE = PREDICATE;
    }

    /**
     * Computes the output value for the given input state.
     *
     * @param state the input state
     * @return the output value for the given state
     */
    public abstract Optional<Boolean> output(T state);

    /**
     * Checks if the number of arguments provided is equal to the number of arguments for the protocol
     */
    public void assertArgLength(int... x) {
        if (x.length != this.ARG_LEN) {
            throw new IllegalArgumentException("The number of arguments must be the same as the number of arguments");
        }
    }
}
