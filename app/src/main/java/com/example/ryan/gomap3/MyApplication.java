package com.example.ryan.gomap3;


import com.youdao.sdk.app.YouDaoApplication;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    private static  MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        if (YouDaoApplication.getApplicationContext() == null)
            YouDaoApplication.init(this, "682dd74669722c41");
            myApplication = this;
    }
    public static MyApplication getInstance() {
        return myApplication;
    }
}
