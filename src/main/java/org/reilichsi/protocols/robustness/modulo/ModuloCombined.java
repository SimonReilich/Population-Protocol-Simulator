package org.reilichsi.protocols.robustness.modulo;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;
import org.reilichsi.protocols.robustness.threshold.InhomTower;
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
        super.FUNCTION = p + " mod " + m + " >= " + t;

        this.t = t;
        this.m = m;
        this.a = a;

        this.modulo = new BigModulo(this.t, this.m, this.a);
        this.inhomTower = new InhomTower((int) (3 * Math.pow(this.m, 2)), this.a);
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            count += a[i] * x[i];
        }
        return (count % m >= t) ? 1 : 0;
    }

    @Override
    public ModCombState I(int x) {
        int[] v = new int[2 * m];
        Arrays.fill(v, a[x]);
        boolean[] r = new boolean[2 * m];
        Arrays.fill(r, false);
        return new ModCombState(new Interval(0, a[x]), a[x], new BigModState(modulo, 0, v, r));
    }

    @Override
    public int O(ModCombState state) {
        if (state.h() < 2 * Math.pow(this.m, 2)) {
            return (state.h() % this.m >= this.t) ? 1 : 0;
        } else {
            return this.modulo.O(state.bigMod());
        }
    }

    @Override
    public Pair<ModCombState, ModCombState> delta(ModCombState x, ModCombState y) {
        Pair<BigModState, BigModState> moduloDelta = this.modulo.delta(x.bigMod(), y.bigMod());
        Pair<Interval, Interval> towerDelta = this.inhomTower.delta(x.inhomTower(), y.inhomTower());
        int h = Math.max(x.h(), y.h());
        int hMod = Math.max(towerDelta.first().end(), Math.max(towerDelta.second().end(), h));
        return new Pair<>(new ModCombState(towerDelta.first(), hMod, moduloDelta.first()), new ModCombState(towerDelta.second(), hMod, moduloDelta.second()));
    }

    @Override
    public boolean hasConsensus(Population<ModCombState> config) {
        return config.stream().allMatch(s1 -> {
            Population<ModCombState> config2 = new Population<>(this);
            for (int i = 0; i < config.sizeAll(); i++) {
                if (config.isActive(i)) {
                    config2.add(config.get(i));
                }
            }
            config2.killState(s1);
            return this.hasTransition(config2);
        });
    }

    @Override
    public ModCombState parseString(String s) {
        return null;
    }
}
