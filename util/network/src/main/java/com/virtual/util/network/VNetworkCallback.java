package com.virtual.util.network;

import androidx.annotation.NonNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class VNetworkCallback implements Callback<ResponseBody> {

    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        try {
            ResponseBody body = response.body();
            if (body != null) {
                String url = call.request().url().toString();
                onResponseString(url, body.string());
            } else {
                onFailure(call, new NullPointerException("body is null."));
            }
        } catch (Throwable throwable) {
            onFailure(call, throwable);
        }
    }

    public void onResponseString(String url, String result) throws Throwable {

    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
        String url = call.request().url().toString();
        onFailure(url, throwable);
    }

    public void onFailure(String url, Throwable throwable) {

    }
}
