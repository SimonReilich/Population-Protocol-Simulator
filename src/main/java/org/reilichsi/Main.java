package org.reilichsi;

import org.reilichsi.predicates.BooleanCombination;
import org.reilichsi.predicates.PressburgerPred;
import org.reilichsi.predicates.UnaryThresholdPred;
import org.reilichsi.protocols.*;
import org.reilichsi.sniper.Sniper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {
    private static PopulationProtocol protocol;
    private static Population config;
    private static Sniper sniper;
    private static PrintStream ps;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Choose protocol");
        protocol = getProtocol(r);

        System.out.println("• Protocol is computing the following predicate: " + protocol.PREDICATE.apply(0));
        int[] x = new int[protocol.ARG_LEN];

        for (int i = 0; i < x.length; i++) {
            System.out.print("  - x_" + i + ": ");
            x[i] = Integer.parseInt(r.readLine());
        }

        config = protocol.genConfig(x);

        int inTol = config.size() - 1;
        if (!(protocol instanceof FileProtocol)) {
            inTol = calculateInTol(x);
            System.out.println("• Protocol with this input has the following initial tolerance: " + inTol);
        }

        System.out.println("\nInitializing sniper");
        sniper = protocol.initializeSniper(r, Math.min(inTol, config.size() - 2));

        System.out.println("\nInitializing simulation");
        boolean fastSim;
        System.out.print("• simulation speed (s for slow, i for instant, f for file): ");
        String simCode = r.readLine();
        fastSim = !simCode.equalsIgnoreCase("s");
        if (simCode.equalsIgnoreCase("f")) {
            System.out.print("  - Outputfile: ");
            String outFile = r.readLine();
            ps = new PrintStream(new FileOutputStream(outFile));
        } else {
            ps = System.out;
        }

        ps.println("\nInput: " + Arrays.toString(x));
        if (!(protocol instanceof FileProtocol)) {
            ps.println("Expected output: " + protocol.predicate(x));
        }

        System.out.println("\nStarting simulation...\n");
        ps.println(config.toString());

        // Run the simulation
        boolean snipeInNextStep = true;
        while (protocol.consensus(config).isEmpty()) {
            snipeInNextStep = simulationStep(fastSim, snipeInNextStep);
        }

        // Print the final configuration
        ps.println("\n" + config.toString());
        ps.print("\nConsensus reached: " + protocol.consensus(config).get() + ", expected " + protocol.predicate(x));
        if (ps != System.out) {
            ps.close();
            System.out.println("Done");
        } else {
        System.out.println(", Done");
        }
    }

    public static boolean simulationStep(boolean fastSim, boolean snipeInNextStep) throws InterruptedException {
        if (snipeInNextStep) {
            sniper.snipe(config, fastSim, ps);
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
        Pair<String, String> newState = Helper.pickRandomPair(protocol.delta(config.get(agent1), config.get(agent2)));

        if (newState == null) {
            return false;
        }

        if (!fastSim) {
            Thread.sleep(1000);
        }
        ps.println("\n" + config.toString(agent1, agent2));
        if (!fastSim) {
            Thread.sleep(1000);
        }

        // Update the configuration
        config.set(agent1, newState.first());
        config.set(agent2, newState.second());

        ps.println("\n" + config.toString(agent1, agent2));
        if (!fastSim) {
            Thread.sleep(1000);
        }
        ps.println("\n" + config.toString());

        return true;
    }

    public static int calculateInTol(int[] x) {
        boolean value = protocol.predicate(x);
        for (int i = 1; i <= Arrays.stream(x).sum(); i++) {
            if (Helper.getSub(x, i).stream().anyMatch(c -> protocol.predicate(c) != value)) {
                return i - 1;
            }
        }
        return Arrays.stream(x).sum();
    }

    public static PopulationProtocol getProtocol(BufferedReader r) throws IOException {
        System.out.print("• Protocol to simulate? (p for Pebbles, t for Tower, i for InhomTower, b for BoolCombThreshold, s for SignedNumbers, l for BigModulo, c for ModuloCombined, f for file, a for and, o for or, n for negation, w for WeakConvert): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        if (protocolCode.equalsIgnoreCase("p")) {
            System.out.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            return new Pebbles(t);
        } else if (protocolCode.equalsIgnoreCase("t")) {
            System.out.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            return new Tower(t);
        } else if (protocolCode.equalsIgnoreCase("i")) {
            System.out.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            System.out.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                System.out.print("  - a_" + i + " (in N): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new InhomTower(t, a);
        } else if (protocolCode.equalsIgnoreCase("b")) {
            return new BoolCombThreshold(new BooleanCombination<>(new BooleanCombination<>(new UnaryThresholdPred(10, PressburgerPred.UB, 1, 1)), PressburgerPred.AND, new BooleanCombination<>(new UnaryThresholdPred(5, PressburgerPred.LB, 1, 1))));
        } else if (protocolCode.equalsIgnoreCase("s")) {
            return new SignedNumbers();
        } else if (protocolCode.equalsIgnoreCase("l")) {
            System.out.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            System.out.print("  - Modulus m (m > " + t + "): ");
            int m = Integer.parseInt(r.readLine());
            System.out.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                System.out.print("  - a_" + i + " (in N+): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new BigModulo(t, m, a);
        } else if (protocolCode.equalsIgnoreCase("c")) {
            System.out.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            System.out.print("  - Modulus m (m > " + t + "): ");
            int m = Integer.parseInt(r.readLine());
            System.out.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                System.out.print("  - a_" + i + " (in N+): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new ModuloCombined(t, m, a);
        } else if (protocolCode.equalsIgnoreCase("f")) {
            System.out.print("  - File to read from: ");
            String file = r.readLine();
            return new FileProtocol(Files.readString(Path.of(file)).replace(" ", "").replace("\n", "").replace("\r", "").split(";"));
        } else if (protocolCode.equalsIgnoreCase("a")) {
            return new AndProtocol(getProtocol(r), getProtocol(r));
        } else if (protocolCode.equalsIgnoreCase("o")) {
            return new OrProtocol(getProtocol(r), getProtocol(r));
        } else if (protocolCode.equalsIgnoreCase("n")) {
            return new NotProtocol(getProtocol(r));
        } else if (protocolCode.equalsIgnoreCase("w")) {
            return new WeakConvert(getWeakProtocol(r));
        }
        throw new IllegalArgumentException("No such Protocol");
    }

    public static WeakProtocol getWeakProtocol(BufferedReader r) throws IOException {
        System.out.print("• Weak Protocol to simulate? (g for GenMajority, i for InhomTowerCancle): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        if (protocolCode.equalsIgnoreCase("g")) {
            System.out.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                System.out.print("  - a_" + i + " (in Z): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new GenMajority(a);
        } else if (protocolCode.equalsIgnoreCase("i")) {
            System.out.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            System.out.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                System.out.print("  - a_" + i + " (in Z): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new InhomTowerCancle(t, a);
        }
        throw new IllegalArgumentException("No such weak protocol");
    }
}

