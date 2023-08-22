package com.virtual.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.virtual.util.log.VLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View view = findViewById(R.id.text);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Virtual", "app onClick " + v);
                /*Intent intent = new Intent(MainActivity.this, Launcher.class);
                MainActivity.this.startActivity(intent);*/

                /*Intent mIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(mIntent);*/

                //VirtualWallpaper.start(MainActivity.this);
                /*Intent intent = new Intent("android.intent.action.MAIN_USE");
                intent.setPackage("com.virtual.use");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);*/
            }
        });

        VLog.i("app", "MainActivity-onCreate " + Process.myUid() + " " + Process.myUserHandle().toString());
    }

}