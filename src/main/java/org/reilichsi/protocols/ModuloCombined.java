package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.states.BigModState;
import org.reilichsi.protocols.states.Interval;
import org.reilichsi.protocols.states.ModCombState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ModuloCombined extends PopulationProtocol<ModCombState> {

    public final int m;
    private final int t;
    private final int[] a;

    private final BigModulo modulo;
    private final InhomTower inhomTower;

    public ModuloCombined(int t, int m, int... a) {
        super(a.length, "");

        // generating String-representation for predicate
        StringBuilder p = new StringBuilder("(" + a[0] + " * x_" + 0 + ")");
        for (int i = 1; i < a.length; i++) {
            p.append(" + (").append(a[i]).append(" * x_").append(i).append(")");
        }
        super.PREDICATE = p + " mod " + m + " >= " + t;

        this.t = t;
        this.m = m;
        this.a = a;

        this.modulo = new BigModulo(this.t, this.m, this.a);
        this.inhomTower = new InhomTower((int) (3 * Math.pow(this.m, 2)), this.a);
    }

    @Override
    public boolean output(ModCombState state) {
        if (state.h < 2 * Math.pow(this.m, 2)) {
            return state.h % this.m >= this.t;
        } else {
            return this.modulo.output(state.bigMod);
        }
    }

    @Override
    public Optional<Boolean> consensus(Population<ModCombState> config) {
        if (config.stream().allMatch(s1 -> {
            Population<ModCombState> config2 = new Population<>(this);
            for (int i = 0; i < config.sizeAll(); i++) {
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
    public String stateToString(ModCombState state) {
        return "(" + state.h + ", " + modulo.stateToString(state.bigMod) + ", " + inhomTower.stateToString(state.inhomTower) + ")";
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
    public Set<ModCombState> getQ() {
        Set<ModCombState> Q = new HashSet<>();
        for (int i = 0; i < Math.pow(this.m, 2) * 3; i++) {
            for (Interval stateTower : this.inhomTower.getQ()) {
                for (BigModState stateMod : this.modulo.getQ()) {
                    Q.add(new ModCombState(this, stateTower, i, stateMod));
                }
            }
        }
        return Q;
    }

    @Override
    public Set<ModCombState> getI() {
        Set<ModCombState> I = new HashSet<>();
        for (int ai : this.a) {
            int[] v = new int[2 * m];
            Arrays.fill(v, ai);
            boolean[] r = new boolean[2 * m];
            Arrays.fill(r, false);
            I.add(new ModCombState(this, new Interval(inhomTower, 0, ai), ai, new BigModState(modulo, 0, v, r)));
        }
        return I;
    }

    @Override
    public Set<Pair<ModCombState, ModCombState>> delta(ModCombState x, ModCombState y) {
        Set<Pair<BigModState, BigModState>> moduloDelta = this.modulo.delta(x.bigMod, y.bigMod);
        Set<Pair<Interval, Interval>> towerDelta = this.inhomTower.delta(x.inhomTower, y.inhomTower);
        int h = Math.max(x.h, y.h);
        Set<Pair<ModCombState, ModCombState>> res = new HashSet<>();
        for (Pair<BigModState, BigModState> moduloStates : moduloDelta) {
            for (Pair<Interval, Interval> towerStates : towerDelta) {
                int hMod = Math.max(towerStates.first().end, Math.max(towerStates.second().end, h));
                res.add(new Pair<>(new ModCombState(this, towerStates.first(), hMod, moduloStates.first()), new ModCombState(this, towerStates.second(), hMod, moduloStates.second())));
            }
        }
        if (moduloDelta.isEmpty()) {
            if (towerDelta.isEmpty()) {
                if (x.h != h || y.h != h) {
                    res.add(new Pair<>(new ModCombState(this, x.inhomTower, h, x.bigMod), new ModCombState(this, y.inhomTower, h, y.bigMod)));
                }
            } else {
                for (Pair<Interval, Interval> towerStates : towerDelta) {
                    int hMod = Math.max(towerStates.first().end, Math.max(towerStates.second().end, h));
                    res.add(new Pair<>(new ModCombState(this, towerStates.first(), hMod, x.bigMod), new ModCombState(this, towerStates.second(), h, x.bigMod)));
                }
            }
        } else if (towerDelta.isEmpty()) {
            int hMod = Math.max(x.inhomTower.end, Math.max(y.inhomTower.end, h));
            for (Pair<BigModState, BigModState> moduloStates : moduloDelta) {
                res.add(new Pair<>(new ModCombState(this, x.inhomTower, hMod, moduloStates.first()), new ModCombState(this, y.inhomTower, hMod, moduloStates.second())));
            }
        }
        return res;
    }

    @Override
    public Population<ModCombState> genConfig(int... x) {
        assertArgLength(x);
        Population<ModCombState> config = new Population<>(this);
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i]; j++) {
                int[] v = new int[2 * m];
                Arrays.fill(v, a[i]);
                boolean[] r = new boolean[2 * m];
                Arrays.fill(r, false);
                config.add(new ModCombState(this, new Interval(inhomTower, 0, a[i]), a[i], new BigModState(modulo, 0, v, r)));
            }
        }
        return config;
    }

    @Override
    public boolean statesEqual(ModCombState x, ModCombState y) {
        return x.equals(y);
    }

    @Override
    public ModCombState parseString(String s) {
        return null;
    }
}
