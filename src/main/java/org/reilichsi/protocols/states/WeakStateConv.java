package org.reilichsi.protocols.states;

import org.reilichsi.protocols.WeakProtocol;

import java.util.Optional;

public class WeakStateConv<T> {

    private final T state;
    private final Optional<Boolean> tendency;
    private final WeakProtocol<T> protocol;

    public WeakStateConv(T state, WeakProtocol<T> protocol) {
        this.state = state;
        if (protocol.output(state).isEmpty()) {
            this.tendency = Optional.of(false);
        } else {
            this.tendency = Optional.empty();
        }
        this.protocol = protocol;
    }

    public WeakStateConv(T state, WeakProtocol<T> protocol, boolean tendency) {
        this.state = state;
        if (protocol.output(state).isEmpty()) {
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

    public boolean getTendency() {
        return tendency.orElseGet(() -> protocol.output(state).get());
    }

}
