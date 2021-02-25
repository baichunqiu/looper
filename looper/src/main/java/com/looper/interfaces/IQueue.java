package com.looper.interfaces;

import java.util.List;

/**
 * 原则上有关原料列表数据的增删查 都要维持在队列线程中
 */
public interface IQueue {
    void apply(IMaterial m);

    void apply(List<IMaterial> m);

    ILooper getLooper(int index);

    IMaterial onProcess(int index, IMaterial material);

    /**
     * 处理完毕回调
     *
     * @param index 队列索引
     * @param count 处理失败的记录数
     */
    void onComplete(int index, int count);
}
