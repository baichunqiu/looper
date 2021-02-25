package com.looper.core;

import com.looper.Logger;
import com.looper.interfaces.ILooper;
import com.looper.interfaces.IMaterial;
import com.looper.interfaces.IQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * 并行队列
 * 描述：第一道工序（looper）处理，并流向下道工序，直至最后一道工序完成，第一道工序开始处理下一个原料，如此往复。
 * 注意：第一道工序需要等待最后一道工序执行完毕后才能分发下一个原料
 */
public abstract class PipeQueue implements IQueue {
    private List<ILooper> loopers = new ArrayList<>(4);
    protected int maxLooper = 1;
    protected int tryMax = 1;
    protected boolean delete;

    /**
     * @param looperSize 队列数
     * @param tryMax     最大尝试次数
     * @param delete     执行后是否删除
     */
    public PipeQueue(int looperSize, int tryMax, boolean delete) {
        if (looperSize < 1) looperSize = 1;
        if (tryMax < 1) tryMax = 1;
        maxLooper = looperSize;
        this.tryMax = tryMax;
        this.delete = delete;
        init();
    }

    private void init() {
        loopers.clear();
        ProcessLooper looper;
        for (int i = 0; i < maxLooper; i++) {
            final int index = i;
            looper = new ProcessLooper(this.getClass().getSimpleName() + "Looper_" + i, delete) {
                @Override
                public boolean onProcess(IMaterial material) {
                    return handleProcess(index, material);
                }

                @Override
                public void onComplete(int count) {
                    PipeQueue.this.onComplete(index, count);
                }
            };
            looper.setMaxTry(tryMax);
            loopers.add(looper);
        }
    }

    @Override
    public void apply(List<IMaterial> os) {
        if (null == os || os.isEmpty()) return;
        loopers.get(0).apply(os);
    }

    @Override
    public void apply(IMaterial o) {
        if (null == o) return;
        loopers.get(0).apply(o);
    }

    @Override
    public ILooper getLooper(int index) {
        if (index > -1 && index < maxLooper) {
            return loopers.get(index);
        }
        return null;
    }

    /**
     * 处理当前任务 并分发下个工序
     *
     * @param index
     * @param material
     */
    protected boolean handleProcess(int index, IMaterial material) {
        IMaterial result = onProcess(index, material);
        ILooper next = getLooper(index + 1);
        if (null != result && null != next) {//分发至下一工序
            next.apply(result);
        }
        return true;
    }


    @Override
    public void onComplete(int index, int count) {
        Logger.e("PipeQueue", "onComplete:" + count);
    }

    @Override
    public abstract IMaterial onProcess(int index, IMaterial material);
}
