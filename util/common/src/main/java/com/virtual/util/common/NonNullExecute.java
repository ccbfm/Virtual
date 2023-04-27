package com.virtual.util.common;

import android.util.Log;

import androidx.annotation.NonNull;

public class NonNullExecute {

    public static <T> void execute(T obj, @NonNull IExecute<T> execute) {
        if (obj == null) {
            Log.w("NullExecute", "obj is null. execute: " + execute.getClass());
        } else {
            execute.execute(obj);
        }
    }

    public interface IExecute<T> {
        void execute(@NonNull T obj);
    }
}
