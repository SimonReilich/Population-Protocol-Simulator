package org.reilichsi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static String[] state;
    private static Map<String, String[]> transitions;
    private static Map<String, Boolean> output;

    public static void main(String[] args) {
        state = new String[]{"Ay", "Ay", "Ay", "Ay", "An", "An", "An", "An"};

        transitions = new HashMap<>();
        transitions.put("Ay,At", new String[]{"Ay", "Py"});
        transitions.put("Ay,An", new String[]{"At", "Pt"});
        transitions.put("At,An", new String[]{"An", "Pn"});
        transitions.put("At,At", new String[]{"At", "Pt"});

        transitions.put("Ay,Pt", new String[]{"Ay", "Py"});
        transitions.put("At,Pn", new String[]{"At", "Pt"});
        transitions.put("An,Py", new String[]{"An", "Pn"});
        transitions.put("Ay,Pn", new String[]{"Ay", "Py"});
        transitions.put("At,Py", new String[]{"At", "Pt"});
        transitions.put("An,Pt", new String[]{"An", "Pn"});

        output = new HashMap<>();
        output.put("Ay", true);
        output.put("At", false);
        output.put("An", false);
        output.put("Py", true);
        output.put("Pt", false);
        output.put("Pn", false);

        printState();

        while (!(Arrays.stream(state).map(s -> output.get(s)).distinct().count() == 1)) {
            int agent1 = (int) (Math.random() * state.length);
            int agent2 = (int) (Math.random() * state.length);

            if (agent1 == agent2) {
                continue;
            }

            String[] transition = transitions.get(state[agent1] + "," + state[agent2]);
            if (transition == null) {
                transition = transitions.get(state[agent2] + "," + state[agent1]);
                if (transition == null) {
                    continue;
                }
            }

            state[agent1] = transition[0];
            state[agent2] = transition[1];
            printState();
        }

        if (output.get(state[0])) {
            System.out.println("The Ninjas agreed to attack!");
        } else {
            System.out.println("The Ninjas did not agree to attack.");
        }
    }

    public static void printState() {
        System.out.println("State: ");
        for (String s : state) {
            System.out.print(s + " ");
        }
        System.out.println("\n");
    }
}