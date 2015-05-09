package com.ctcstudio.ebooktemplate.utils;

import android.util.Log;

/**
 * Created by HungHN on 5/9/2015.
 */
public class LogUtils {
    public static void d(String tag, String msg) {
        if (Config.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable e) {
        if (Config.DEBUG) {
            Log.d(tag, msg, e);
        }
    }

    public static void e(String tag, String msg) {
        if (Config.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (Config.DEBUG) {
            Log.e(tag, msg, e);
        }
    }

    public static void i(String tag, String msg) {
        if (Config.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable e) {
        if (Config.DEBUG) {
            Log.i(tag, msg, e);
        }
    }

    public static void v(String tag, String msg) {
        if (Config.DEBUG) {
            Log.v(tag, msg);
        }
    }
}
