package com.looper.interfaces;

import java.util.List;

/**
 * 原则上有关原料列表数据的增删查 都要维持在队列线程中
 *
 * @param <M>
 */
public interface ILooper<M> {
    /**
     * 添加任务原料
     *
     * @param m     原料
     * @param delay 延迟时间,执行时和下一任务处理间隔 自动处理next的延迟时间
     */
    void apply(M m, long delay);


    /**
     * 批量添加
     *
     * @param ms
     * @param delay
     */
    void apply(List<M> ms, long delay);

    /**
     * 轮循原料
     *
     * @param delete 轮循原料是否从队列中删除
     * @param delay  延迟时间
     */
    void next(boolean delete, long delay);

    M pop();


    /**
     * 设置原料处理器
     *
     * @param iProcessor
     */
    void setProcessor(IProcessor<M> iProcessor);
}
