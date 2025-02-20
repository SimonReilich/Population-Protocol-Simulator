package org.reilichsi;

public class Generator {
    public static void main(String[] args) {
        Tower(32);
    }

    public static void Pebbles(int t) {
        for (int i = 0; i < t; i++) {
            System.out.print(i + ", ");
        }
        System.out.println(t + ";");
        System.out.println(1 + ";");
        System.out.println(t + ";");
        for (int i = 0; i <= t; i++) {
            for (int j = i; j <= t; j++) {
                if (i + j < t) {
                    System.out.println(j + ", " + i + " -> " + (i + j) + ", 0;");
                } else {
                    System.out.println(i + ", " + j + " -> " + t + ", " + t + ";");
                }
            }
        }
    }

    public static void Tower(int t) {
        for (int i = 0; i < t; i++) {
            System.out.print(i + ", ");
        }
        System.out.println(t + ";");
        System.out.println(1 + ";");
        System.out.println(t + ";");
        for (int i = 0; i <= t; i++) {
            for (int j = i; j <= t; j++) {
                if (i == j && i < t) {
                    System.out.println(j + ", " + i + " -> " + (j + 1) + ", " + i + ";");
                } else if (i == t || j == t) {
                    System.out.println(j + ", " + i + " -> " + t + ", " + t + ";");
                }
            }
        }
    }
}
