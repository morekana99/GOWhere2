package com.example.ryan.utill;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtill {
    public static String oneIP = "192.168.110.57";
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}

