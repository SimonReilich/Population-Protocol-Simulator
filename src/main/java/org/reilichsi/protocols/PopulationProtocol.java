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
        System.out.print("Kind of sniper? (r for random, p for percise, n for none): ");
        String sniperCode = r.readLine();
        if (sniperCode.equalsIgnoreCase("y")) {
            return new RandomSniper<>(r);
        } else if (sniperCode.equalsIgnoreCase("p")) {
            return new PerciseSniper<>(r, this);
        } else {
            return new NoSniper<>();
        }
    }
    public abstract boolean output(T state);

    public abstract Optional<Boolean> consensus(Population<T> config);

    public abstract T stateFromString(String s);
}
