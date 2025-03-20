package org.reilichsi.protocols;

public interface Protocol<T> {

    public default String stateToString(T state) {
        return state.toString();
    }

    /**
     * Parses a string representation of a state into the corresponding state of type T.
     *
     * @param s a string representation of a state
     * @return the state of type T represented by the string
     * @throws IllegalArgumentException if the string is not a valid representation of a state
     */
    public abstract T stateFromString(String s);
}
