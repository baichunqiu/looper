package com.looper.interfaces;

import com.looper.core.TaskLooper;

/**
 * 原则上有关原料列表数据的增删查 都要维持在队列线程中
 *
 * @param <M>
 */
public interface IQueue<M> {
    M onProcess(int index, IMaterial material, TaskLooper next);
}
