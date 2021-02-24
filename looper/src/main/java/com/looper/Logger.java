package com.looper;

import android.util.Log;

public class Logger {
    public static void e(String tag, Object obj) {
        Log.e(tag, obj.toString());
    }

}
