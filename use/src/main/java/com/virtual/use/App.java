package com.virtual.use;

import android.app.Application;

import com.virtual.util.log.VLogExported;
import com.virtual.util.log.VLogLevel;
import com.virtual.util.log.VLogManager;
import com.virtual.util.persist.VPersistExported;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VLogManager.instance().defaultConfig(this,
                "Virtual_Use",
                (BuildConfig.DEBUG ? VLogLevel.D : VLogLevel.NONE),
                VLogLevel.I);

        VLogExported.init("com.virtual.app");
        VPersistExported.init(this, "com.virtual.app");
        VLogExported.d("App", "onCreate");

        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        VLogExported.d("App", "onCreate-run");
                    }
                } catch (Throwable throwable) {
                    VLogExported.d("App", "onCreate-run throwable " + throwable.getMessage());
                }
            }
        }.start();
    }

}
