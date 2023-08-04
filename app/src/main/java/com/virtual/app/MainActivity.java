package com.virtual.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.virtual.mutual.simple.launcher.Launcher;
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
                Intent intent = new Intent(MainActivity.this, Launcher.class);
                MainActivity.this.startActivity(intent);

                /*Intent mIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(mIntent);*/
            }
        });
        VLog.i("app", "MainActivity-onCreate " + Process.myUid() + " " + Process.myUserHandle().toString());
    }

}