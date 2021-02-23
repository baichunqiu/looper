package com.looper;

public interface IProcessor<T> {

    /**
     * 处理任务
     *
     * @param material
     * @return 是否轮训下一个任务
     */
    boolean onProcess(IMaterial<T> material);
}