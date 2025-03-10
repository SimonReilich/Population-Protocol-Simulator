package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InhomTowerCancle extends WeakProtocol<Object> {

    private int t;
    private int T;
    private int[] a;

    public InhomTowerCancle(BufferedReader r) throws IOException {
        System.out.print("threshold t: ");
        t = Integer.parseInt(r.readLine());
        T = t;
        System.out.print("How many factors?: ");
        int count = Integer.parseInt(r.readLine());
        a = new int[count];
        for (int i = 0; i < count; i++) {
            System.out.print("Factor " + i + ": ");
            a[i] = Integer.parseInt(r.readLine());
            T = Math.max(a[i], T);
        }
    }

    @Override
    public Population<Object> initializeConfig(BufferedReader r) throws IOException {
        Population<Object> config = new Population<>();
        for (int ai : a) {
            System.out.print("factor for skalar " + ai + ": ");
            int count = Integer.parseInt(r.readLine());
            if (ai > 0) {
                for (int j = 0; j < count; j++) {
                    config.add(new Pair<>(0, ai));
                }
            } else {
                for (int j = 0; j < count; j++) {
                    config.add(ai);
                }
            }
        }
        return config;
    }

    @Override
    public Set<Object> getQ() {
        Set<Object> set = new HashSet<>();
        for (int i = -Arrays.stream(a).max().getAsInt(); i <= 0; i++) {
            set.add(i);
        }
        for (int i = 0; i <= T; i++) {
            for (int j = i + 1; j <= T; j++) {
                set.add(new Pair<>(i, j));
            }
        }
        return set;
    }

    @Override
    public Set<Pair<Object, Object>> delta(Object x, Object y) {
        if (x instanceof Pair && y instanceof Pair && ((Pair<Integer, Integer>) x).getFirst() <= ((Pair<Integer, Integer>) y).getFirst() && ((Pair<Integer, Integer>) x).getSecond() < T && ((Pair<Integer, Integer>) y).getSecond() < T) {
            return Set.of(new Pair<>(x, new Pair<>(((Pair<Integer, Integer>) y).getFirst() + 1, ((Pair<Integer, Integer>) y).getSecond() + 1)));
        } else if (x instanceof Pair && y instanceof Integer && ((int) y) != 0 && ((Pair<Integer, Integer>) x).getFirst() < ((Pair<Integer, Integer>) x).getSecond()) {
            if (((Pair<Integer, Integer>) x).getFirst() == ((Pair<Integer, Integer>) x).getSecond() - 1) {
                return Set.of(new Pair<>(0, ((int) y) + 1));
            } else {
                return Set.of(new Pair<>(new Pair<>(((Pair<Integer, Integer>) x).getFirst(), ((Pair<Integer, Integer>) x).getSecond() - 1), ((int) y) + 1));
            }
        }
        return Set.of();
    }

    @Override
    public Set<Object> getI() {
        Set<Object> set = new HashSet<>();
        for (int ai : a) {
            set.add(new Pair<>(0, ai));
            set.add(ai);
        }
        return set;
    }

    @Override
    public Optional<Boolean> output(Object state) {
        if (state instanceof Integer && ((int) state) != 0) {
            return Optional.of(false);
        } else if (state instanceof Pair && ((Pair<Integer, Integer>) state).getSecond() >= t) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<Object> config) {
        if (config.stream().filter(s -> s instanceof Integer).anyMatch(s -> ((int) s) != 0)) {
            if (config.stream().anyMatch(s -> s instanceof Pair)) {
                return Optional.empty();
            }
            return Optional.of(false);
        }
        return Optional.of(true);
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

    @Override
    public Object stateFromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
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
    }
}
