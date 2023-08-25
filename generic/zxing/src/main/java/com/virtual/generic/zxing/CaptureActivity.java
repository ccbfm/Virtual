package com.virtual.generic.zxing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public final class CaptureActivity extends Activity {
    private static final String TAG = "CaptureActivity";
    private ZXingView mZXingView;
    private String mResultKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.layout_capture);
        Intent intent = getIntent();
        if (intent != null) {
            mResultKey = intent.getStringExtra("result_key");
        }
        if (TextUtils.isEmpty(mResultKey)) {
            mResultKey = "scan_result";
        }

        mZXingView = findViewById(R.id.zxing_scan_view);
        mZXingView.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                Log.d(TAG, "onScanQRCodeSuccess ResultKey: " + mResultKey);
                vibrate();

                Intent intent = new Intent();
                intent.putExtra(mResultKey, result);
                setResult(RESULT_OK, intent);
                CaptureActivity.this.finish();
            }

            @Override
            public void onCameraAmbientBrightnessChanged(boolean isDark) {
                Log.d(TAG, "onCameraAmbientBrightnessChanged: " + isDark);
                if (isDark) {
                    mZXingView.getScanBoxView().setTipText(getString(R.string.scan_dark_hints));
                } else {
                    mZXingView.getScanBoxView().setTipText(getString(R.string.scan_normal_hints));
                }
            }

            @Override
            public void onScanQRCodeOpenCameraError() {
                Log.d(TAG, "onScanQRCodeOpenCameraError");
            }
        });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, 123));
        } else {
            vibrator.vibrate(200);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.showScanRect(); // 显示扫描框，
    }

    @Override
    protected void onResume() {
        super.onResume();
        mZXingView.startSpot();// 开始识别
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mZXingView.onDestroy(); // 销毁二维码扫描控件
    }

    public static void start(Activity activity, String result_key, int request_code) {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    android.Manifest.permission.CAMERA,
            }, 0x11);
            return;
        }
        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.putExtra("result_key", result_key);
        activity.startActivityForResult(intent, request_code);
    }


}
