package com.looper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskLooper<T> extends HandlerThread implements ILooper<T> {
    private final static int CODE_APPLY = 70001;
    private final static int CODE_NEXT = 70002;
    private final static int CODE_NEXT_AND_DELETE = 70003;
    public final static boolean DEF_DELETE = true;
    private final List<IMaterial<T>> _materials = new ArrayList<>();

    private IProcessor<T> iProcessor;
    private Handler loopHander;

    public TaskLooper(String name) {
        super(TextUtils.isEmpty(name) ? "TaskLooper" : name);
        start();
        loopHander = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (CODE_APPLY == what) {
                    IMaterial m = (Material) msg.obj;
                    int count = 0;
                    Logger.e("TaskLooper", getName() + " 剩余：" + _materials.size());
                    if (m.getMaterial() instanceof List) {
                        count = _apply((List) m.getMaterial(), m.getDelay());
                    } else {
                        count = _apply((T) m.getMaterial(), m.getDelay());
                    }
                    Logger.e("TaskLooper", getName() + " count：" + count);
                    next(DEF_DELETE, m.getDelay());
                } else if (CODE_NEXT == what || CODE_NEXT_AND_DELETE == what) {
                    IMaterial m = _pop(CODE_NEXT_AND_DELETE == what);
                    if (null != m && null != iProcessor) {
                        boolean next = iProcessor.onProcess(m);
                        if (next) {
                            next(DEF_DELETE, m.getDelay());
                        }
                    }
                }
            }
        };
    }

    @Override
    public void setProcessor(IProcessor<T> iProcessor) {
        this.iProcessor = iProcessor;
    }

    @Override
    public void apply(T o, long delay) {
        Material material = new Material();
        material.material = o;
        material.delay = delay;

        Message msg = Message.obtain();
        msg.what = CODE_APPLY;
        msg.obj = material;
        loopHander.sendMessageDelayed(msg, delay);
    }

    @Override
    public void apply(List<T> os, long delay) {
        Material<List<T>> material = new Material();
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


    private int _apply(T o, long delay) {
        if (null == o) {
            return -1;
        }
        synchronized (_materials) {
            Material ma = new Material();
            ma.material = o;
            ma.delay = delay;
            if (!_materials.contains(ma)) {
                _materials.add(ma);
            }
        }
        return 1;
    }

    private int _apply(List<T> os, long delay) {
        int len = null == os ? 0 : os.size();
        if (len < 1) return -1;
        int count = 0;
        synchronized (_materials) {
            Material materi;
            for (int i = 0; i < len; i++) {
                materi = new Material();
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

    private IMaterial _pop(boolean delete) {
        IMaterial material = null;
        synchronized (_materials) {
            int len = _materials.size();
            for (int i = len - 1; i > -1; i--) {
                IMaterial m = _materials.get(i);
                if (m.available()) {
                    material = m;
                    m.countPlus();
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
