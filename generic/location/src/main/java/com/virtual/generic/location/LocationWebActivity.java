package com.virtual.generic.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import java.net.URLDecoder;

public class LocationWebActivity extends Activity {
    private static final String TAG = "LocationWebActivity";
    private String mResultKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mResultKey = intent.getStringExtra("result_key");
        }
        if (TextUtils.isEmpty(mResultKey)) {
            mResultKey = "scan_result";
        }

        setContentView(initView(this));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private View initView(Context context) {
        FrameLayout content = new FrameLayout(context);

        String url = "https://apis.map.qq.com/tools/locpicker?search=1&type=0&backurl=http://callback&key=RTNBZ-76S64-IYWUB-D5WRM-JBXEK-R6FLS&referer=MyApp";
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
                                    resultIntent.putExtra(mResultKey, latng);
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
        return content;
    }

    public static void start(Activity activity, String result_key, int request_code) {
        Intent intent = new Intent(activity, LocationWebActivity.class);
        intent.putExtra("result_key", result_key);
        activity.startActivityForResult(intent, request_code);
    }
}
