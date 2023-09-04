package com.virtual.util.common;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast sToast;

    public static void makeTextShortShow(Context context, CharSequence text) {
        cancel();
        sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        sToast.show();
    }

    public static void makeTextLongShow(Context context, CharSequence text) {
        cancel();
        sToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        sToast.show();
    }

    private static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }
}
