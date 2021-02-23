package com.looper;

import java.util.List;

public interface ILooper<T> {
    /**
     * 添加原料
     *
     * @param o
     * @param delay
     */
    void apply(T o, long delay);


    void apply(List<T> o, long delay);

    /**
     * 轮循原料
     *
     * @param delete 轮循原料是否从队列中删除
     * @param delay  延迟时间
     */
    void next(boolean delete, long delay);


    /**
     * 设置原料处理器
     *
     * @param iProcessor
     */
    void setProcessor(IProcessor<T> iProcessor);
}
