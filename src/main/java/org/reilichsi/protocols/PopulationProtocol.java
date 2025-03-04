package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class PopulationProtocol<T> {
    private final Map<Population<T>, Population<T>> reachable;
    private final Map<Population<T>, Optional<Boolean>> ambiguity;

    public PopulationProtocol() {
        reachable = new HashMap<>();
        ambiguity = new HashMap<>();
    }

    public abstract Set<T> getQ();
    public abstract Set<Pair<T, T>> delta (T x, T y);
    public abstract Set<T> getI();
    public abstract Population<T> initializeConfig(BufferedReader r) throws IOException;

    public Sniper<T> initializeSniper(BufferedReader r) throws IOException {
        System.out.print("Random sniper? (y/n): ");
        if (r.readLine().equalsIgnoreCase("y")) {
            RandomSniper<T> sniper = new RandomSniper<>();
            System.out.print("Mean agents killed by the sniper per round: ");
            sniper.setSnipeRate(Double.parseDouble(r.readLine()));
            System.out.print("Maximum number of snipes (-1 for no limit): ");
            sniper.setMaxSnipes(Integer.parseInt(r.readLine()));
            return sniper;
        } else {
            return new NoSniper<>();
        }
    }
    public abstract boolean output(T state);

    public Optional<Boolean> consensus(Population<T> config) {
        if (config.stream().map(this::output).distinct().count() > 1) {
            return Optional.empty();
        } else {
            return config.stream().map(this::output).findFirst();
        }
    }
}
