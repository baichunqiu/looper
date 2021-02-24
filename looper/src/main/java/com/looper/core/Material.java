package com.looper.core;

import com.looper.interfaces.IMaterial;

public class Material<M> implements IMaterial<M> {
    protected long delay;//任务处理延迟时间
    protected M material;//原料
    protected int count = 0;//尝试次数
    protected int max = 0;//最大次数

    protected Material(int max) {
        this.max = max;
    }

    @Override
    public M material() {
        return material;
    }

    @Override
    public boolean available() {
        return count < max;
    }

    @Override
    public long delay() {
        return delay;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                (o instanceof Material &&
                        null != material && material.equals(((Material) o).material));
    }
}