package org.reilichsi.protocols;

import java.util.Optional;

public abstract class WeakProtocol<T> extends Protocol<T> {

    public WeakProtocol(int ARG_LEN, String PREDICATE) {
        super(ARG_LEN, PREDICATE);
    }

    /**
     * Computes the output value for the given input state.
     *
     * @param state the input state
     * @return the output value for the given state
     */
    public abstract Optional<Integer> O(T state);
}
