package com.virtual.util.widget.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.virtual.util.widget.VViewStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class VVBaseActivity<Data extends VBasePackData, Presenter extends VBasePresenter<Data>>
        extends Activity implements VBaseView<Data> {

    protected VViewStorage mViewStorage;
    protected Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutResId = layoutResId();
        View contentView;
        if (layoutResId > -1) {
            contentView = LayoutInflater.from(this).inflate(layoutResId, null);
        } else {
            contentView = layoutView();
        }
        if (contentView == null) {
            TextView textV = new TextView(this);
            String hintText = "contentView not impl";
            textV.setText(hintText);
            contentView = textV;
        }
        mViewStorage = new VViewStorage(contentView);
        setContentView(contentView);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.setView(this);
        }
        initView();
        initData();
    }

    protected Presenter createPresenter() {
        return null;
    }

    protected @LayoutRes int layoutResId() {
        return -1;
    }

    protected View layoutView() {
        return null;
    }

    protected void initView() {

    }

    protected void initData() {

    }

    public @NonNull Intent getIntent() {
        Intent intent = super.getIntent();
        if (intent == null) {
            intent = new Intent();
        }
        return intent;
    }

    public <T extends View> T findView(@IdRes int viewId) {
        return mViewStorage.findView(viewId);
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void presenterCallback(int action, Data data) {
        if (action == VBasePackData.Action.SHOW_TOAST) {
            showToast(data.mToastMsg);
        }
    }

    protected void showToast(String toastMsg) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mResultPermissionCallbacks == null) {
            return;
        }
        ActivityResultCallback<Map<String, Boolean>> callback = mResultPermissionCallbacks.get(requestCode);
        if (callback != null) {
            mResultPermissionCallbacks.remove(requestCode);
            Map<String, Boolean> map = new HashMap<>();
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                map.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            callback.onActivityResult(map);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mResultActivityCallbacks == null) {
            return;
        }
        ActivityResultCallback<ActivityResult> callback = mResultActivityCallbacks.get(requestCode);
        if (callback != null) {
            mResultActivityCallbacks.remove(requestCode);
            callback.onActivityResult(new ActivityResult(resultCode, data));
        }
    }

    private SparseArray<ActivityResultCallback<Map<String, Boolean>>> mResultPermissionCallbacks;
    private SparseArray<ActivityResultCallback<ActivityResult>> mResultActivityCallbacks;

    protected void requestPermissions(String[] permissions,
                                      ActivityResultCallback<Map<String, Boolean>> callback) {
        int requestCode = Arrays.hashCode(permissions);
        if (mResultPermissionCallbacks == null) {
            mResultPermissionCallbacks = new SparseArray<>(2);
        }
        mResultPermissionCallbacks.put(requestCode, callback);
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    protected void startActivity(Intent intent,
                                 ActivityResultCallback<ActivityResult> callback) {
        int requestCode = intent.filterHashCode();
        if (mResultActivityCallbacks == null) {
            mResultActivityCallbacks = new SparseArray<>(2);
        }
        mResultActivityCallbacks.put(requestCode, callback);
        ActivityCompat.startActivityForResult(this, intent, requestCode, null);
    }
}
