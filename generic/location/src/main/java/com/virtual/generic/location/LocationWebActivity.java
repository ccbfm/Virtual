package com.virtual.generic.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.net.URLDecoder;

public class LocationWebActivity extends Activity {
    private static final String TAG = "LocationWebActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(initView(this));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private View initView(Context context) {
        FrameLayout content = new FrameLayout(context);
        //RTNBZ-76S64-IYWUB-D5WRM-JBXEK-R6FLS
        String url = "https://apis.map.qq.com/tools/locpicker?search=1&type=0&backurl=http://callback&key=RTNBZ-76S64-IYWUB-D5WRM-JBXEK-R6FLS&referer=MyApp";
        //String url = "https://mapapi.qq.com/web/mapComponents/locationPicker/v/index.html?search=1&type=0&backurl=http://callback&key=00e3d061e7debe5f88aec44e0b549b76&referer=MyApp";
        WebView webView = new WebView(context);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request != null) {
                    Uri url = request.getUrl();
                    if (url != null) {
                        boolean flag = url.toString().startsWith("http://callback");
                        if (flag) {
                            try {
                                String decode = URLDecoder.decode(url.toString(), "UTF-8");
                                Uri uri = Uri.parse(decode);
                                String latng = uri.getQueryParameter("latng");
                                Log.d(TAG, "shouldOverrideUrlLoading-latng=" + latng);
                                if (!TextUtils.isEmpty(latng)) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("location_action", "ok");
                                    resultIntent.putExtra("location_result", latng);
                                    setResult(RESULT_OK, resultIntent);
                                    LocationWebActivity.this.finish();
                                    return true;
                                }
                            } catch (Throwable th) {
                                Log.e(TAG, "shouldOverrideUrlLoading Throwable", th);
                            }
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.loadUrl(url);

        content.addView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        int hp = getResources().getDisplayMetrics().heightPixels;
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText("取消");
        int tw = hp >> 4;
        textView.setBackground(createCircleDrawable((tw >> 1)));
        FrameLayout.LayoutParams tLp = new FrameLayout.LayoutParams(tw, tw);
        tLp.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        content.addView(textView, tLp);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location_action", "cancel");
                resultIntent.putExtra("location_result", "");
                setResult(RESULT_OK, resultIntent);
                LocationWebActivity.this.finish();
            }
        });

        return content;
    }

    private static GradientDrawable createCircleDrawable(float radius) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        //设置背景色
        background.setColor(0xFFFFFFFF);
        //设置边线粗细，颜色
        background.setStroke(2, 0xFF000000);
        //设置圆角弧度
        background.setCornerRadius(radius);
        return background;
    }

    /**
     * 返回结果字段 location_result
     */
    public static void start(Activity activity, int request_code) {
        Intent intent = new Intent(activity, LocationWebActivity.class);
        activity.startActivityForResult(intent, request_code);
    }
}
