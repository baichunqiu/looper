package com.looper;

public class Material<T> implements IMaterial<T> {
    private final static int MAX = 3;
    protected long delay;
    protected T material;
    protected int count = 0;

    public T getMaterial() {
        return material;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public boolean available() {
        return count < MAX;
    }

    @Override
    public void countPlus() {
        count = count + 1;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                (o instanceof Material &&
                        null != material && material.equals(((Material) o).material));
    }
}