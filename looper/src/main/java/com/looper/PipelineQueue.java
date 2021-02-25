package com.looper;

import android.os.SystemClock;

import com.looper.core.PipeQueue;
import com.looper.interfaces.IMaterial;
import com.looper.interfaces.IQueue;

import java.util.Random;

/**
 * 并行队列
 */
public class PipelineQueue extends PipeQueue {
    private final static PipelineQueue _queue = new PipelineQueue();

    public static IQueue getQueue() {
        return _queue;
    }

    private PipelineQueue() {
        super(2, 3, false);
    }

    @Override
    public IMaterial onProcess(int index, IMaterial material) {
        // TODO: 2/24/21 模拟耗时操作
        SystemClock.sleep(index == 0 ? 200 : 500);
        String result = material.material() + "_pro" + index;
        Logger.e("PipelineQueue", "index = " + index + " result = " + result);
        material.countPlus();
        Random random = new Random();
        int count = random.nextInt();
        if (count > 0) {
            // TODO: 2/25/21 模拟处理成功后
            getLooper(index).remove(material);
            Logger.e("PipelineQueue", "index = " + index + " 成功 移除");
        }
        return new Material(result);
    }
}
