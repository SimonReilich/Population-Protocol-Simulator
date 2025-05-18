package org.reilichsi.protocols;

import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class PopulationProtocol<T> extends Protocol<T> {

    public PopulationProtocol(int ARG_LEN, String FUNCTION) {
        super(ARG_LEN, FUNCTION);
    }

    /**
     * Computes the output value for the given input state.
     *
     * @param state the input state
     * @return the output value for the given state
     */
    public abstract int O(T state);

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
            T target = this.parseString(r.readLine());
            return new PerciseSniper<>(this, max, target);
        } else if (sniperCode.equalsIgnoreCase("m")) {
            System.out.print("  - Number of Snipers: ");
            int count = Integer.parseInt(r.readLine());
            Sniper<T>[] snipers = new Sniper[count];
            for (int i = 0; i < count; i++) {
                snipers[i] = this.initializeSniperCustomMax(r);
            }
            return new MultiSniper<>(this, max, snipers);
        } else {
            return new NoSniper<>();
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
    public Sniper<T> initializeSniperCustomMax(BufferedReader r) throws IOException {
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
            T target = this.parseString(r.readLine());
            return new PerciseSniper<>(this, maxSnipes, target);
        } else if (sniperCode.equalsIgnoreCase("m")) {
            System.out.print("  - Maximum amount of Snipes: ");
            int maxSnipes = Integer.parseInt(r.readLine());
            System.out.print("  - Number of Snipers: ");
            int count = Integer.parseInt(r.readLine());
            Sniper<T>[] snipers = new Sniper[count];
            for (int i = 0; i < count; i++) {
                snipers[i] = this.initializeSniperCustomMax(r);
            }
            return new MultiSniper<>(this, maxSnipes, snipers);
        } else {
            return new NoSniper<>();
        }
    }
}