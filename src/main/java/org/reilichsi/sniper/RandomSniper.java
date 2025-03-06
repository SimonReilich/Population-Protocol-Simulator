package org.reilichsi.sniper;

import org.reilichsi.Population;

import java.io.BufferedReader;
import java.io.IOException;

public class RandomSniper<T> extends Sniper<T> {

    private int maxSnipes;
    private double snipeRate;

    public RandomSniper(BufferedReader r) throws IOException {
        super();
        System.out.print("Mean agents killed by the sniper per round: ");
        snipeRate = Double.parseDouble(r.readLine());
        System.out.print("Maximum number of snipes (-1 for no limit): ");
        maxSnipes = Integer.parseInt(r.readLine());
    }

    @Override
    public boolean snipe(Population<T> config, boolean fastSim) throws InterruptedException {
        if (maxSnipes >= config.sizeActive()) {
            maxSnipes = config.sizeActive() - 1;
        }

        boolean out = false;

        if (maxSnipes != 0) {
            int toBeSniped = getPoissonRandom(snipeRate);
            for (int i = 0; i < toBeSniped; i++) {
                int index;
                do {
                    index = (int) (Math.random() * config.size());
                } while (!config.isActive(index));
                config.kill(index);
                out = true;
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
        return out;
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
