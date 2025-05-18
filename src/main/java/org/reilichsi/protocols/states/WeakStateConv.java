package org.reilichsi.protocols.states;

import org.reilichsi.protocols.WeakProtocol;

import java.util.Optional;

public class WeakStateConv<T> {

    private final T state;
    private final Optional<Integer> tendency;
    private final WeakProtocol<T> protocol;

    public WeakStateConv(T state, WeakProtocol<T> protocol) {
        this.state = state;
        if (protocol.O(state).isEmpty()) {
            this.tendency = Optional.of(0);
        } else {
            this.tendency = Optional.empty();
        }
        this.protocol = protocol;
    }

    public WeakStateConv(T state, WeakProtocol<T> protocol, int tendency) {
        this.state = state;
        if (protocol.O(state).isEmpty()) {
            this.tendency = Optional.of(tendency);
        } else {
            this.tendency = Optional.empty();
        }
        this.protocol = protocol;
    }

    public T getState() {
        return state;
    }

    public boolean isNeutral() {
        return tendency.isPresent();
    }

    public int getTendency() {
        return tendency.orElseGet(() -> protocol.O(state).get());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WeakStateConv) {
            WeakStateConv<T> other = (WeakStateConv<T>) o;
            return state.equals(other.state) && tendency.equals(other.tendency);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + state.toString() + ", " + tendency + ")";
    }

}
