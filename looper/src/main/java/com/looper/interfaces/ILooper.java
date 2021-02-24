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

    /**
     * 获取下一个可用
     *
     * @return
     */
    IMaterial<M> pop();

    /**
     * 手动移除
     *
     * @param material
     * @return
     */
    boolean remove(IMaterial<M> material);

    /**
     * 清空队列
     */
    void clear();

    /**
     * 处理任务回调
     *
     * @param material
     * @return
     */
    boolean onProcess(IMaterial<M> material);

    /**
     * 处理完毕回调
     *
     * @return 处理完毕后是否情况
     */
    void onComplete(int count);
}
