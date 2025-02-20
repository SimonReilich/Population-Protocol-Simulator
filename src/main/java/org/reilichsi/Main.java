package org.reilichsi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static PopulationProtocol<String> protoSim;
    private static List<String> config;
    private static boolean[] alive;

    public static void main(String[] args) throws IOException, InterruptedException {

        // File input can be passed as a command line argument
        System.out.println();
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

        alive = new boolean[config.size()];
        Arrays.fill(alive, true);

        System.out.println("Total number of agents: " + config.size());
        System.out.println("\nRandom sniper? (y/n):");
        boolean randomSniper = r.readLine().equalsIgnoreCase("y");
        double snipeRate;
        int maxSnipes;
        if (randomSniper) {
            System.out.println("Mean agents killed by the sniper per round: ");
            snipeRate = Double.parseDouble(r.readLine());
            System.out.println("Maximum number of snipes (-1 for no limit): ");
            maxSnipes = Integer.parseInt(r.readLine());
        } else {
            snipeRate = 0.0;
            maxSnipes = 0;
        }
        System.out.println("Fast simulation? (y/n): ");
        boolean fastSim = r.readLine().equalsIgnoreCase("y");
        System.out.println("\nStarting simulation...\n");
        printConfig();

        // Run the simulation
        while (!protoSim.hasConsensus(config, alive)) {
            simulationStep(fastSim, true, maxSnipes, snipeRate);
        }

        // Print the final configuration
        if (fastSim) {
            System.out.println("\n\nFinal configuration:\n");
        }
        printConfig();
        System.out.println("\n\nConsensus reached: " + protoSim.output(config.getFirst()));
    }

    public static void simulationStep(boolean fastSim, boolean snipeInNextStep, int maxSnipes, double snipeRate) throws InterruptedException {
        if (maxSnipes != 0 && snipeInNextStep) {
            int toBeSniped = getPoissonRandom(snipeRate);
            for (int i = 0; i < toBeSniped; i++) {
                int index;
                do {
                    index = (int) (Math.random() * config.size());
                } while (!alive[index]);
                alive[index] = false;
                maxSnipes--;
                config.set(index, "☠");
                if (maxSnipes == 0) {
                    break;
                }
            }
            if (toBeSniped > 0 && !fastSim) {
                Thread.sleep(1000);
                printConfig();
            }
        }

        // Pick a random pair of agents
        int agent1;
        int agent2;
        do {
            agent1 = (int) (Math.random() * config.size());
        } while (!alive[agent1]);
        do {
            agent2 = (int) (Math.random() * config.size());
        } while (agent1 == agent2 && !alive[agent2]);
        Pair<String, String> newState = pickRandom(protoSim.delta(config.get(agent1), config.get(agent2)));

        if (newState == null) {
            simulationStep(fastSim, false, maxSnipes, snipeRate);
            return;
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

    public static void printConfig(int... selected) {
        StringBuilder sb = new StringBuilder("\r|");
        // len is the maximum length of the strings representing each state
        int len = config.stream().map(String::length).max(Comparator.naturalOrder()).orElse(0);
        for (int i = 0; i < config.size(); i++) {
            // finalI is necessary for lambda capture
            int finalI = i;
            if (selected != null && Arrays.stream(selected).anyMatch(s -> s == finalI)) {
                sb.append("* ").append(extend(config.get(i), len)).append(" *|");
            } else {
                sb.append("  ").append(extend(config.get(i), len)).append("  |");
            }
        }
        System.out.print(sb);
    }

    public static String extend(String str, int len) {
        // extending the string to length len with blanks equally distributed at beginning and end
        if (str.length() > len) {
            throw new RuntimeException("String too short");
        }
        return " ".repeat((len - str.length()) / 2) + str + " ".repeat(Math.ceilDiv(len - str.length(), 2));
    }

    public static Pair<String, String> pickRandom(Set<Pair<String, String>> set) {
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

    // As proposed by D. Knuth (http://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables)
    private static int getPoissonRandom(double mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * Math.random();
            k++;
        } while (p > L);
        return k - 1;
    }
}

