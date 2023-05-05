package com.virtual.ui.plane.feel;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

public class NormalFeel extends BaseFeel<AppCompatTextView, Object> {

    public NormalFeel(@NonNull Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected AppCompatTextView createFeel(@NonNull Context context) {
        float t_size = mHeight / 40.f;
        AppCompatTextView textView = new AppCompatTextView(context);
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setTextSize(t_size);
        textView.setText("[叶凡]来了");
        return textView;
    }

    public void append(String text) {
        mFeelView.append(text);
        mFeelView.append("\n");
    }

    @Override
    public void onChange(Object o) {
        for (int i = 0; i < 40; i++) {
            append("啦啦啦 " + i);
        }
    }
}
