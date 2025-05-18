package org.reilichsi.protocols.robustness;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;
import org.reilichsi.protocols.PopulationProtocol;
import org.reilichsi.protocols.WeakProtocol;
import org.reilichsi.protocols.states.WeakStateConv;

import java.util.List;

public class WeakConvert<T> extends PopulationProtocol<WeakStateConv<T>> {

    private final WeakProtocol<T> w;

    public WeakConvert(WeakProtocol<T> weakProtocol) {
        super(weakProtocol.ARG_LEN, weakProtocol.FUNCTION);
        this.w = weakProtocol;
    }

    @Override
    public int function(int... x) {
        assert x.length == this.ARG_LEN;
        return this.w.function(x);
    }

    @Override
    public WeakStateConv<T> I(int x) {
        return new WeakStateConv<>(this.w.I(x), w);
    }

    @Override
    public int O(WeakStateConv<T> state) {
        return state.getTendency();
    }

    @Override
    public Pair<WeakStateConv<T>, WeakStateConv<T>> delta(WeakStateConv<T> x, WeakStateConv<T> y) {
        if (!x.isNeutral() && y.isNeutral()) {
            // witnessPos / witnessNeg
            return new Pair<>(x, new WeakStateConv<>(y.getState(), w, x.getTendency()));
        } else if (!y.isNeutral() && x.isNeutral()) {
            // witnessPos / witnessNeg
            return new Pair<>(new WeakStateConv<>(x.getState(), w, y.getTendency()), y);
        } else if (x.isNeutral() && y.isNeutral() && x.getTendency() != y.getTendency()) {
            // convince
            return new Pair<>(new WeakStateConv<>(x.getState(), w, 0), new WeakStateConv<>(y.getState(), w, 0));
        }
        // derived
        Pair<T, T> weakDelta = this.w.delta(x.getState(), y.getState());
        return new Pair<>(new WeakStateConv<>(weakDelta.first(), w, 0), new WeakStateConv<>(weakDelta.second(), w, 0));
    }

    @Override
    public boolean hasConsensus(Population<WeakStateConv<T>> config) {
        List<T> list = config.stream().map(WeakStateConv::getState).toList();
        Population<T> weakPop = new Population<>(this.w);
        for (T s : list) {
            weakPop.add(s);
        }
        return this.w.hasConsensus(weakPop);
    }

    @Override
    public WeakStateConv<T> parseString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new WeakStateConv<>(this.w.parseString(first), w, Integer.parseInt(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}