package com.looper.core;

import com.looper.interfaces.IMaterial;
import com.looper.interfaces.IProcessor;
import com.looper.interfaces.IQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * 并行队列
 * 描述：第一道工序（looper）处理，并流向下道工序，直至最后一道工序完成，第一道工序开始处理下一个原料，如此往复。
 * 注意：第一道工序需要等待最后一道工序执行完毕后才能分发下一个原料
 */
public abstract class PipeQueue<T> implements IQueue<T> {
    private List<TaskLooper> loopers = new ArrayList<>(4);
    protected int maxLooper = 1;

    public PipeQueue(int looperSize) {
        if (looperSize < 1) looperSize = 1;
        maxLooper = looperSize;
        init();
    }

    private void init() {
        loopers.clear();
        TaskLooper looper;
        for (int i = 0; i < maxLooper; i++) {
            looper = new TaskLooper(this.getClass().getSimpleName() + i);
            final int index = i;
            looper.setProcessor(new IProcessor() {
                @Override
                public boolean onProcess(IMaterial material) {
                    return handleProcess(index, material);
                }
            });
            loopers.add(looper);
        }
    }


    public boolean apply(List<String> os, long delay) {
        if (null == os || os.isEmpty()) return false;
        loopers.get(0).apply(os, delay);
        return true;
    }

    protected TaskLooper getLooper(int index) {
        if (index < 0 || index >= maxLooper) return null;
        return loopers.get(index);
    }

    /**
     * 处理当前任务 并分发下个工序
     *
     * @param index
     * @param material
     */
    protected boolean handleProcess(int index, IMaterial material) {
        TaskLooper next = getLooper(index + 1);
        Object obj = onProcess(index, material, next);
        if (null != obj && null != next) {//分发至下一工序
            next.apply(obj, material.delay());
        }
        return true;
    }

    @Override
    public abstract T onProcess(int index, IMaterial material, TaskLooper next);
}
