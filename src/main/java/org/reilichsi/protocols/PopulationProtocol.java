package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class PopulationProtocol<T> {

    public int ARG_LEN;
    public String PREDICATE;

    /**
     * Conventionally evaluates the protocols predicate based on the provided arguments.
     *
     * @param x an array of integers used as input for the predicate
     * @return true if the predicate is met based on the input, false otherwise
     */
    public abstract boolean predicate(int... x);

    /**
     * Retrieves the set of all possible states for this protocol.
     *
     * @return a set containing all possible states of type T
     */
    public abstract Set<T> getQ();

    /**
     * Retrieves the set of initial states for this protocol.
     *
     * @return a set containing all initial states of type T
     */
    public abstract Set<T> getI();

    /**
     * Computes the transition relation of this protocol for the given input states.
     *
     * @param x the first input state
     * @param y the second input state
     * @return a set of all possible successor states of <code>x</code> and <code>y</code>
     */
    public abstract Set<Pair<T, T>> delta(T x, T y);

    /**
     * Computes the output value for the given input state.
     *
     * @param state the input state
     * @return the output value for the given state
     */
    public abstract boolean output(T state);

    /**
     * Computes the consensus value of this protocol for the given configuration.
     * The consensus value is the output value of the protocol if all nodes in the configuration
     * agree on the output value, or {@link Optional#empty()} if no consensus is reached.
     *
     * @param config the configuration
     * @return the consensus value of this protocol for the given configuration, or empty if no consensus is reached
     */
    public abstract Optional<Boolean> consensus(Population<T> config);

    /**
     * Generates a configuration for the provided arguments.
     *
     * @param x an array of integers used as input for the protocol
     * @return a configuration corresponding to the provided arguments
     */
    public abstract Population<T> genConfig(int... x);

    /**
     * Parses a string representation of a state into the corresponding state of type T.
     *
     * @param s a string representation of a state
     * @return the state of type T represented by the string
     * @throws IllegalArgumentException if the string is not a valid representation of a state
     */
    public abstract T stateFromString(String s);

    /**
     * Initializes a sniper based on user input.
     * Prompts the user to select the type of sniper to initialize: Random, Precise, Multi, or None.
     *
     * @param r a BufferedReader to read user input
     * @return an instance of Sniper<T> corresponding to the user's input
     * @throws IOException if an I/O error occurs while reading input
     */
    public Sniper<T> initializeSniper(BufferedReader r) throws IOException {
        System.out.print("Kind of sniper? (r for random, p for percise, m for multi, n for none): ");
        String sniperCode = r.readLine();
        if (sniperCode.equalsIgnoreCase("y")) {
            return new RandomSniper<>(r);
        } else if (sniperCode.equalsIgnoreCase("p")) {
            return new PerciseSniper<>(r, this);
        } else if (sniperCode.equalsIgnoreCase("m")) {
            return new MultiSniper<>(r, this);
        } else {
            return new NoSniper<>();
        }
    }
}
