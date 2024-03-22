package com.virtual.util.native_shell;

public class NativeShell {

    // Used to load the 'native_shell' library on application startup.
    static {
        System.loadLibrary("native_shell");
    }

    /**
     * A native method that is implemented by the 'native_shell' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String imei();
}