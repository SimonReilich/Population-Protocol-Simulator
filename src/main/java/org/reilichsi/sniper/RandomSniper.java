package org.reilichsi.sniper;

import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;

import java.io.PrintStream;

public class RandomSniper<T> extends Sniper<T> {

    private final double snipeRate;

    public RandomSniper(PopulationProtocol<T> protocol, int maxSnipes, double snipeRate) {
        super(protocol, maxSnipes);
        this.snipeRate = snipeRate;
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

    @Override
    public boolean snipe(Population<T> config, boolean fastSim, PrintStream ps) throws InterruptedException {
        matchPopulationSize(config);

        boolean out = false;

        if (super.canSnipe()) {
            int toBeSniped = getPoissonRandom(this.snipeRate);
            for (int i = 0; i < toBeSniped; i++) {
                int index;
                do {
                    index = (int) (Math.random() * config.sizeAll());
                } while (!config.isActive(index));
                config.kill(index);
                out = true;
                super.decreaseSnipes();
                if (!super.canSnipe()) {
                    break;
                }
            }
            if (toBeSniped > 0 && !fastSim) {
                Thread.sleep(1000);
            }
            if (toBeSniped > 0) {
                ps.println("\n" + config);
            }
        }
        return out;
    }
}
