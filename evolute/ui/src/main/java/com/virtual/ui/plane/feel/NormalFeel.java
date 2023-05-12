package com.virtual.ui.plane.feel;

import android.content.Context;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.virtual.ui.plane.feel.data.FeelData;

public class NormalFeel extends BaseFeel<AppCompatTextView, FeelData> {
    private boolean mHasText = false;
    private boolean mIsScrolled = false;

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
        textView.setGravity(Gravity.BOTTOM);
        textView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                AppCompatTextView tv = (AppCompatTextView) v;
                Layout layout = tv.getLayout();
                int lineCount = layout.getLineCount();
                int lastLineTop = layout.getLineTop(lineCount);
                int scrollTop = tv.getHeight() + scrollY;
                //Log.d("NormalFeel", "onScrollChange lastLineTop " + lastLineTop + " " + scrollTop);
                mIsScrolled = lastLineTop != scrollTop;
                tv.setGravity(mIsScrolled ? Gravity.START : Gravity.BOTTOM);
            }
        });
        return textView;
    }

    public void appendText(String text) {
        if (mHasText) {
            mFeelView.append("\n");
        } else {
            mHasText = true;
        }

        mFeelView.append(text);
    }

    public void clearText() {
        mHasText = false;
        mFeelView.setText("");
    }

    @Override
    public void onChange(@NonNull FeelData feelData) {
        appendText(feelData.format());
    }
}
