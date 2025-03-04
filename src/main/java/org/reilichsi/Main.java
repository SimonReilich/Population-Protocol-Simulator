package org.reilichsi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static PopulationProtocol<String> protocol;
    private static Population<String> config;

    public static void main(String[] args) throws IOException, InterruptedException {

        // File input can be passed as a command line argument
        System.out.println();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        if (args.length < 1) {
            System.out.print("File to read from: ");
            args = new String[]{r.readLine()};
        }

        // Initialize the simulation with the content of the file
        protocol = new FileProtocol(Files.readString(Path.of(args[0])));
        Set<String> initialStates = protocol.getI();
        config = new Population<>();

        // Prompt the user for the number of agents in each initial state
        if (args.length > 1 + initialStates.size()) {
            Iterator<String> iter = initialStates.iterator();
            for (int i = 1; i < args.length; i++) {
                String s = iter.next();
                for (int j = 0; j < Integer.parseInt(args[i]); j++) {
                    config.add(s);
                }
            }
        } else {
            for (String state : initialStates) {
                System.out.print("How many agents in state " + state + "?: ");
                int count = Integer.parseInt(r.readLine());
                for (int i = 0; i < count; i++) {
                    config.add(state);
                }
            }
        }

        boolean randomSniper;
        if (args.length > 1 + initialStates.size() + 1) {
            randomSniper = args[1 + initialStates.size()].equalsIgnoreCase("y");
        } else {
            System.out.print("Random sniper? (y/n): ");
            randomSniper = r.readLine().equalsIgnoreCase("y");
        }
        double snipeRate;
        int maxSnipes;
        if (randomSniper) {
            if (args.length > 1 + initialStates.size() + 3) {
                snipeRate = Double.parseDouble(args[1 + initialStates.size() + 1]);
                maxSnipes = Integer.parseInt(args[2 + initialStates.size() + 2]);
            } else {
                System.out.print("Mean agents killed by the sniper per round: ");
                snipeRate = Double.parseDouble(r.readLine());
                System.out.print("Maximum number of snipes (-1 for no limit): ");
                maxSnipes = Integer.parseInt(r.readLine());
            }
        } else {
            snipeRate = 0.0;
            maxSnipes = 0;
        }

        if (maxSnipes >= config.size()) {
            maxSnipes = config.size() - 1;
        }

        boolean fastSim;
        if ((randomSniper && args.length > 1 + initialStates.size() + 4) || (!randomSniper && args.length > 1 + initialStates.size() + 2)) {
            fastSim = args[args.length - 1].equalsIgnoreCase("y");
        } else {
            System.out.print("Fast simulation? (y/n): ");
            fastSim = r.readLine().equalsIgnoreCase("y");
        }
        System.out.println("\nStarting simulation...\n");
        System.out.print(config.toString());

        // Run the simulation
        while (!protocol.hasConsensus(config)) {
            maxSnipes = simulationStep(fastSim, true, maxSnipes, snipeRate);
        }

        // Print the final configuration
        if (fastSim) {
            System.out.println("\n\nFinal configuration:\n");
        }
        System.out.println("\r" + config.toString());
        System.out.println("\nConsensus reached: " + config.consensus(s -> protocol.output(s)).get());
    }

    public static int simulationStep(boolean fastSim, boolean snipeInNextStep, int maxSnipes, double snipeRate) throws InterruptedException {
        if (maxSnipes != 0 && snipeInNextStep) {
            int toBeSniped = getPoissonRandom(snipeRate);
            for (int i = 0; i < toBeSniped; i++) {
                int index;
                do {
                    index = (int) (Math.random() * config.size());
                } while (!config.isActive(index));
                config.kill(index);
                maxSnipes--;
                if (maxSnipes == 0) {
                    break;
                }
            }
            if (toBeSniped > 0 && !fastSim) {
                Thread.sleep(1000);
                System.out.print("\r" + config.toString());
            }
        }

        // Pick a random pair of agents
        int agent1;
        int agent2;
        do {
            agent1 = (int) (Math.random() * config.size());
        } while (!config.isActive(agent1));
        do {
            agent2 = (int) (Math.random() * config.size());
        } while (agent1 == agent2 || !config.isActive(agent1));
        Pair<String, String> newState = pickRandom(protocol.delta(config.get(agent1), config.get(agent2)));

        if (newState == null) {
            maxSnipes = simulationStep(fastSim, false, maxSnipes, snipeRate);
            return maxSnipes;
        }

        if (!fastSim) {
            Thread.sleep(1000);
            System.out.print("\r" + config.toString(agent1, agent2));
            Thread.sleep(1000);
        }

        // Update the configuration
        config.set(agent1, newState.getFirst());
        config.set(agent2, newState.getSecond());

        if (!fastSim) {
            System.out.print("\r" + config.toString(agent1, agent2));
            Thread.sleep(1000);
            System.out.print("\r" + config.toString());
        }

        return maxSnipes;
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

