package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Optional;

public abstract class Protocol<T> {

    public final int ARG_LEN;
    public String FUNCTION;

    public Protocol(int ARG_LEN, String FUNCTION) {
        this.ARG_LEN = ARG_LEN;
        this.FUNCTION = FUNCTION;
    }

    /**
     * Conventionally evaluates the protocols function based on the provided arguments.
     *
     * @param x an array of integers used as input for the function
     * @return the output of the function
     */
    public abstract int function(int... x);

    /**
     * Maps the variables to states
     *
     * @param x the variable
     * @return initial states of type T corresponding to the variable
     */
    public abstract T I(int x);

    /**
     * Computes the transition relation of this protocol for the given input states.
     *
     * @param x the first input state
     * @param y the second input state
     * @return successor states of <code>x</code> and <code>y</code>
     */
    public abstract Pair<T, T> delta(T x, T y);

    /**
     * Decides if there is an effective transition from <code>x</code> and <code>y</code>
     *
     * @param x the first state
     * @param y the second state
     * @return true if there is a transition using the two states and false otherwise
     */
    public final boolean hasTransition(T x, T y) {
        Pair<T, T> delta = delta(x, y);
        return (statesEqual(x, delta.first()) && statesEqual(delta.second(), y))
                || (statesEqual(x, delta.second()) && statesEqual(delta.first(), y));
    }

    /**
     * Decides if there is an effective transition from <code>config</code>
     *
     * @param config the configuration
     * @return true if there are effective transitions, false if there are only silent transitions
     */

    public final boolean hasTransition(Population<T> config) {
        for (int i = 0; i < config.sizeAll(); i++) {
            for (int j = i + 1; j < config.sizeAll(); j++) {
                if (config.isActive(i) && config.isActive(j)) {
                    if (hasTransition(config.get(i), config.get(j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Generates a configuration for the provided arguments.
     *
     * @param x an array of integers used as input for the protocol
     * @return a configuration corresponding to the provided arguments
     */
    public final Population<T> genConfig(int... x) {
        assert x.length == ARG_LEN;

        Population<T> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i]; j++) {
                config.add(this.I(i));
            }
        }
        return config;
    }

    /**
     * Computes the consensus value of this protocol for the given configuration.
     * The consensus value is the output value of the protocol if all nodes in the configuration
     * agree on the output value, or {@link Optional#empty()} if no consensus is reached.
     *
     * @param config the configuration
     * @return the consensus value of this protocol for the given configuration, or empty if no consensus is reached
     */
    public abstract boolean hasConsensus(Population<T> config);

    /**
     * Compares two states of type T for equality.
     * This method is used for custom state comparison.
     *
     * @param x the first state
     * @param y the second state
     * @return true if the two states are considered equal, false otherwise
     */
    public boolean statesEqual(T x, T y) {
        return x.equals(y);
    }

    /**
     * Converts a state to its String representation
     *
     * @param state the state
     * @return the String representation of the state
     */
    public final String stateToString(T state) {
        return state.toString();
    }

    /**
     * Parses a string representation of a state into the corresponding state of type T.
     *
     * @param s a string representation of a state
     * @return the state of type T represented by the string
     * @throws IllegalArgumentException if the string is not a valid representation of a state
     */
    public abstract T parseString(String s);
}
