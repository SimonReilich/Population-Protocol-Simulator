package org.reilichsi.protocols.robustness;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;
import org.reilichsi.protocols.states.WeakStateConv;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class WeakConvert<T> extends PopulationProtocol<WeakStateConv<T>> {

    private final WeakProtocol<T> w;

    public WeakConvert(WeakProtocol<T> weakProtocol) {
        super(weakProtocol.ARG_LEN, weakProtocol.PREDICATE);
        this.w = weakProtocol;
    }

    @Override
    public boolean predicate(int... x) {
        assertArgLength(x);
        return this.w.predicate(x);
    }

    @Override
    public Set<WeakStateConv<T>> getQ() {
        HashSet<WeakStateConv<T>> Q = new HashSet<>();
        Q.addAll(w.getQ().stream().filter(s -> w.output(s).isPresent()).map(s -> new WeakStateConv<>(s, w)).toList());
        Q.addAll(w.getQ().stream().filter(s -> w.output(s).isEmpty()).map(s -> new WeakStateConv<>(s, w, false)).toList());
        Q.addAll(w.getQ().stream().filter(s -> w.output(s).isEmpty()).map(s -> new WeakStateConv<>(s, w, true)).toList());
        return Q;
    }

    @Override
    public Set<WeakStateConv<T>> getI() {
        return this.w.getI().stream().map(s -> new WeakStateConv<>(s, w)).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<WeakStateConv<T>, WeakStateConv<T>>> delta(WeakStateConv<T> x, WeakStateConv<T> y) {
        HashSet<Pair<WeakStateConv<T>, WeakStateConv<T>>> result = new HashSet<>();
        if (!x.isNeutral() && y.isNeutral()) {
            // witnessPos / witnessNeg
            result.add(new Pair<>(x, new WeakStateConv<>(y.getState(), w, x.getTendency())));
        } else if (!y.isNeutral() && x.isNeutral()) {
            // witnessPos / witnessNeg
            result.add(new Pair<>(new WeakStateConv<>(x.getState(), w, y.getTendency()), y));
        } else if (x.isNeutral() && y.isNeutral() && x.getTendency() != y.getTendency()) {
            // convince
            result.add(new Pair<>(new WeakStateConv<>(x.getState(), w, false), new WeakStateConv<>(y.getState(), w, false)));
        }
        // derived
        result.addAll(this.w.delta(x.getState(), y.getState()).stream().map(s -> new Pair<>(new WeakStateConv<>(s.first(), w, false), new WeakStateConv<>(s.second(), w, false))).collect(Collectors.toSet()));
        return result;
    }

    @Override
    public boolean output(WeakStateConv<T> state) {
        return state.getTendency();
    }

    @Override
    public Optional<Boolean> consensus(Population<WeakStateConv<T>> config) {
        List<T> list = config.stream().map(WeakStateConv::getState).toList();
        Population<T> weakPop = new Population<>(this.w);
        for (T s : list) {
            weakPop.add(s);
        }
        return this.w.consensus(weakPop);
    }

    @Override
    public Population<WeakStateConv<T>> genConfig(int... x) {
        assertArgLength(x);
        List<WeakStateConv<T>> weakPopulation = this.w.genConfig(x).stream().map(s -> new WeakStateConv<>(s, w)).toList();
        return new Population<WeakStateConv<T>>(this, weakPopulation.toArray(new WeakStateConv[0]));
    }

    @Override
    public boolean statesEqual(WeakStateConv x, WeakStateConv y) {
        return x.getState().equals(y.getState()) && x.getTendency() == y.getTendency();
    }

    @Override
    public WeakStateConv<T> parseString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new WeakStateConv<>(this.w.parseString(first), w, Boolean.parseBoolean(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}