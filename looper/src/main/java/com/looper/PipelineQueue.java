package com.looper;

import android.os.SystemClock;

import com.looper.core.PipeQueue;
import com.looper.core.TaskLooper;
import com.looper.interfaces.IMaterial;
import com.looper.interfaces.IQueue;

/**
 * 并行队列
 */
public class PipelineQueue extends PipeQueue<String> {
    private final static PipelineQueue _queue = new PipelineQueue();

    public static IQueue<String> getQueue() {
        return _queue;
    }

    private PipelineQueue() {
        super(2, 3, false);
    }

    @Override
    public String onProcess(int index, IMaterial material, TaskLooper next) {
        // TODO: 2/24/21 模拟耗时操作
        SystemClock.sleep(index == 0 ? 200 : 500);
        String result = material.material() + "_pro" + index;
        Logger.e("PipelineQueue", "process1 result = " + result);
        return result;
    }
}
