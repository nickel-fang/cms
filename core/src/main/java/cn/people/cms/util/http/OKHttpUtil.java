package cn.people.cms.util.http;

import okhttp3.*;

import java.io.IOException;

public class OKHttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    public static String httpPut(String url, String json) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }


    public static String httpPost(String url, String json) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }


    public static String httpGet(String url) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }
}
