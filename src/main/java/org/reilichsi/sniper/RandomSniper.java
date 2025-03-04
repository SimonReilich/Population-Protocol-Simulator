package org.reilichsi.sniper;

import org.reilichsi.Population;

public class RandomSniper<T> extends Sniper<T> {

    private int maxSnipes;
    private double snipeRate;

    public RandomSniper() {
        super();
    }

    public RandomSniper(int maxSnipes, double snipeRate) {
        super();
        this.maxSnipes = maxSnipes;
        this.snipeRate = snipeRate;
    }

    public void setMaxSnipes(int maxSnipes) {
        this.maxSnipes = maxSnipes;
    }

    public void setSnipeRate(double snipeRate) {
        this.snipeRate = snipeRate;
    }

    @Override
    public void snipe(Population<T> config, boolean fastSim) throws InterruptedException {
        if (maxSnipes >= config.sizeActive()) {
            maxSnipes = config.sizeActive() - 1;
        }

        if (maxSnipes != 0) {
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
