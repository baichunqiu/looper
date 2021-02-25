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

/**
 * 当个流程工序处理队列
 *
 * @param <M>
 */
public abstract class ProcessLooper<M> extends HandlerThread implements ILooper<M> {
    private final static String TAG = "TaskLooper";
    private final static int CODE_APPLY = 70001;
    private final static int CODE_NEXT = 70002;
    private final static int CODE_NEXT_AND_DELETE = 70003;
    private final List<IMaterial<M>> _materials = new ArrayList<>();
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
    public ProcessLooper(String name, boolean delete) {
        super(TextUtils.isEmpty(name) ? "TaskLooper" : name);
        this._delete = delete;
        start();
        loopHander = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (CODE_APPLY == what) {
                    Object o = msg.obj;
                    int count = 0;
                    Logger.e(TAG, getName() + " 剩余：" + _materials.size());
                    if (o instanceof IMaterial) {
                        _apply((IMaterial) o);
                    } else if (o instanceof List) {
                        _apply((List<IMaterial>) o);
                    }
                    Logger.e(TAG, getName() + " count：" + count);
                    next(_delete, 0);
                } else if (CODE_NEXT == what || CODE_NEXT_AND_DELETE == what) {
                    IMaterial m = _pop(CODE_NEXT_AND_DELETE == what);
                    if (null != m) {
                        boolean next = onProcess(m);
                        if (next) {
                            next(_delete, m.delay());
                        }
                    } else {
                        onComplete(_materials.size());
                    }
                }
            }
        };
    }

    @Override
    public void apply(IMaterial<M> material) {
        Message msg = Message.obtain();
        msg.what = CODE_APPLY;
        msg.obj = material;
        loopHander.sendMessage(msg);
    }

    @Override
    public void apply(List<IMaterial<M>> os) {
        Message msg = Message.obtain();
        msg.what = CODE_APPLY;
        msg.obj = os;
        loopHander.sendMessage(msg);
    }

    @Override
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
     * @param m
     * @return
     */
    private int _apply(IMaterial m) {
        if (null == m) return -1;
        synchronized (_materials) {
            if (!_materials.contains(m)) {
                _materials.add(m);
            }
        }
        return 1;
    }

    /**
     * 当前线程运行
     *
     * @param ms
     * @return 添加记录数
     */
    private int _apply(List<IMaterial> ms) {
        int len = null == ms ? 0 : ms.size();
        if (len < 1) return -1;
        int count = 0;
        synchronized (_materials) {
            for (int i = 0; i < len; i++) {
                IMaterial m = ms.get(i);
                if (!_materials.contains(m)) {
                    _materials.add(m);
                    count++;
                }
            }
        }
        return count;
    }

    private IMaterial _pop(boolean delete) {
        IMaterial material = null;
        synchronized (_materials) {
            int len = _materials.size();
            for (int i = 0; i < len; i++) {
                IMaterial m = _materials.get(i);
                if (m.available()) {
                    material = m;
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
