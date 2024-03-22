package com.virtual.app;

import android.app.Application;
import android.util.Log;

import com.kongzue.dialogx.DialogX;
import com.virtual.util.log.VLogLevel;
import com.virtual.util.log.VLogManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("App", "onCreate");
        VLogManager.instance().defaultConfig(this, "Virtual", VLogLevel.D, VLogLevel.W);
        DialogX.init(this);
    }
}
