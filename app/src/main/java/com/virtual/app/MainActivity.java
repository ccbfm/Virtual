package com.virtual.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.virtual.util.log.VLog;
import com.virtual.util.network.VDownloadManager;

import java.util.Arrays;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View view = findViewById(R.id.text);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Virtual", "app onClick " + v);

                testDownload();

                //LocationWebActivity.start(MainActivity.this, "xxxxxx", 1);
                //CaptureActivity.start(MainActivity.this, 1, 0x11);

                /*Intent mIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(mIntent);*/

                //VirtualWallpaper.start(MainActivity.this);
                /*Intent intent = new Intent("android.intent.action.MAIN_USE");
                intent.setPackage("com.virtual.use");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);*/
            }
        });

        VLog.w("app", "MainActivity-onCreate " + Process.myUid() + " " + Process.myUserHandle().toString());
    }

    private void testDownload() {
        String path = getExternalFilesDir("apk") + "/app-yunguanjia-release.apk";
        if (VDownloadManager.instance().isDownload(path)) {
            VDownloadManager.instance().stopDownload(path);
        } else {
            VDownloadManager.instance().startDownload(path,
                    "https://img.qn.72ygj.com/72ygj/app-yunguanjia-release.apk",
                    true, new VDownloadManager.DownloadStatusAdapter() {
                        @Override
                        public void start(String path) {
                            super.start(path);
                            Log.d("Virtual", "VDownloadManager start " + path);
                        }

                        @Override
                        public void progress(String path, long curLen, long allLen) {
                            super.progress(path, curLen, allLen);
                            Log.d("Virtual", "VDownloadManager progress " + path + " " + curLen + " " + allLen);
                        }

                        @Override
                        public void end(boolean complete, String path) {
                            super.end(complete, path);
                            Log.d("Virtual", "VDownloadManager end " + path + " " + complete);
                        }

                        @Override
                        public void error(String path, String error) {
                            super.error(path, error);
                            Log.d("Virtual", "VDownloadManager error " + path + " " + error);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isShow = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
        VLog.i("app", "MainActivity-onRequestPermissionsResult " + isShow +
                " " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VLog.i("app", "MainActivity-onActivityResult " + requestCode + " " + resultCode + " " + data);
        if (data != null) {
            Bundle bundle = data.getExtras();
            for (String key : bundle.keySet()) {
                VLog.i("app", "MainActivity-onActivityResult " + key + " " + bundle.get(key));
            }
        }
    }

}