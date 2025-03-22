package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class PopulationProtocol<T> implements Protocol<T> {

    public final int ARG_LEN;
    public Function<Integer, String> PREDICATE;
    private Set<Population<T>> visited;

    public PopulationProtocol(int ARG_LEN, Function<Integer, String> PREDICATE) {
        this.ARG_LEN = ARG_LEN;
        this.PREDICATE = PREDICATE;
    }

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
    public Optional<Boolean> consensus(Population<T> config) {
        visited = new HashSet<>();
        if (config.stream().allMatch(this::output) && this.checkStableTrue(config)) {
            return Optional.of(true);
        } else if (config.stream().noneMatch(this::output) && this.checkStableFalse(config)) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    private boolean checkStableTrue(Population<T> config) {
        if (visited.contains(config)) {
            return true;
        } else {
            if (config.stream().allMatch(this::output)) {
                visited.add(config);
                for (int i = 0; i < config.size(); i++) {
                    for (int j = i + 1; j < config.size(); j++) {
                        Set<Pair<T, T>> delta = this.delta(config.get(i), config.get(j));
                        int finalI = i;
                        int finalJ = j;
                        if (delta.stream().anyMatch(p -> {
                            Population<T> newConfig = new Population<>(this);
                            newConfig.set(finalI, p.first());
                            newConfig.set(finalJ, p.second());
                            return !this.checkStableTrue(newConfig);
                        })) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkStableFalse(Population<T> config) {
        if (visited.contains(config)) {
            return true;
        } else {
            if (config.stream().noneMatch(this::output)) {
                visited.add(config);
                for (int i = 0; i < config.size(); i++) {
                    for (int j = i + 1; j < config.size(); j++) {
                        Set<Pair<T, T>> delta = this.delta(config.get(i), config.get(j));
                        int finalI = i;
                        int finalJ = j;
                        if (delta.stream().anyMatch(p -> {
                            Population<T> newConfig = new Population<>(this);
                            for (int k = 0; k < config.size(); k++) {
                                newConfig.add(config.get(k));
                            }
                            newConfig.set(finalI, p.first());
                            newConfig.set(finalJ, p.second());
                            return !this.checkStableFalse(newConfig);
                        })) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Checks if the number of arguments provided is equal to the number of arguments for the protocol
     */
    public void assertArgLength(int... x) {
        if (x.length != this.ARG_LEN) {
            throw new IllegalArgumentException("The number of arguments must be the same as the number of arguments");
        }
    }

    /**
     * Initializes a sniper based on user input.
     * Prompts the user to select the type of sniper to initialize: Random, Precise, Multi, or None.
     *
     * @param r a BufferedReader to read user input
     * @return an instance of Sniper<T> corresponding to the user's input
     * @throws IOException if an I/O error occurs while reading input
     */
    public Sniper<T> initializeSniper(BufferedReader r) throws IOException {
        System.out.print("• Kind of sniper? (r for random, p for percise, m for multi, n for none): ");
        String sniperCode = r.readLine();

        if (sniperCode.equalsIgnoreCase("r")) {
            System.out.print("  - Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("  - Average agents deactivated per round: ");
            double snipeRate = Double.parseDouble(r.readLine());
            return new RandomSniper<>(this, maxSnipes, snipeRate);
        } else if (sniperCode.equalsIgnoreCase("p")) {
            System.out.print("  - Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("  - Target: ");
            T target = this.stateFromString(r.readLine());
            return new PerciseSniper<>(this, maxSnipes, target);
        } else if (sniperCode.equalsIgnoreCase("m")) {
            System.out.print("  - Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("  - Number of Snipers: ");
            int count = Integer.parseInt(r.readLine());
            Sniper<T>[] snipers = new Sniper[count];
            for (int i = 0; i < count; i++) {
                snipers[i] = this.initializeSniper(r);
            }
            return new MultiSniper<>(this, maxSnipes, snipers);
        } else {
            return new NoSniper<>();
        }
    }

    /**
     * Initializes a sniper based on user input.
     * Prompts the user to select the type of sniper to initialize: Random, Precise, Multi, or None.
     *
     * @param r   a BufferedReader to read user input
     * @param max the maximum amount of snipes
     * @return an instance of Sniper<T> corresponding to the user's input
     * @throws IOException if an I/O error occurs while reading input
     */
    public Sniper<T> initializeSniper(BufferedReader r, int max) throws IOException {
        System.out.print("• Kind of sniper? (r for random, p for percise, m for multi, n for none): ");
        String sniperCode = r.readLine();

        if (sniperCode.equalsIgnoreCase("r")) {
            System.out.print("  - Average agents deactivated per round: ");
            double snipeRate = Double.parseDouble(r.readLine());
            return new RandomSniper<>(this, max, snipeRate);
        } else if (sniperCode.equalsIgnoreCase("p")) {
            System.out.print("  - Target: ");
            T target = this.stateFromString(r.readLine());
            return new PerciseSniper<>(this, max, target);
        } else if (sniperCode.equalsIgnoreCase("m")) {
            System.out.print("  - Number of Snipers: ");
            int count = Integer.parseInt(r.readLine());
            Sniper<T>[] snipers = new Sniper[count];
            for (int i = 0; i < count; i++) {
                snipers[i] = this.initializeSniper(r);
            }
            return new MultiSniper<>(this, max, snipers);
        } else {
            return new NoSniper<>();
        }
    }
}