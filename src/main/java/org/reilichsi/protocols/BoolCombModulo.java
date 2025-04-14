package org.reilichsi.protocols;

import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.states.BoolCombModState;

import java.util.Set;

public class BoolCombModulo extends PopulationProtocol<BoolCombModState> {
    public BoolCombModulo() {
        super(0, "");
    }

    @Override
    public boolean output(BoolCombModState state) {
        return false;
    }

    @Override
    public boolean predicate(int... x) {
        return false;
    }

    @Override
    public Set<BoolCombModState> getQ() {
        return Set.of();
    }

    @Override
    public Set<BoolCombModState> getI() {
        return Set.of();
    }

    @Override
    public Set<Pair<BoolCombModState, BoolCombModState>> delta(BoolCombModState x, BoolCombModState y) {
        return Set.of();
    }

    @Override
    public Population<BoolCombModState> genConfig(int... x) {
        return null;
    }

    @Override
    public boolean statesEqual(BoolCombModState x, BoolCombModState y) {
        return false;
    }

    @Override
    public BoolCombModState parseString(String s) {
        return null;
    }
}
