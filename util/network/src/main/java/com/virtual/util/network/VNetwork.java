package com.virtual.util.network;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class VNetwork {

    /**
     * 设置 可以用http访问
     */
    public static void setCleartext() {
        try {
            Class<?> classNSP = Class.forName("android.security.NetworkSecurityPolicy");
            Object objNSP = classNSP.getMethod("getInstance").invoke(null);
            classNSP.getMethod("setCleartextTrafficPermitted", boolean.class).invoke(objNSP, true);
        } catch (Throwable throwable) {
            Log.e("VNetwork", "setCleartext Throwable ", throwable);
        }
    }

    public static void init() {
        init(null);
    }

    public static void init(OkHttpClient client) {
        VNetManager.instance().initDefault(client);
        VNetManager.instance().putApi(VApi.class);
    }

    private static boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            Log.e("VNetwork", "checkUrl url is null.");
            return true;
        }
        if (!url.startsWith("http")) {
            Log.e("VNetwork", "checkUrl url startsWith not http.");
            return true;
        }
        return false;
    }

    public static String get(String url) {
        return get(url, null, null);
    }

    /**
     * @param url    url
     * @param params 参数
     * @return 结果
     */
    public static String get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    /**
     * @param url     url
     * @param params  参数
     * @param headers 请求头
     * @return 结果
     */
    public static String get(String url, Map<String, Object> params, Map<String, Object> headers) {
        if (checkUrl(url)) {
            return null;
        }
        if (params == null) {
            params = new HashMap<>();
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        try {
            VApi api = VNetManager.instance().getApi(VApi.class);
            if (api != null) {
                Response<ResponseBody> response = api.get(url, params, headers).execute();
                ResponseBody body = response.body();
                if (body != null) {
                    return body.string();
                }
            }
        } catch (IOException ioe) {
            Log.e("VNetwork", "get Exception ", ioe);
        }
        return null;
    }

    public static void runGet(String url, VNetworkCallback callback) {
        runGet(url, null, null, callback);
    }

    /**
     * @param url      url
     * @param params   参数
     * @param callback callback
     */
    public static void runGet(String url, Map<String, Object> params, VNetworkCallback callback) {
        runGet(url, params, null, callback);
    }

    /**
     * @param url      url
     * @param params   参数
     * @param headers  请求头
     * @param callback callback
     */
    public static void runGet(String url,
                              Map<String, Object> params,
                              Map<String, Object> headers,
                              VNetworkCallback callback) {
        if (checkUrl(url)) {
            return;
        }
        if (params == null) {
            params = new HashMap<>();
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        VApi api = VNetManager.instance().getApi(VApi.class);
        if (api != null) {
            api.get(url, params, headers).enqueue(callback);
        }
    }

    public static String post(String url) {
        return post(url, null, null);
    }

    /**
     * @param url    url
     * @param params 参数
     * @return 结果
     */
    public static String post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    /**
     * @param url     url
     * @param params  参数
     * @param headers 请求头
     * @return 结果
     */
    public static String post(String url, Map<String, Object> params, Map<String, Object> headers) {
        if (checkUrl(url)) {
            return null;
        }
        if (params == null) {
            params = new HashMap<>();
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        try {
            VApi api = VNetManager.instance().getApi(VApi.class);
            if (api != null) {
                Response<ResponseBody> response = api.post(url, params, headers).execute();
                ResponseBody body = response.body();
                if (body != null) {
                    return body.string();
                }
            }
        } catch (IOException ioe) {
            Log.e("VNetwork", "post Exception ", ioe);
        }
        return null;
    }

    public static void runPost(String url, VNetworkCallback callback) {
        runPost(url, null, null, callback);
    }

    /**
     * @param url      url
     * @param params   参数
     * @param callback callback
     */
    public static void runPost(String url, Map<String, Object> params, VNetworkCallback callback) {
        runPost(url, params, null, callback);
    }

    /**
     * @param url      url
     * @param params   参数
     * @param headers  请求头
     * @param callback callback
     */
    public static void runPost(String url,
                               Map<String, Object> params,
                               Map<String, Object> headers,
                               VNetworkCallback callback) {
        if (checkUrl(url)) {
            return;
        }
        if (params == null) {
            params = new HashMap<>();
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        VApi api = VNetManager.instance().getApi(VApi.class);
        if (api != null) {
            api.post(url, params, headers).enqueue(callback);
        }
    }
}
