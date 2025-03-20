package org.reilichsi;

import org.reilichsi.protocols.PopulationProtocol;
import org.reilichsi.protocols.Protocol;
import org.reilichsi.protocols.WeakProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Population<T> {

    private final Protocol<T> protocol;
    private final List<T> population;
    private final List<Boolean> active;

    @SafeVarargs
    public Population(Protocol<T> protocol, T... input) {
        this.protocol = protocol;
        population = new ArrayList<>();
        population.addAll(Arrays.asList(input));
        active = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            active.add(true);
        }
    }

    public Population(Protocol<T> protocol, Set<T> input) {
        this.protocol = protocol;
        population = new ArrayList<>();
        population.addAll(input);
        active = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            active.add(true);
        }
    }

    @SafeVarargs
    public Population(WeakProtocol<T> protocol, T... input) {
        this.protocol = protocol;
        population = new ArrayList<>();
        population.addAll(Arrays.asList(input));
        active = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            active.add(true);
        }
    }

    public Population(WeakProtocol<T> protocol, Set<T> input) {
        this.protocol = protocol;
        population = new ArrayList<>();
        population.addAll(input);
        active = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            active.add(true);
        }
    }

    public void add(T state) {
        population.add(state);
        active.add(true);
    }

    public int count(T t) {
        return (int) population.stream().filter(s -> s == t).count();
    }

    public int countActive(T t) {
        int count = 0;
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i) == t && active.get(i)) {
                count += 1;
            }
        }
        return count;
    }

    public int size() {
        return population.size();
    }

    public int sizeActive() {
        return (int) active.stream().filter(b -> b).count();
    }

    @SafeVarargs
    public final boolean contains(T... states) {
        boolean[] visited = new boolean[population.size()];
        Arrays.fill(visited, false);
        for (T s : states) {
            for (int i = 0; i < population.size(); i++) {
                if (population.get(i) == s && !visited[i] && active.get(i)) {
                    visited[i] = true;
                    break;
                }
            }
            return false;
        }
        return true;
    }

    public T get(int i) {
        return population.get(i);
    }

    public void set(int i, T state) {
        population.set(i, state);
    }

    public boolean isActive(int i) {
        return active.get(i);
    }

    public void kill(int pos) {
        active.set(pos, false);
    }

    public boolean killState(T state) {
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i) == state && active.get(i)) {
                active.set(i, false);
                return true;
            }
        }
        return false;
    }

    private String format(T state) {
        if (state instanceof Boolean) {
            return ((boolean) state) ? "+" : "-";
        }
        return state.toString();
    }

    private String toStringArgs(Integer... selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < population.size(); i++) {
            if (Arrays.asList(selected).contains(i)) {
                if (active.get(i)) {
                    sb.append(" *").append(protocol.stateToString(population.get(i))).append("* ").append("|");
                } else {
                    throw new IllegalStateException("Inactive agent cant be selected");
                }
            } else {
                if (active.get(i)) {
                    sb.append("  ").append(protocol.stateToString(population.get(i))).append("  ").append("|");
                } else {
                    sb.append(" -").append(protocol.stateToString(population.get(i))).append("- ").append("|");
                }
            }
        }
        return sb.toString();
    }

    public String toString(Integer... selected) {
        return toStringArgs(selected);
    }

    @Override
    public String toString() {
        return toStringArgs();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass())) {
            Population<T> other = (Population<T>) o;
            for (T s : this.population) {
                if (other.countActive(s) != this.countActive(s)) {
                    return false;
                }
            }
            for (T s : other.population) {
                if (other.countActive(s) != this.countActive(s)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Stream<T> stream() {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            if (active.get(i)) {
                list.add(population.get(i));
            }
        }
        return list.stream();
    }
}