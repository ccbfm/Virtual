package com.virtual.app;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.virtual.util.context.VResources;
import com.virtual.util.log.VLog;
import com.virtual.util.network.VNetwork;
import com.virtual.util.network.VNetworkCallback;
import com.vritual.mutual.live.wallpaper.VirtualWallpaper;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

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
                VNetwork.setCleartext();
                VNetwork.init();

                VNetwork.runPost("http://111.223.15.84:3307/api/device/getappversion",
                        new VNetworkCallback(){
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                super.onResponse(call, response);
                                try {
                                    Log.d("Virtual", "app onClick runGet " + response.body().string());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                /*new Thread(){
                    @Override
                    public void run() {
                        Log.d("Virtual", "app onClick get " + VNetwork.post("http://111.223.15.84:3307/api/device/getappversion"));
                    }
                }.start();*/
            }
        });

        VLog.i("app", "MainActivity-onCreate " + VirtualWallpaper.isWallpaper(this));
        VLog.i("app", "MainActivity-onCreate " + Arrays.toString(VResources.screenSize()));
        VLog.i("app", "MainActivity-onCreate " + Process.myUid() + " " + Process.myUserHandle().toString());
    }

}