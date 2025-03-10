package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InhomTower extends PopulationProtocol<Pair<Integer, Integer>> {

    private int[] a;
    private int t;

    public InhomTower(BufferedReader r) throws IOException {
        System.out.print("threshold t: ");
        t = Integer.parseInt(r.readLine());
        System.out.print("How many factors?: ");
        int count = Integer.parseInt(r.readLine());
        a = new int[count];
        for (int i = 0; i < count; i++) {
            System.out.print("Factor " + i + " (a_i > 0): ");
            a[i] = Integer.parseInt(r.readLine());
        }
    }

    @Override
    public Set<Pair<Integer, Integer>> getQ() {
        HashSet<Pair<Integer, Integer>> Q = new HashSet<>();
        for (int ai : a) {
            for (int j = 0; j + ai <= t + 1; j++) {
                Q.add(new Pair<>(j, j + ai));
            }
        }
        return Q;
    }

    @Override
    public Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> delta(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        if (arePairsJoint(x, y) && x.getFirst() <= y.getFirst() && x.getSecond() <= t && y.getSecond() <= t) {
            return Set.of(new Pair<>(x, new Pair<>(y.getFirst() + 1, y.getSecond() + 1)));
        } else if (x.getSecond() == t + 1 && y.getSecond() <= t) {
            return Set.of(new Pair<>(x, new Pair<>(t + 1 - (y.getSecond() - y.getFirst()), t + 1)));
        }
        return Set.of();
    }

    @Override
    public Set<Pair<Integer, Integer>> getI() {
        HashSet<Pair<Integer, Integer>> I = new HashSet<>();
        for (int ai : a) {
            I.add(new Pair<>(0, ai));
        }
        return I;
    }

    @Override
    public Population<Pair<Integer, Integer>> initializeConfig(BufferedReader r) throws IOException {
        Population<Pair<Integer, Integer>> config = new Population<>();
        for (int i = 0; i < a.length; i++) {
            System.out.print("factor for skalar " + a[i] + ": ");
            int count = Integer.parseInt(r.readLine());
            for (int j = 0; j < count; j++) {
                config.add(new Pair<>(0, a[i]));
            }
        }
        return config;
    }

    @Override
    public boolean output(Pair<Integer, Integer> state) {
        return state.getSecond() == t + 1;
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Integer, Integer>> config) {
        if (config.stream().anyMatch(this::output)) {
            return config.stream().filter(this::output).count() < config.sizeActive() ? Optional.empty() : Optional.of(true);
        } else {
            return config.stream().anyMatch(s -> config.stream().filter(s2 -> arePairsJoint(s, s2)).count() > 1) ? Optional.empty() : Optional.of(false);
        }
    }

    private static int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public Pair<Integer, Integer> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (countChar(first, '(') - countChar(first, ')') == 0 && countChar(second, '(') - countChar(second, ')') == 0) {
                return new Pair<>(Integer.parseInt(first), Integer.parseInt(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }

    private static boolean arePairsJoint(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        if (p1.getFirst() < p2.getFirst()) {
            return p1.getSecond() > p2.getFirst();
        } else if (p2.getFirst() < p1.getFirst()) {
            return p2.getSecond() > p1.getFirst();
        } else {
            return true;
        }
    }
}
