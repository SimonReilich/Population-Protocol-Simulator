package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class PopulationProtocol<T> {

    public abstract Set<T> getQ();
    public abstract Set<Pair<T, T>> delta (T x, T y);
    public abstract Set<T> getI();
    public abstract Population<T> initializeConfig(BufferedReader r) throws IOException;

    public Sniper<T> initializeSniper(BufferedReader r) throws IOException {
        System.out.print("Random sniper? (y/n): ");
        if (r.readLine().equalsIgnoreCase("y")) {
            return new RandomSniper<>(r);
        } else {
            return new NoSniper<>();
        }
    }
    public abstract boolean output(T state);

    public abstract Optional<Boolean> consensus(Population<T> config);
}
