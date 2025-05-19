package org.reilichsi;

import org.reilichsi.protocols.*;
import org.reilichsi.protocols.monadic.UnaryThreshold;
import org.reilichsi.protocols.robustness.*;
import org.reilichsi.protocols.robustness.modulo.BigModulo;
import org.reilichsi.protocols.robustness.modulo.ModuloCombined;
import org.reilichsi.protocols.robustness.threshold.*;
import org.reilichsi.sniper.Sniper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

public class Simulator<T> {

    private final PopulationProtocol<T> protocol;
    private final PrintStream info;
    private PrintStream output;
    private Population<T> config;
    private Sniper<T> sniper;
    public Simulator(PopulationProtocol<T> protocol, Population<T> config, Sniper<T> sniper, PrintStream output, PrintStream info) {
        this.protocol = protocol;
        this.info = info;
        this.output = output;
        this.config = config;
        this.sniper = sniper;
    }

    private Simulator(PopulationProtocol<T> protocol) {
        this.protocol = protocol;
        this.info = System.out;
    }

    public void setConfig(Population<T> config) {
        this.config = config;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Simulator<?> simulator = init();
        Pair<int[], Boolean> setup = simulator.setup();
        simulator.simulate(setup.first(), setup.second());
    }

    public static Simulator<?> init() throws IOException {
        System.out.println();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        return new Simulator<>(getProtocol(r, System.out));
    }

    private static PopulationProtocol<?> getProtocol(BufferedReader r, PrintStream info) throws IOException {
        info.println("Choose protocol");
        info.print("• Protocol to simulate? (p for Pebbles, t for Tower, i for InhomTower, s for SignedNumbers, l for BigModulo, c for ModuloCombined, f for file, a for and, o for or, n for negation, w for WeakConvert): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        int t, m, count;
        int[] a;

        switch (protocolCode) {
            case "p", "P", "pebbles", "Pebbles":
                info.print("  - Threshold t (t >= 1): ");
                t = Integer.parseInt(r.readLine());
                return new Pebbles(t);
            case "t", "T", "tower", "Tower":
                info.print("  - Threshold t (t >= 1): ");
                t = Integer.parseInt(r.readLine());
                return new Tower(t);
            case "i", "I", "inhomtower", "Inhomtower", "InhomTower":
                info.print("  - Threshold t (t >= 1): ");
                t = Integer.parseInt(r.readLine());
                info.print("  - Number of Dimensions: ");
                count = Integer.parseInt(r.readLine());
                a = new int[count];
                for (int i = 0; i < a.length; i++) {
                    info.print("  - a_" + i + " (in N): ");
                    a[i] = Integer.parseInt(r.readLine());
                }
                return new InhomTower(t, a);
            case "s", "S", "signednumbers", "Signednumbers", "SignedNumbers":
                return new SignedNumbers();
            case "l", "L", "bigmod", "bigmodulo", "Bigmod", "BigMod", "Bigmodulo", "BigModulo":
                info.print("  - Threshold t (t >= 1): ");
                t = Integer.parseInt(r.readLine());
                info.print("  - Modulus m (m > " + t + "): ");
                m = Integer.parseInt(r.readLine());
                info.print("  - Number of Dimensions: ");
                count = Integer.parseInt(r.readLine());
                a = new int[count];
                for (int i = 0; i < a.length; i++) {
                    info.print("  - a_" + i + " (in N+): ");
                    a[i] = Integer.parseInt(r.readLine());
                }
                return new BigModulo(t, m, a);
            case "c", "C", "modulocomb", "modulocombined", "Modulocomb", "Modulocombined", "ModuloComb", "ModuloCombined":
                info.print("  - Threshold t (t >= 1): ");
                t = Integer.parseInt(r.readLine());
                info.print("  - Modulus m (m > " + t + "): ");
                m = Integer.parseInt(r.readLine());
                info.print("  - Number of Dimensions: ");
                count = Integer.parseInt(r.readLine());
                a = new int[count];
                for (int i = 0; i < a.length; i++) {
                    info.print("  - a_" + i + " (in N+): ");
                    a[i] = Integer.parseInt(r.readLine());
                }
                return new ModuloCombined(t, m, a);
            case "f", "F", "file", "File":
                info.print("  - File to read from: ");
                String file = r.readLine();
                return new FileProtocol(Files.readString(Path.of(file)).replace(" ", "").replace("\n", "").replace("\r", "").split(";"));
            case "a", "A", "and", "And", "&", "&&":
                return new TimesProtocol<>(getProtocol(r, info), getProtocol(r, info));
            case "o", "O", "or", "Or", "|", "||":
                return new PlusProtocol<>(getProtocol(r, info), getProtocol(r, info));
            case "w", "W", "weak", "Weak", "weakconvert", "Weakconvert", "WeakConvert":
                return new WeakConvert<>(getWeakProtocol(r, info));
            case "u", "U", "unarythreshold", "Unarythreshold", "UnaryThreshold":
                info.print("  - Threshold cMax (cMax >= 1): ");
                int cMax = Integer.parseInt(r.readLine());
                return new UnaryThreshold(cMax);
            default:
                throw new IllegalArgumentException("Unknown protocol: " + protocolCode);
        }
    }

    private static WeakProtocol<?> getWeakProtocol(BufferedReader r, PrintStream info) throws IOException {
        info.print("• Weak Protocol to simulate? (g for GenMajority, i for InhomTowerCancle): ");
        String protocolCode = r.readLine();

        // Initialize the protocol
        if (protocolCode.equalsIgnoreCase("g")) {
            info.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                info.print("  - a_" + i + " (in Z): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new GenMajority(a);
        } else if (protocolCode.equalsIgnoreCase("i")) {
            info.print("  - Threshold t (t >= 1): ");
            int t = Integer.parseInt(r.readLine());
            info.print("  - Number of Dimensions: ");
            int count = Integer.parseInt(r.readLine());
            int[] a = new int[count];
            for (int i = 0; i < a.length; i++) {
                info.print("  - a_" + i + " (in Z): ");
                a[i] = Integer.parseInt(r.readLine());
            }
            return new InhomTowerCancle(t, a);
        }
        throw new IllegalArgumentException("No such weak protocol");
    }

    public int simulate(int[] x, boolean fastSim) throws InterruptedException {
        output.println("\nInput: " + Arrays.toString(x));
        if (!(protocol instanceof FileProtocol)) {
            output.println("Expected output: " + protocol.function(x));
        }
        System.out.println("\nStarting simulation...\n");
        output.println(config.toString());

        // Run the simulation
        boolean snipeInNextStep = true;
        while (!protocol.hasConsensus(config)) {
            snipeInNextStep = simulationStep(fastSim, snipeInNextStep);
        }

        // Print the final configuration
        output.println("\n" + config.toString());
        output.print("\nConsensus reached: " + protocol.O(config.get(0)) + ", expected " + protocol.function(x));
        if (output != System.out) {
            output.close();
            info.println("Done");
        } else {
            info.println(", Done");
        }
        return protocol.O(config.get(0));
    }

    public int calculateInTol(int[] x) {
        if (!(protocol instanceof FileProtocol)) {
            int value = protocol.function(x);
            for (int i = 1; i <= Arrays.stream(x).sum(); i++) {
                if (Helper.getSub(x, i).stream().anyMatch(c -> protocol.function(c) != value)) {
                    info.println("• Protocol with this input has the following initial tolerance: " + (i - 1));
                    return i - 1;
                }
            }
        }
        return config.sizeAll() - 1;
    }

    private Pair<int[], Boolean> setup() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        // Getting input
        int[] x = getInput(r);
        config = protocol.genConfig(x);
        int inTol = calculateInTol(x);

        info.println("\nInitializing sniper");
        sniper = protocol.initializeSniper(r, Math.min(inTol, config.sizeAll() - 2));

        boolean fastSim = configureOutput(r);
        return new Pair<>(x, fastSim);
    }

    private boolean simulationStep(boolean fastSim, boolean snipeInNextStep) throws InterruptedException {
        if (snipeInNextStep) {
            sniper.snipe(config, fastSim, output);
        }

        // Pick a random pair of agents
        int agent1;
        int agent2;
        do {
            agent1 = (int) (Math.random() * config.sizeAll());
        } while (!config.isActive(agent1));
        do {
            agent2 = (int) (Math.random() * config.sizeAll());
        } while (agent1 == agent2 || !config.isActive(agent2));
        Pair<T, T> newState = protocol.delta(config.get(agent1), config.get(agent2));

        if (newState == null) {
            return false;
        }

        if (!fastSim) {
            Thread.sleep(1000);
        }
        output.println("\n" + config.toString(agent1, agent2));
        if (!fastSim) {
            Thread.sleep(1000);
        }

        // Update the configuration
        config.set(agent1, newState.first());
        config.set(agent2, newState.second());

        output.println("\n" + config.toString(agent1, agent2));
        if (!fastSim) {
            Thread.sleep(1000);
        }
        output.println("\n" + config.toString());

        return true;
    }

    private int[] getInput(BufferedReader r) throws IOException {
        info.println("• Protocol is computing the following predicate: " + protocol.FUNCTION);
        int[] x = new int[protocol.ARG_LEN];

        for (int i = 0; i < x.length; i++) {
            info.print("  - x_" + i + ": ");
            x[i] = Integer.parseInt(r.readLine());
        }

        return x;
    }

    private boolean configureOutput(BufferedReader r) throws IOException {
        info.println("\nInitializing simulation");
        boolean fastSim;
        info.print("• simulation speed (s for slow, i for instant, f for file): ");
        String simCode = r.readLine();
        fastSim = !simCode.equalsIgnoreCase("s");
        if (simCode.equalsIgnoreCase("f")) {
            info.print("  - Outputfile: ");
            String outFile = r.readLine();
            output = new PrintStream(new FileOutputStream(outFile));
        } else {
            output = info;
        }
        return fastSim;
    }

    private Pair<T, T> pickRandomPair(Set<Pair<T, T>> set) {
        // randomly pick a pair from the set
        int index = (int) (Math.random() * set.size());
        int i = 0;
        // iterating over the elements of the set until the index is reached
        for (Pair<T, T> p : set) {
            if (i == index) {
                return p;
            }
            i++;
        }
        return null;
    }
}

