package com.looper;

import com.looper.interfaces.IMaterial;

public class Material<M> implements IMaterial<M> {
    private long delay = 0;//任务处理延迟时间
    private M material;//原料
    private int count = 0;//尝试次数

    public Material(M material) {
        this.material = material;
    }

    @Override
    public M material() {
        return material;
    }

    @Override
    public boolean available() {
        return count >= 0 && count < 3;
    }

    @Override
    public long delay() {
        return delay;
    }

    @Override
    public void countPlus() {
        this.count++;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                (o instanceof Material &&
                        null != material && material.equals(((Material) o).material));
    }
}