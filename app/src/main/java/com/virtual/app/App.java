package com.virtual.app;

import android.app.Application;
import android.util.Log;

import com.virtual.util.log.VLogLevel;
import com.virtual.util.log.VLogConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("App", "onCreate");
        VLogConfig.instance().createBuilder(this)
                .setLogTag("Virtual")
                //.setDebugLevel(BuildConfig.DEBUG ? VLevel.D : VLevel.NONE)
                .setDebugLevel(VLogLevel.D)
                .setSaveLevel(VLogLevel.W)
                .build();
    }
}
