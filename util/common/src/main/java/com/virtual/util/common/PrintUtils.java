package com.virtual.util.common;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class PrintUtils {
    private static final String TAG = "PrintUtils";

    public static void pConstructors(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> c : constructors) {
            c.setAccessible(true);
            Class<?>[] classes = c.getParameterTypes();
            Log.d(TAG, "pConstructors parameterTypes " + Arrays.toString(classes));
        }
    }

    public static void pFieldsAllSuper(Object obj) {
        if (obj != null) {
            pFieldsAllSuper(obj.getClass(), obj);
        }
    }

    public static void pFieldsAllSuper(Class<?> clazz, Object obj) {
        if (obj != null) {
            if (clazz == null) {
                clazz = obj.getClass();
            }
            if (clazz != Object.class) {
                pFields(clazz, obj);
                pFieldsAllSuper(clazz.getSuperclass(), obj);
            }
        }
    }

    public static void pFields(Object obj) {
        if (obj != null) {
            pFields(obj.getClass(), obj);
        }
    }

    public static void pFields(Class<?> clazz, Object obj) {
        if (clazz == null || obj == null) {
            return;
        }
        try {
            Field[] fields = clazz.getDeclaredFields();
            Log.d(TAG, "pFields start " + clazz);
            for (Field f : fields) {
                f.setAccessible(true);
                try {
                    Log.d(TAG, f.getName() + " = " + f.get(obj));
                } catch (Throwable ignore) {
                    Log.d(TAG, f.getName() + " = ");
                }

            }
            Log.d(TAG, "pFields end ");
        } catch (Throwable th) {
            Log.w(TAG, "pFields Throwable ", th);
        }
    }

}
