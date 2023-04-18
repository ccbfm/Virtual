package com.virtual.app;

import android.app.Application;

import com.virtual.util.log.VLevel;
import com.virtual.util.log.VLogConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VLogConfig.instance().createBuilder()
                .setLogTag("Virtual")
                .setDebugLevel(VLevel.D)
                .setSaveLevel(VLevel.I)
                .build();
    }
}
