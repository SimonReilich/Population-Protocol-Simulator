package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class PopulationProtocol<T> {

    public final int ARG_LEN;
    public Function<Integer, String> PREDICATE;

    public PopulationProtocol(int ARG_LEN, Function<Integer, String> PREDICATE) {
        this.ARG_LEN = ARG_LEN;
        this.PREDICATE = PREDICATE;
    }

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
     * Checks if the number of arguments provided is equal to the number of arguments for the protocol
     */
    public void assertArgLength(int... x) {
        if (x.length != this.ARG_LEN) {
            throw new IllegalArgumentException("The number of arguments must be the same as the number of arguments");
        }
    }

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

        if (sniperCode.equalsIgnoreCase("r")) {
            System.out.print("Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("Average agents deactivated per round: ");
            double snipeRate = Double.parseDouble(r.readLine());
            return new RandomSniper<>(maxSnipes, snipeRate);
        } else if (sniperCode.equalsIgnoreCase("p")) {
            System.out.print("Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("Target: ");
            T target = this.stateFromString(r.readLine());
            return new PerciseSniper<>(maxSnipes, target);
        } else if (sniperCode.equalsIgnoreCase("m")) {
            System.out.print("Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("Number of Snipers: ");
            int count = Integer.parseInt(r.readLine());
            Sniper<T>[] snipers = new Sniper[count];
            for (int i = 0; i < count; i++) {
                snipers[i] = this.initializeSniper(r);
            }
            return new MultiSniper<>(maxSnipes, snipers);
        } else {
            return new NoSniper<>();
        }
    }
}
