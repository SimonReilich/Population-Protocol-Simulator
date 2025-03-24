package org.reilichsi.protocols;

import org.reilichsi.Helper;
import org.reilichsi.Pair;
import org.reilichsi.Population;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class WeakConvert<T> extends PopulationProtocol<Pair<T, Boolean>> {

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
    public Set<Pair<T, Boolean>> getQ() {
        return this.w.getQ().stream().flatMap(s -> Set.of(new Pair<>(s, true), new Pair<>(s, false)).stream()).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<T, Boolean>> getI() {
        return this.w.getI().stream().map(s -> this.w.output(s).isPresent() ? new Pair<>(s, this.w.output(s).get()) : new Pair<>(s, false)).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<Pair<T, Boolean>, Pair<T, Boolean>>> delta(Pair<T, Boolean> x, Pair<T, Boolean> y) {
        HashSet<Pair<Pair<T, Boolean>, Pair<T, Boolean>>> result = new HashSet<>();
        if (this.w.output(x.first()).isPresent() && this.w.output(y.first()).isEmpty()) {
            // witnessPos / witnessNeg
            result.add(new Pair<>(new Pair<>(x.first(), this.w.output(x.first()).get()), new Pair<>(y.first(), this.w.output(x.first()).get())));
        } else if (this.w.output(y.first()).isPresent() && this.w.output(x.first()).isEmpty()) {
            // witnessPos / witnessNeg
            result.add(new Pair<>(new Pair<>(x.first(), this.w.output(y.first()).get()), new Pair<>(y.first(), this.w.output(y.first()).get())));
        } else if (this.w.output(x.first()).isEmpty() && this.w.output(y.first()).isEmpty() && x.second() != y.second()) {
            // convince
            result.add(new Pair<>(new Pair<>(x.first(), false), new Pair<>(y.first(), false)));
        }
        // derived
        result.addAll(this.w.delta(x.first(), y.first()).stream().map(s -> new Pair<>(new Pair<>(s.first(), (this.w.output(s.first()).isPresent() && this.w.output(s.first()).get())), new Pair<>(s.second(), (this.w.output(s.second()).isPresent() && this.w.output(s.second()).get())))).collect(Collectors.toSet()));
        return result;
    }

    @Override
    public boolean output(Pair<T, Boolean> state) {
        return this.w.output(state.first()).isPresent() ? (boolean) this.w.output(state.first()).get() : state.second();
    }

    @Override
    public Optional<Boolean> consensus(Population<Pair<T, Boolean>> config) {
        List<T> list = config.stream().map(Pair::first).toList();
        Population<T> weakPop = new Population<>(this.w);
        for (T s : list) {
            weakPop.add(s);
        }
        return this.w.consensus(weakPop);
    }

    @Override
    public Population<Pair<T, Boolean>> genConfig(int... x) {
        assertArgLength(x);
        List<Pair<T, Boolean>> weakPopulation = this.w.genConfig(x).stream().map(s -> this.w.output(s).isPresent() ? new Pair<>(s, this.w.output(s).get()) : new Pair<>(s, false)).toList();
        return new Population<Pair<T, Boolean>>(this, weakPopulation.toArray(new Pair[0]));
    }

    @Override
    public boolean statesEqual(Pair<T, Boolean> x, Pair<T, Boolean> y) {
        return w.statesEqual(x.first(), y.first()) && x.second() == y.second();
    }

    @Override
    public Pair<T, Boolean> stateFromString(String s) {
        s = s.trim();
        for (int i = s.indexOf(';'); i < s.length(); i++) {
            String first = s.substring(1, i).trim();
            String second = s.substring(i + 2, s.length() - 1).trim();
            if (Helper.countChar(first, '(') - Helper.countChar(first, ')') == 0 && Helper.countChar(second, '(') - Helper.countChar(second, ')') == 0) {
                return new Pair<>(this.w.stateFromString(first), Boolean.parseBoolean(second));
            }
        }
        throw new IllegalArgumentException("Invalid state: " + s);
    }
}