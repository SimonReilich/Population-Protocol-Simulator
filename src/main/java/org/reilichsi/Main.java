package org.reilichsi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static PopProtoSim protoSim;
    private static List<String> config;

    public static void main(String[] args) throws IOException, InterruptedException {

        // File input can be passed as a command line argument
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        if (args.length < 1) {
            System.out.println("File to read from: ");
            args = new String[]{r.readLine()};
        }

        // Initialize the simulation with the content of the file
        protoSim = new PopProtoSim(Files.readString(Path.of(args[0])));
        Set<String> initialStates = protoSim.getI();
        config = new ArrayList<>();

        // Prompt the user for the number of agents in each initial state
        for (String state : initialStates) {
            System.out.println("How many agents in state " + state + "?");
            int count = Integer.parseInt(r.readLine());
            for (int i = 0; i < count; i++) {
                config.add(state);
            }
        }

        System.out.println("Total number of agents: " + config.size());
        System.out.println("\nFast simulation? (y/n): ");
        boolean fastSim = r.readLine().equals("y");
        System.out.println("\nStarting simulation...");
        printConfig();

        // Run the simulation
        while (!protoSim.hasConsensus(config)) {

            // Pick a random pair of agents
            int agent1 = (int) (Math.random() * config.size());
            int agent2 = 0;
            do {
                agent2 = (int) (Math.random() * config.size());
            } while (agent1 == agent2);
            Pair<String> newState = pickRandom(protoSim.delta(config.get(agent1), config.get(agent2)));

            if (newState == null) {
                continue;
            }

            if (!fastSim) {
                Thread.sleep(1000);
                printConfig(agent1, agent2);
                Thread.sleep(1000);
            }

            // Update the configuration
            config.set(agent1, newState.getFirst());
            config.set(agent2, newState.getSecond());

            if (!fastSim) {
                printConfig(agent1, agent2);
                Thread.sleep(1000);
                printConfig();
            }
        }

        // Print the final configuration
        printConfig();
        System.out.println("\nConsensus reached!: " + protoSim.output(config.getFirst()));
    }

    public static void printConfig(int... selected) {
        StringBuilder sb = new StringBuilder("\n|");
        // len is the maximum length of the strings representing each state
        int len = config.stream().map(String::length).max(Comparator.naturalOrder()).orElse(0);
        for (int i = 0; i < config.size(); i++) {
            // finalI is necessary for lambda capture
            int finalI = i;
            if (selected != null && Arrays.stream(selected).anyMatch(s -> s == finalI)) {
                sb.append(" * ").append(extend(config.get(i), len)).append(" * |");
            } else {
                sb.append("   ").append(extend(config.get(i), len)).append("   |");
            }
        }
        System.out.println(sb);
    }

    public static String extend(String str, int len) {
        // extending the string to length len with blanks equally distributed at beginning and end
        if (str.length() > len) {
            throw new RuntimeException("String too short");
        }
        return " ".repeat((len - str.length()) / 2) + str + " ".repeat(len - str.length() - ((len - str.length()) / 2));
    }

    public static Pair<String> pickRandom(Set<Pair<String>> set) {
        // randomly pick a pair from the set
        int index = (int) (Math.random() * set.size());
        int i = 0;
        // iterating over the elements of the set until the index is reached
        for (Pair<String> p : set) {
            if (i == index) {
                return p;
            }
            i++;
        }
        return null;
    }
}

