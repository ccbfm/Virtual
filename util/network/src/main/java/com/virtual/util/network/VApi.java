package com.virtual.util.network;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @Url 需要使用带host的全路径，否则不会更改baseUrl，只会和原baseUrl拼接在一起。
 * @Url url前置的"/"符号,会把baseUrl中host后面的"字符串/"去掉
 */
public interface VApi {

    @GET
    Call<ResponseBody> get(@Url String url);

    @GET
    Call<ResponseBody> get(@Url String url, @QueryMap Map<String, String> params);

    @GET
    Call<ResponseBody> get(@Url String url, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> headers);

    @POST
    Call<ResponseBody> post(@Url String url, @Body Map<String, String> params);

    @POST
    Call<ResponseBody> post(@Url String url, @Body Map<String, String> params, @HeaderMap Map<String, String> headers);

    @POST
    Call<ResponseBody> post(@Url String url, @Body RequestBody body, @HeaderMap Map<String, String> headers);
}
