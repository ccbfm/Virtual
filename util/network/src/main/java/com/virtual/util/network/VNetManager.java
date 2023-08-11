package com.virtual.util.network;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VNetManager {
    private static final class Singleton {
        private static final VNetManager INSTANCE = new VNetManager();
    }

    public static VNetManager instance() {
        return Singleton.INSTANCE;
    }

    private VNetManager() {
    }

    private final HashMap<Class<?>, Object> mApiMap = new HashMap<>();
    private Retrofit mRetrofit;

    @SuppressWarnings("unchecked")
    public <Api> Api getApi(Class<Api> classApi) {
        Object api = mApiMap.get(classApi);
        if (api != null) {
            return (Api) api;
        }
        return null;
    }

    public <Api> boolean putApi(Class<Api> classApi) {
        Api api = getApi(classApi);
        if (api == null) {
            if (mRetrofit != null) {
                api = mRetrofit.create(classApi);
                putApi(classApi, api);
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public <Api> void putApi(Class<Api> classApi, Api api) {
        mApiMap.putIfAbsent(classApi, api);
    }

    public void initDefault(OkHttpClient client) {
        if (mRetrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl("https://abc.com");
            if (client != null) {
                builder.client(client);
            }
            builder.addConverterFactory(GsonConverterFactory.create());
            mRetrofit = builder.build();
        }
    }


}
