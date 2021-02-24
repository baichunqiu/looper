package com.looper.interfaces;

import com.looper.core.TaskLooper;

import java.util.List;

/**
 * 原则上有关原料列表数据的增删查 都要维持在队列线程中
 */
public interface IQueue<R> {
    void apply(Object m, long delay);

    void apply(List m, long delay);

    R onProcess(int index, IMaterial material, TaskLooper next);
}
