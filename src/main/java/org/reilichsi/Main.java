package org.reilichsi;

import org.reilichsi.protocols.*;
import org.reilichsi.sniper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static PopulationProtocol protocol;
    private static Population config;
    private static Sniper sniper;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Protocol to simulate? (p for Pebbles, f for file): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        if (protocolCode.equalsIgnoreCase("p")) {
            protocol = new Pebbles(r);
        } else if (protocolCode.equalsIgnoreCase("f")) {
            protocol = new FileProtocol(r);
        }

        config = protocol.initializeConfig(r);
        sniper = protocol.initializeSniper(r);

        boolean fastSim;
        System.out.print("Fast simulation? (y/n): ");
        fastSim = r.readLine().equalsIgnoreCase("y");

        System.out.println("\nStarting simulation...\n");
        System.out.print(config.toString());

        // Run the simulation
        boolean snipeInNextStep = true;
        while (protocol.consensus(config).isEmpty()) {
            snipeInNextStep = simulationStep(fastSim, snipeInNextStep);
        }

        // Print the final configuration
        if (fastSim) {
            System.out.println("\n\nFinal configuration:\n");
        }
        System.out.println("\r" + config.toString());
        System.out.println("\nConsensus reached: " + protocol.consensus(config).get());
    }

    public static boolean simulationStep(boolean fastSim, boolean snipeInNextStep) throws InterruptedException {
        if (snipeInNextStep) {
            sniper.snipe(config, fastSim);
        }

        // Pick a random pair of agents
        int agent1;
        int agent2;
        do {
            agent1 = (int) (Math.random() * config.size());
        } while (!config.isActive(agent1));
        do {
            agent2 = (int) (Math.random() * config.size());
        } while (agent1 == agent2 || !config.isActive(agent2));
        Pair<String, String> newState = pickRandomPair(protocol.delta(config.get(agent1), config.get(agent2)));

        if (newState == null) {
            return false;
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

        return true;
    }

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
}

