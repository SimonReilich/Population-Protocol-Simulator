package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Optional;
import java.util.Set;

public interface Protocol<T> {

    default String stateToString(T state) {
        return state.toString();
    }

    /**
     * Conventionally evaluates the protocols predicate based on the provided arguments.
     *
     * @param x an array of integers used as input for the predicate
     * @return true if the predicate is met based on the input, false otherwise
     */
    boolean predicate(int... x);

    /**
     * Retrieves the set of all possible states for this protocol.
     *
     * @return a set containing all possible states of type T
     */
    Set<T> getQ();

    /**
     * Retrieves the set of initial states for this protocol.
     *
     * @return a set containing all initial states of type T
     */
    Set<T> getI();

    /**
     * Computes the transition relation of this protocol for the given input states.
     *
     * @param x the first input state
     * @param y the second input state
     * @return a set of all possible successor states of <code>x</code> and <code>y</code>
     */
    Set<Pair<T, T>> delta(T x, T y);

    /**
     * Computes the consensus value of this protocol for the given configuration.
     * The consensus value is the output value of the protocol if all nodes in the configuration
     * agree on the output value, or {@link Optional#empty()} if no consensus is reached.
     *
     * @param config the configuration
     * @return the consensus value of this protocol for the given configuration, or empty if no consensus is reached
     */
    Optional<Boolean> consensus(Population<T> config);

    /**
     * Generates a configuration for the provided arguments.
     *
     * @param x an array of integers used as input for the protocol
     * @return a configuration corresponding to the provided arguments
     */
    Population<T> genConfig(int... x);

    /**
     * Compares two states of type T for equality.
     * This method is used for custom state comparison.
     *
     * @param x the first state
     * @param y the second state
     * @return true if the two states are considered equal, false otherwise
     */
    boolean statesEqual(T x, T y);

    /**
     * Parses a string representation of a state into the corresponding state of type T.
     *
     * @param s a string representation of a state
     * @return the state of type T represented by the string
     * @throws IllegalArgumentException if the string is not a valid representation of a state
     */
    T stateFromString(String s);
}
