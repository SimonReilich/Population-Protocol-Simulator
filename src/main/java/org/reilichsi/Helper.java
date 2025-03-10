package org.reilichsi;

import java.util.Set;

public class Helper {

    public static Pair<String, String> pickRandomPair(Set<Pair<String, String>> set) {
        // randomly pick a pair from the set
        int index = (int) (Math.random() * set.size());
        int i = 0;
        // iterating over the elements of the set until the index is reached
        for (Pair<String, String> p : set) {
            if (i == index) {
                return p;
            }
            i++;
        }
        return null;
    }

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
        if (p1.getFirst() < p2.getFirst()) {
            return p1.getSecond() > p2.getFirst();
        } else if (p2.getFirst() < p1.getFirst()) {
            return p2.getSecond() > p1.getFirst();
        } else {
            return true;
        }
    }
}
