package com.looper.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.looper.Logger;
import com.looper.interfaces.ILooper;
import com.looper.interfaces.IMaterial;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskLooper<M> extends HandlerThread implements ILooper<M> {
    private final static String TAG = "TaskLooper";
    private final static int CODE_APPLY = 70001;
    private final static int CODE_NEXT = 70002;
    private final static int CODE_NEXT_AND_DELETE = 70003;
    private final List<Material<M>> _materials = new ArrayList<>();
    private Handler loopHander;
    private int maxTry = 1;//最大尝试次数
    private boolean _delete;//执行next时是否删除
    //自定义字段
    public Object obj;

    public void setMaxTry(int maxTry) {
        this.maxTry = maxTry;
    }

    /**
     * @param name   名称
     * @param delete process时是否删除
     */
    public TaskLooper(String name, boolean delete) {
        super(TextUtils.isEmpty(name) ? "TaskLooper" : name);
        this._delete = delete;
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
                    next(_delete, m.delay);
                } else if (CODE_NEXT == what || CODE_NEXT_AND_DELETE == what) {
                    Material m = _pop(CODE_NEXT_AND_DELETE == what);
                    if (null != m) {
                        boolean next = onProcess(m);
                        if (next) {
                            next(_delete, m.delay);
                        }
                    } else {
                        onComplete(_materials.size());
                    }
                }
            }
        };
    }

    @Override
    public void apply(M o, long delay) {
        Logger.e(TAG, getName() + "apply");
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
        Logger.e(TAG, getName() + "applys");
        Material<List<M>> material = new Material(maxTry);
        material.material = os;
        material.delay = delay;

        Message msg = Message.obtain();
        msg.what = CODE_APPLY;
        msg.obj = material;
        loopHander.sendMessageDelayed(msg, 0);
    }

    public void next(long delay) {
        next(_delete, delay);
    }

    public void next(boolean delete, long delay) {
        if (delay < 0) delay = 0;
        loopHander.removeMessages(CODE_NEXT);
        loopHander.removeMessages(CODE_NEXT_AND_DELETE);
        Message msg = Message.obtain();
        msg.what = delete ? CODE_NEXT_AND_DELETE : CODE_NEXT;
        loopHander.sendMessageDelayed(msg, delay);
    }

    @Override
    public IMaterial<M> pop() {
        return _pop(false);
    }

    @Override
    public boolean remove(IMaterial<M> material) {
        synchronized (_materials) {
            return _materials.remove(material);
        }
    }

    @Override
    public void clear() {
        synchronized (_materials) {
            _materials.clear();
        }
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
                    m.count = m.count + 1;
                    break;
                }
            }
            if (delete && null != material) {
                _materials.remove(material);
            }
        }
        return material;
    }


    @Override
    public void onComplete(int count) {
    }

    @Override
    public abstract boolean onProcess(IMaterial<M> material);

}
