package com.looper;

import java.util.ArrayList;
import java.util.List;

/**
 * 非线性队列
 * 队列1独立运行  队列1数据处理结果apply到队列2.
 * 队列2独立运行
 */
public class CurveQueue {
    private final static CurveQueue _queue = new CurveQueue();

    public static CurveQueue getQueue() {
        return _queue;
    }

    private TaskLooper<String> fitstLooper;
    private TaskLooper<String> secondLooper;

    private CurveQueue() {
        fitstLooper = new TaskLooper<String>("First-Looper");
        fitstLooper.setProcessor(new IProcessor<String>() {
            @Override
            public boolean onProcess(IMaterial<String> material) {
                String result = material.getMaterial() + "_process1";//模拟fitstLooper数据处理
                Logger.e("TaskQueue", "process1 result = " + result);
                secondLooper.apply(result, 0);//向队列2添加原料
                // TODO: 2/23/21 自动触发next
                return true;
            }
        });
        secondLooper = new TaskLooper<String>("Second-Looper");
        secondLooper.setProcessor(new IProcessor<String>() {
            @Override
            public boolean onProcess(IMaterial<String> material) {
                String result = material.getMaterial() + "_process2";//模拟secondLooper数据处理
                Logger.e("TaskQueue", "process2 result = " + result);
                // TODO: 2/23/21 自动触发next
                secondLooper.next(TaskLooper.DEF_DELETE, 0);
                return false;
            }
        });
    }

    public void testApply() {
        List<String> os = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            os.add("material_test_" + i);
        }
        boolean ok = apply(os);
        Logger.e("TaskQueue", "apply ok = " + ok);
    }

    /**
     * 第一步：第一个队列添加原料
     *
     * @param os
     * @return
     */
    public boolean apply(List<String> os) {
        if (null == fitstLooper || null == os || os.isEmpty()) return false;
        fitstLooper.apply(os, 0);
        return true;
    }


    private void process1(IMaterial<String> material) {
        // TODO: 2/23/21 模拟first looper 数据处理
        String result = material.getMaterial() + "_process1";
        Logger.e("TaskQueue", "process1 result = " + result);
        secondLooper.apply(result, 0);

    }

    private void process2(IMaterial<String> material) {
        // TODO: 2/23/21 模拟second looper 数据处理
        String result = material.getMaterial() + "_process2";
        Logger.e("TaskQueue", "process2 result = " + result);
    }
}
