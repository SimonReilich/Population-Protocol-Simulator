package org.reilichsi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Main {
    private static String[] state;
    private static Map<String, String[]> transitions;
    private static Map<String, Boolean> output;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String fileName;
        String[] lines = new String[0];

        if (args.length == 0) {
            System.out.println("File to read from: ");
            fileName = scanner.nextLine();
            System.out.println("\n");
        } else {
            fileName = args[0];
        }

        try {
            Path filePath = Paths.get(fileName);
            lines = Files.readAllLines(filePath).toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PopulationProtocol pp;
        if (lines[0] == "String") {
            pp = PopulationProtocol.createStr(lines);
        } else if (lines[0] == "Integer") {
            pp = PopulationProtocol.createInt(lines);
        } else {
            throw new IllegalArgumentException();
        }

        while (!pp.isDone()) {
            pp.step();
        }
        System.out.println(pp.getConsensus());
    }
}

class PopulationProtocol<T> {
    private T[] initialStates;
    private T[] population;
    private Function<T[], Boolean>[] transitionConditions;
    private Function<T[], T[]>[] transitionResults;
    private Function<T, Boolean> output;

    private PopulationProtocol() {

    }

    public static PopulationProtocol<String> createStr(String[] inputLines) {
        PopulationProtocol<String> pp = new PopulationProtocol<>();
        return pp;
    }

    public static PopulationProtocol<Integer> createInt(String[] inputLines) {
        PopulationProtocol<Integer> pp = new PopulationProtocol<>();
        return pp;
    }

    public void init(T[] population) {
        this.population = population;
    }

    public T[] delta(T[] t) {
        for (int i = 0; i < transitionConditions.length; i++) {
            if (transitionConditions[i].apply(t)) {
                return transitionResults[i].apply(t);
            }
        }
        return t;
    }

    public void step() {
        int agent1 = (int) (Math.random() * population.length);
        int agent2 = (int) (Math.random() * population.length);

        while (agent1 == agent2) {
            agent2 = (int) (Math.random() * population.length);
        }

        T[] newStates = delta(Arrays.copyOf(population, 2));
        newStates[0] = population[agent1];
        newStates[1] = population[agent2];

        newStates = delta(newStates);
        population[agent1] = newStates[0];
        population[agent2] = newStates[1];
    }

    public boolean isDone() {
        return true;
    }

    public boolean getConsensus() {
        return true;
    }
}