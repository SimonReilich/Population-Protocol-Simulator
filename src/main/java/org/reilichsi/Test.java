package org.reilichsi;

import org.reilichsi.protocols.robustness.modulo.BigModulo;
import org.reilichsi.protocols.robustness.modulo.ModuloCombined;
import org.reilichsi.protocols.states.BigModState;
import org.reilichsi.protocols.states.ModCombState;
import org.reilichsi.sniper.NoSniper;
import org.reilichsi.sniper.Sniper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        testModCombUnary();
    }

    public static void testBigModUnary() throws InterruptedException {
        Sniper<BigModState> sniper = new NoSniper<>();
        for (int m = 2; m < 10; m++) {
            for (int t = 1; t < m; t++) {
                BigModulo protocol = new BigModulo(t, m, 1);
                Simulator<BigModState> simulator = new Simulator<>(protocol, null, sniper, new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        return;
                    }
                }), System.out);
                for (int i = 2 * m; i < 4 * m; i += (int) (Math.random() * (m - 1) + 1)) {
                    Population<BigModState> config = protocol.genConfig(i);
                    simulator.setConfig(config);
                    if (simulator.simulate(new int[]{i}, true) != protocol.function(i)) {
                        throw new UnknownError("Failed for m = " + m + ", t = " + t + ", i = " + i);
                    }
                    System.out.println("  for m = " + m + ", t = " + t + ", i = " + i);
                    System.out.println("    " + config.stream().map(s -> helper(s.result())).reduce((String::concat)).get());
                }
            }
        }
    }

    public static void testModCombUnary() throws InterruptedException {
        Sniper<ModCombState> sniper = new NoSniper<>();
        for (int m = 2; m < 10; m++) {
            for (int t = 1; t < m; t++) {
                ModuloCombined protocol = new ModuloCombined(t, m, 1);
                Simulator<ModCombState> simulator = new Simulator<>(protocol, null, sniper, new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        return;
                    }
                }), System.out);
                for (int i = 1; i < 4 * m; i += (int) (Math.random() * (m - 1) + 1)) {
                    Population<ModCombState> config = protocol.genConfig(i);
                    simulator.setConfig(config);
                    if (simulator.simulate(new int[]{i}, true) != protocol.function(i)) {
                        throw new UnknownError("Failed for m = " + m + ", t = " + t + ", i = " + i);
                    }
                    System.out.println("  for m = " + m + ", t = " + t + ", i = " + i);
                }
            }
        }
    }

    public static String helper(boolean[] r) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : r) {
            sb.append(b ? "+" : "-");
        }
        return sb.toString();
    }

}
