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
            String url = call.request().url().toString();
            if (body != null) {
                onResponseString(url, body.string());
            } else {
                onFailure(FailType.RESPONSE_BODY, url, new NullPointerException("body is null."));
            }
        } catch (Throwable throwable) {
            onFailure(FailType.PROGRAM, "", throwable);
        }
    }

    public void onResponseString(String url, String result) throws Throwable {

    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
        String url = call.request().url().toString();
        onFailure(FailType.NETWORK, url, throwable);
    }

    public void onFailure(int type, String url, Throwable throwable) {

    }

    public interface FailType {
        int RESPONSE_BODY = 1;
        int NETWORK = RESPONSE_BODY + 1;
        int PROGRAM = NETWORK + 1;
    }
}
