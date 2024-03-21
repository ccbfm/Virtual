package com.virtual.util.widget.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.util.widget.VViewStorage;

public abstract class BaseActivity<Data extends BasePackData> extends Activity implements BaseView<Data> {

    protected VViewStorage mViewStorage;
    protected BasePresenter<Data> mPresenter;

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
        initView();
        initData();
    }

    protected BasePresenter<Data> createPresenter() {
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

    public <T extends View> T getView(@IdRes int viewId) {
        return mViewStorage.getView(viewId);
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void presenterCallback(int action, @NonNull Data data) {
        if (action == BasePackData.Action.SHOW_TOAST) {
            Toast.makeText(this, data.mToastMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }
}
