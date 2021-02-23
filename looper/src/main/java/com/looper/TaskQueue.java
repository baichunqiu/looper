package com.looper;

import java.util.ArrayList;
import java.util.List;

/**
 * 线性队列
 * 调用和执行流程
 * 1.TaskQueue.apply() -> 最终执行fitstLooper.apply 向第一个队列添加原料 -> 被动执行fitstLooper.next()
 * 2.fitstLooper.onProcess()回调 -> process1() -> fitstLooper处理完成
 * 3.secondLooper.apply() -> 被动执行secondLooper.next()
 * 4.secondLooper.onProcess()回调 -> process2() -> secondLooper处理完成 -> 手动执行fitstLooper.next()
 */
public class TaskQueue {
    private final static TaskQueue _queue = new TaskQueue();

    public static TaskQueue getQueue() {
        return _queue;
    }

    private TaskLooper<String> fitstLooper;
    private TaskLooper<String> secondLooper;

    private TaskQueue() {
        fitstLooper = new TaskLooper<String>("First-Looper");
        fitstLooper.setProcessor(new IProcessor<String>() {
            @Override
            public boolean onProcess(IMaterial<String> material) {
                process1(material);
                // TODO: 2/23/21 此处不能自动next执行 所以 return false
                //  在第二个队列处理完成后 手动触发 next
                return false;
            }
        });
        secondLooper = new TaskLooper<String>("Second-Looper");
        secondLooper.setProcessor(new IProcessor<String>() {
            @Override
            public boolean onProcess(IMaterial<String> material) {
                process2(material);
                // TODO: 2/23/21 此处不自动触发next 在第一个队列处理完毕后 调用apply被动触发
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
        fitstLooper.apply(os, 500);
        return true;
    }


    private void process1(IMaterial<String> material) {
        // TODO: 2/23/21 模拟first looper 数据处理
        String result = material.getMaterial() + "_process1";
        Logger.e("TaskQueue", "process1 result = " + result);
        secondLooper.apply(result, 1000);

    }

    private void process2(IMaterial<String> material) {
        // TODO: 2/23/21 模拟second looper 数据处理
        String result = material.getMaterial() + "_process2";
        Logger.e("TaskQueue", "process2 result = " + result);
        fitstLooper.next(TaskLooper.DEF_DELETE,material.getDelay());
    }
}
