package com.virtual.util.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    private static Toast sToast;
    private static Handler sHandler;

    public static void makeTextShortShow(Context context, CharSequence text) {
        makeTextShow(context, text, Toast.LENGTH_SHORT);
    }

    public static void makeTextLongShow(Context context, CharSequence text) {
        makeTextShow(context, text, Toast.LENGTH_LONG);
    }

    public static void makeTextShow(Context context, CharSequence text, int duration) {
        if (Looper.myLooper() == null) {
            if (sHandler == null) {
                sHandler = new Handler(Looper.getMainLooper());
            }
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    cancel();
                    sToast = Toast.makeText(context, text, duration);
                    sToast.show();
                }
            });
        } else {
            cancel();
            sToast = Toast.makeText(context, text, duration);
            sToast.show();
        }
    }

    private static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }
}
