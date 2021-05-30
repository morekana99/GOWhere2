package com.example.ryan.gomap3;


import com.blankj.utilcode.util.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.youdao.sdk.app.YouDaoApplication;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author devonwong
 */
public class MyApplication extends LitePalApplication {
    private static  MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        if (YouDaoApplication.getApplicationContext() == null){
            YouDaoApplication.init(this, "682dd74669722c41");
        }
        Fresco.initialize(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("Xbanner"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
        Utils.init(this);
        myApplication = this;

    }
    public static MyApplication getInstance() {
        return myApplication;
    }
}
