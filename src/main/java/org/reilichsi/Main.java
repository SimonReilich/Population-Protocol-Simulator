package org.reilichsi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static PopProtoSim protoSim;
    private static List<String> config;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new RuntimeException("Usage: java Main <input-file>");
        }
        protoSim = new PopProtoSim(args[0]);
        Set<String> initialStates = protoSim.getI();
        config = new ArrayList<>();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        for (String state : initialStates) {
            System.out.println("How many agents in state " + state + "?");
            int count = Integer.parseInt(r.readLine());
            for (int i = 0; i < count; i++) {
                config.add(state);
            }
        }

        System.out.println("Total number of agents: " + config.size());
        System.out.println("Starting simulation...");
    }
}

