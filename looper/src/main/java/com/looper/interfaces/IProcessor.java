package com.looper.interfaces;

public interface IProcessor<M> {

    /**
     * 处理任务
     *
     * @param material
     * @return 是否轮训下一个任务
     */
    boolean onProcess(IMaterial<M> material);
}