package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ModuloCombined extends PopulationProtocol<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> {

    private final int t;
    private final int m;
    private final int[] a;

    private final BigModulo modulo;
    private final InhomTower inhomTower;

    public ModuloCombined(int t, int m, int... a) {
        super(a.length , n -> "");

        // generating String-representation for predicate
        Function<Integer, String> p = n -> "(" + a[0] + " * x_" + n + ")";
        for (int i = 1; i < a.length; i++) {
            Function<Integer, String> finalP = p;
            int finalI = i;
            p = n -> finalP.apply(n) + " + (" + a[finalI] + " * x_" + (n + finalI) + ")";
        }
        Function<Integer, String> finalP = p;
        super.PREDICATE = n -> finalP.apply(n) + " mod " + m + " >= " + t;

        this.t = t;
        this.m = m;
        this.a = a;

        this.modulo = new BigModulo(this.t, this.m, this.a);
        this.inhomTower = new InhomTower((int) (3 * Math.pow(this.m, 2)), this.a);
    }

    @Override
    public boolean output(Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> state) {
        if (state.first() < 2 * Math.pow(this.m, 2)) {
            return state.first() % this.m >= this.t;
        } else {
            return this.modulo.output(state.second().first());
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> config) {
        if (config.stream().allMatch(s1 -> {
            Population<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> config2 = new Population<>(this);
            for (int i = 0; i < config.size(); i++) {
                if (config.isActive(i)) {
                    config2.add(config.get(i));
                }
            }
            config2.killState(s1);
            return config2.stream().allMatch(s2 -> this.delta(s1, s2).isEmpty());
        })) {
            return Optional.of(this.output(config.get(0)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String stateToString(Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> state) {
        return "(" + state.first() + ", " + modulo.stateToString(state.second().first()) + ", " + inhomTower.stateToString(state.second().second()) + ")";
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            count += a[i] * x[i];
        }
        return count % m >= t;
    }

    @Override
    public Set<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> getQ() {
        Set<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> Q = new HashSet<>();
        for (int i = 0; i < Math.pow(this.m, 2) * 3; i++) {
            for (Pair<Integer, Integer> stateTower : this.inhomTower.getQ()) {
                for (Pair<Integer, Pair<Integer[], Boolean[]>> stateMod : this.modulo.getQ()) {
                    Q.add(new Pair<>(i, new Pair<>(stateMod, stateTower)));
                }
            }
        }
        return Q;
    }

    @Override
    public Set<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> getI() {
        Set<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> I = new HashSet<>();
        for (int ai : this.a) {
            Integer[] v = new Integer[2 * m];
            Arrays.fill(v, ai);
            Boolean[] r = new Boolean[2 * m];
            Arrays.fill(r, false);
            I.add(new Pair<>(ai, new Pair<>(new Pair<>(0, new Pair<>(v, r)), new Pair<>(0, ai))));
        }
        return I;
    }

    @Override
    public Set<Pair<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>, Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>>> delta(Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> x, Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> y) {
        Set<Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Pair<Integer[], Boolean[]>>>> moduloDelta = this.modulo.delta(x.second().first(), y.second().first());
        Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> towerDelta = this.inhomTower.delta(x.second().second(), y.second().second());
        int h = Math.max(x.first(), y.first());
        Set<Pair<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>, Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>>> res = new HashSet<>();
        for (Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Pair<Integer[], Boolean[]>>> moduloStates : moduloDelta) {
            for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> towerStates : towerDelta) {
                int hMod = Math.max(towerStates.first().second(), Math.max(towerStates.second().second(), h));
                res.add(new Pair<>(new Pair<>(hMod, new Pair<>(moduloStates.first(), towerStates.first())), new Pair<>(hMod, new Pair<>(moduloStates.second(), towerStates.second()))));
            }
        }
        if (moduloDelta.isEmpty()) {
            if (towerDelta.isEmpty()) {
                if (x.first() != h || y.first() != h) {
                    res.add(new Pair<>(new Pair<>(h, x.second()), new Pair<>(h, y.second())));
                }
            } else {
                for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> towerStates : towerDelta) {
                    int hMod = Math.max(towerStates.first().second(), Math.max(towerStates.second().second(), h));
                    res.add(new Pair<>(new Pair<>(hMod, new Pair<>(x.second().first(), towerStates.first())), new Pair<>(hMod, new Pair<>(x.second().first(), towerStates.second()))));
                }
            }
        } else if (towerDelta.isEmpty()) {
            int hMod = Math.max(x.second().second().second(), Math.max(y.second().second().second(), h));
            for (Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Pair<Integer[], Boolean[]>>> moduloStates : moduloDelta) {
                res.add(new Pair<>(new Pair<>(hMod, new Pair<>(moduloStates.first(), x.second().second())), new Pair<>(hMod, new Pair<>(moduloStates.second(), y.second().second()))));
            }
        }
        return res;
    }

    @Override
    public Population<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> genConfig(int... x) {
        assertArgLength(x);
        Population<Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>>> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i]; j++) {
                Integer[] v = new Integer[2 * m];
                Arrays.fill(v, a[i]);
                Boolean[] r = new Boolean[2 * m];
                Arrays.fill(r, false);
                config.add(new Pair<>(a[i], new Pair<>(new Pair<>(0, new Pair<>(v, r)), new Pair<>(0, a[i]))));
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> x, Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> y) {
        return x.first() == y.first() && modulo.statesEqual(x.second().first(), y.second().first()) && inhomTower.statesEqual(x.second().second(), y.second().second());
    }

    @Override
    public Pair<Integer, Pair<Pair<Integer, Pair<Integer[], Boolean[]>>, Pair<Integer, Integer>>> stateFromString(String s) {
        return null;
    }
}
