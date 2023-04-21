package com.virtual.util.context;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

public final class VResources {

    public static Resources system() {
        return Resources.getSystem();
    }

    public static Resources app() {
        return VContextHolder.instance().getContext().getResources();
    }

    public static DisplayMetrics displayMetrics() {
        return system().getDisplayMetrics();
    }

    public static int[] screenSize() {
        int[] size = new int[2];
        DisplayMetrics dm = displayMetrics();
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    public static int[] locationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    public static int[] locationInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    public static View layoutIdToView(int id) {
        return VContextHolder.instance().getLayoutInflater().inflate(id, null);
    }

    public static int dpToPx(final float dpValue) {
        final float scale = system().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int pxToDp(final float pxValue) {
        final float scale = system().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int spToPx(final float spValue) {
        final float fontScale = system().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int pxToSp(final float pxValue) {
        final float fontScale = system().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public interface OnGetSizeListener {
        void onGetSize(View view);
    }

    public static void forceGetViewSize(final View view, final OnGetSizeListener listener) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onGetSize(view);
                }
            }
        });
    }
}
