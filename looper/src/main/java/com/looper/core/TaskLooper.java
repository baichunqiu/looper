package com.looper.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.looper.Logger;
import com.looper.interfaces.ILooper;
import com.looper.interfaces.IProcessor;

import java.util.ArrayList;
import java.util.List;

public class TaskLooper<M> extends HandlerThread implements ILooper<M> {
    private final static String TAG = "TaskLooper";
    private final static int CODE_APPLY = 70001;
    private final static int CODE_NEXT = 70002;
    private final static int CODE_NEXT_AND_DELETE = 70003;
    public final static boolean DEF_DELETE = true;
    private final List<Material<M>> _materials = new ArrayList<>();
    private IProcessor<M> iProcessor;
    private Handler loopHander;

    public Object obj;//自定义字段
    private int maxTry = 1;//最大尝试次数

    public void setMaxTry(int maxTry) {
        this.maxTry = maxTry;
    }

    public TaskLooper(String name) {
        super(TextUtils.isEmpty(name) ? "TaskLooper" : name);
        start();
        loopHander = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (CODE_APPLY == what) {
                    Material m = (Material) msg.obj;
                    int count = 0;
                    Logger.e(TAG, getName() + " 剩余：" + _materials.size());
                    if (m.material instanceof List) {
                        count = _apply((List) m.material, m.delay);
                    } else {
                        count = _apply((M) m.material, m.delay);
                    }
                    Logger.e(TAG, getName() + " count：" + count);
                    next(DEF_DELETE, m.delay);
                } else if (CODE_NEXT == what || CODE_NEXT_AND_DELETE == what) {
                    Material m = _pop(CODE_NEXT_AND_DELETE == what);
                    if (null != m && null != iProcessor) {
                        boolean next = iProcessor.onProcess(m);
                        if (next) {
                            next(DEF_DELETE, m.delay);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void setProcessor(IProcessor<M> iProcessor) {
        this.iProcessor = iProcessor;
    }

    @Override
    public void apply(M o, long delay) {
        Material material = new Material(maxTry);
        material.material = o;
        material.delay = delay;

        Message msg = Message.obtain();
        msg.what = CODE_APPLY;
        msg.obj = material;
        loopHander.sendMessageDelayed(msg, 0);
    }

    @Override
    public void apply(List<M> os, long delay) {
        Material<List<M>> material = new Material(maxTry);
        material.material = os;
        material.delay = delay;

        Message msg = Message.obtain();
        msg.what = CODE_APPLY;
        msg.obj = material;
        loopHander.sendMessageDelayed(msg, 0);
    }

    @Override
    public void next(boolean delete, long delay) {
        if (delay < 0) delay = 0;
        loopHander.removeMessages(CODE_NEXT);
        loopHander.removeMessages(CODE_NEXT_AND_DELETE);
        Message msg = Message.obtain();
        msg.what = delete ? CODE_NEXT_AND_DELETE : CODE_NEXT;
        loopHander.sendMessageDelayed(msg, delay);
    }

    @Override
    public M pop() {
        Material<M> m = _pop(false);
        return null == m ? null : m.material;
    }

    /**
     * 当前线程执行
     *
     * @param o
     * @param delay
     * @return
     */
    private int _apply(M o, long delay) {
        if (null == o) return -1;
        synchronized (_materials) {
            Material ma = new Material(maxTry);
            ma.material = o;
            ma.delay = delay;
            if (!_materials.contains(ma)) {
                _materials.add(ma);
            }
        }
        return 1;
    }

    /**
     * 当前线程运行
     *
     * @param os
     * @param delay
     * @return 添加记录数
     */
    private int _apply(List<M> os, long delay) {
        int len = null == os ? 0 : os.size();
        if (len < 1) return -1;
        int count = 0;
        synchronized (_materials) {
            Material materi;
            for (int i = 0; i < len; i++) {
                materi = new Material(maxTry);
                materi.material = os.get(i);
                materi.delay = delay;
                if (!_materials.contains(materi)) {
                    _materials.add(materi);
                    count++;
                }
            }
        }
        return count;
    }

    private Material _pop(boolean delete) {
        Material material = null;
        synchronized (_materials) {
            int len = _materials.size();
            for (int i = 0; i < len; i++) {
                Material m = _materials.get(i);
                if (m.available()) {
                    material = m;
                    m.count++;
                    break;
                }
            }
            if (delete && null != material) {
                _materials.remove(material);
            }
        }
        return material;
    }
}
