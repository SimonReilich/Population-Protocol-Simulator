package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class WeakProtocol<T> {

    public static WeakProtocol getWeakProtocol(BufferedReader r) throws IOException {
        System.out.print("Weak Protocol to simulate? (g for GenMajority): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        if (protocolCode.equalsIgnoreCase("g")) {
            return new GenMajority(r);
        }

        return null;
    }
    public abstract Population<T> initializeConfig(BufferedReader r) throws IOException;

    public abstract Set<T> getQ();
    public abstract Set<Pair<T, T>> delta (T x, T y);
    public abstract Set<T> getI();
    public abstract Optional<Boolean> output(T state);

    public abstract Optional<Boolean> consensus(Population<T> config);
    public abstract T stateFromString(String s);
}
