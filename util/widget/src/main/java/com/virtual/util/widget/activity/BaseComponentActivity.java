package com.virtual.util.widget.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.util.widget.VViewStorage;

public abstract class BaseComponentActivity<Data extends BasePackData, Presenter extends BasePresenter<Data>>
        extends ComponentActivity implements BaseView<Data> {

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

    public <T extends View> T getView(@IdRes int viewId) {
        return mViewStorage.getView(viewId);
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void presenterCallback(int action, Data data) {
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
