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
