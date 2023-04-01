package com.ky.android.photo;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author keke
 * 用于获取全局的application
 */
public class AppGlobals {

    private static final String TAG = "AppGlobals";

    private static Application mApplication = null;

    public static Application get() {
        if (mApplication != null) {
            return mApplication;
        }
        try {
            Class atClass = Class.forName("android.app.ActivityThread");
            Method currentApplicationMethod = atClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            mApplication = (Application) currentApplicationMethod.invoke(null);
        } catch (Exception e) {
            Log.d(TAG, "e:" + e.toString());
        }

        if (mApplication != null) {
            return mApplication;
        }

        try {
            Class atClass = Class.forName("android.app.AppGlobals");
            Method currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
            currentApplicationMethod.setAccessible(true);
            mApplication = (Application) currentApplicationMethod.invoke(null);
        } catch (Exception e) {
            Log.d(TAG, "e:" + e.toString());
        }
        return mApplication;
    }
}
