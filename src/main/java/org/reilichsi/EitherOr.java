package org.reilichsi;

public class EitherOr<T, U> {
    private T t;
    private U u;

    public EitherOr(T t, U u) {
        this.t = t;
        this.u = u;
    }

    public void setT(T t) {
        if (this.u != null && t != null) {
            throw new IllegalStateException();
        }
        this.t = t;
    }

    public void setU(U u) {
        if (u != null && this.t != null) {
            throw new IllegalStateException();
        }
        this.u = u;
    }

    public T getT() {
        return t;
    }

    public U getU() {
        return u;
    }

    public boolean isT() {
        return t != null;
    }

    public boolean isU() {
        return u != null;
    }
}
