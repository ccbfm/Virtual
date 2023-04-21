package com.virtual.use;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.virtual.util.log.VLog;
import com.virtual.util.log.VLogExported;
import com.virtual.util.persist.VPersistExported;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Virtual", "use onClick " + v);
                dddd();
            }
        });
        // VLogExported.i("Use", "MainActivity-onCreate");
        Log.d("Virtual", "Use MainActivity-onCreate");

        dddd();
    }

    private void dddd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Uri uri = VLogExported.providerUri("com.virtual.app");
                //MainActivity.this.getContentResolver().acquireUnstableContentProviderClient()
                Log.d("Virtual", "use th s");
                long t = System.nanoTime();
                VLogExported.i("Use", "MainActivity-onCreate-1-Thread");
                Log.d("Virtual", "use th e " + (System.nanoTime() - t));

                Log.d("Virtual", "use th s2");
                long t2 = System.nanoTime();
                VLog.i("Use", "MainActivity-onCreate-2-Thread");
                Log.d("Virtual", "use th e2 " + (System.nanoTime() - t2));

                VPersistExported.setValue("Virtual_Use", "Virtual_Use");
                Log.d("Virtual", "use th getValue " + VPersistExported.getValue("Virtual_Use"));
            }
        }).start();
    }
}