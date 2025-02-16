package org.reilichsi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            pp = PopulationProtocol.createStr(Arrays.copyOfRange(lines, 1, lines.length));
        } else if (lines[0] == "Integer") {
            pp = PopulationProtocol.createInt(Arrays.copyOfRange(lines, 1, lines.length));
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
    private ArrayList<Function<T[], Boolean>> transitionConditions;
    private ArrayList<Function<T[], T[]>> transitionResults;
    private Function<T, Boolean> output;

    private PopulationProtocol() {

    }

    public static PopulationProtocol<String> createStr(String[] inputLines) {
        PopulationProtocol<String> pp = new PopulationProtocol<>();
        pp.initialStates = Arrays.stream(inputLines[0].split(",")).map(String::strip).toArray(String[]::new);
        int startAccept = 0;
        for (int i = 1; i < inputLines.length; i++) {
            if (inputLines[i].startsWith("accept")) {
                startAccept = i;
                break;
            }
            String[][] rule = Arrays.stream(inputLines[i].split("->")).map(String::strip).map(s -> Arrays.stream(s.split(",")).map(String::strip)).toArray(String[][]::new);
            pp.transitionConditions.add(t -> t[0].equals(rule[0][0]) && t[1].equals(rule[0][1]));
            pp.transitionResults.add(t -> new String[]{rule[1][0], rule[1][1]});
        }
        pp.init();
        return pp;
    }

    public static PopulationProtocol<Integer> createInt(String[] inputLines) {
        PopulationProtocol<Integer> pp = new PopulationProtocol<>();
        pp.initialStates = Arrays.stream(inputLines[0].split(",")).map(Integer::valueOf).toArray(Integer[]::new);
        int startAccept = 0;
        for (int i = 1; i < inputLines.length; i++) {
            if (inputLines[i].startsWith("accept")) {
                startAccept = i;
                break;
            }
            Integer[][] rule = Arrays.stream(inputLines[i].split("->")).map(String::strip).map(s -> Arrays.stream(s.split(",")).map(Integer::valueOf)).toArray(Integer[][]::new);
            pp.transitionConditions.add(t -> t[0] == rule[0][0] && t[1] == rule[0][1]);
            pp.transitionResults.add(t -> new Integer[]{rule[1][0], rule[1][1]});
        }
        pp.init();
        return pp;
    }

    public void init() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<T> list = new ArrayList<>();
        for (T initialState : initialStates) {
            System.out.println("number of agents of state" + initialState + ": ");
            int numberOfAgents = scanner.nextInt();
            for (int j = 0; j < numberOfAgents; j++) {
                list.add(initialState);
            }
        }
        this.population = list.toArray((T[])(new Object[list.size()]));
    }

    public T[] delta(T[] t) {
        for (int i = 0; i < transitionConditions.size(); i++) {
            if (transitionConditions.get(i).apply(t)) {
                return transitionResults.get(i).apply(t);
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
        boolean out = getConsensus();
        for (T t : population) {
            if (output.apply(t) != out) {
                return false;
            }
        }
        return true;
    }

    public boolean getConsensus() {
        return output.apply(population[0]);
    }
}