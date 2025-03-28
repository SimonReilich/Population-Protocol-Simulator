package org.reilichsi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Helper {

    public static int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static boolean arePairsJoint(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        if (p1.first() < p2.first()) {
            return p1.second() > p2.first();
        } else if (p2.first() < p1.first()) {
            return p2.second() > p1.first();
        } else {
            return true;
        }
    }

    public static Set<int[]> getSub(int[] x, int n) {
        if (Arrays.stream(x).allMatch(a -> a == 0)) {
            return Set.of(x);
        } else if (n > 0) {
            Set<int[]> result = new HashSet<>();
            for (int i = 0; i < x.length; i++) {
                int[] xMod = Arrays.copyOf(x, x.length);
                if (xMod[i] > 0) {
                    xMod[i]--;
                }
                result.addAll(getSub(xMod, n - 1));
            }
            return result;
        } else {
            return Set.of(x);
        }
    }
}
