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
                onResponseString(body.string());
            } else {
                onFailure(call, new NullPointerException("body is null."));
            }
        } catch (Throwable throwable) {
            onFailure(call, throwable);
        }
    }

    public void onResponseString(String result) {

    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
        onFailure(throwable);
    }

    public void onFailure(Throwable throwable) {

    }
}
