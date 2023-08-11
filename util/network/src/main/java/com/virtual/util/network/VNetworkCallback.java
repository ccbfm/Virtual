package com.virtual.util.network;

import androidx.annotation.NonNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VNetworkCallback implements Callback<ResponseBody> {

    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        
    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {

    }
}
