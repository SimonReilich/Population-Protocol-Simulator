package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class PopulationProtocol<T> {

    public static PopulationProtocol getProtocol(BufferedReader r) throws IOException {
        System.out.print("Protocol to simulate? (p for Pebbles, t for Tower, i for InhomTower, f for file, a for and, n for negation, w for WeakConvert): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        if (protocolCode.equalsIgnoreCase("p")) {
            return new Pebbles(r);
        } else if (protocolCode.equalsIgnoreCase("t")) {
            return new Tower(r);
        } else if (protocolCode.equalsIgnoreCase("i")) {
            return new InhomTower(r);
        } else if (protocolCode.equalsIgnoreCase("f")) {
            return new FileProtocol(r);
        } else if (protocolCode.equalsIgnoreCase("a")) {
            return new AndProtocol(r);
        } else if (protocolCode.equalsIgnoreCase("n")) {
            return new NotProtocol(r);
        } else if (protocolCode.equalsIgnoreCase("w")) {
            return new WeakConvert(r);
        }
        return null;
    }

    public abstract Set<T> getQ();
    public abstract Set<Pair<T, T>> delta (T x, T y);
    public abstract Set<T> getI();
    public abstract Population<T> initializeConfig(BufferedReader r) throws IOException;

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
    public abstract boolean output(T state);

    public abstract Optional<Boolean> consensus(Population<T> config);

    public abstract T stateFromString(String s);
}
