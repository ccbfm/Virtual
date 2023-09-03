package com.virtual.use;

import android.app.Application;

import com.virtual.util.log.VLogLevel;
import com.virtual.util.log.VLogConfig;
import com.virtual.util.log.VLogExported;
import com.virtual.util.persist.VPersistExported;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VLogConfig.instance().createBuilder(this)
                .setLogTag("Virtual_Use")
                .setDebugLevel(BuildConfig.DEBUG ? VLogLevel.D : VLogLevel.NONE)
                .setSaveLevel(VLogLevel.I)
                .build();
        VLogExported.init("com.virtual.app");
        VPersistExported.init("com.virtual.app");
        VLogExported.d("App", "onCreate");

        new Thread(){
            @Override
            public void run() {
                try {
                    while (true){
                        Thread.sleep(1000);
                        VLogExported.d("App", "onCreate-run");
                    }
                } catch (Throwable throwable){
                    VLogExported.d("App", "onCreate-run throwable " + throwable.getMessage());
                }
            }
        }.start();
    }

}
