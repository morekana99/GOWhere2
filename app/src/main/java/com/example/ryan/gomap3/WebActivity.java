package com.example.ryan.gomap3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;


public class WebActivity extends AppCompatActivity {

    WebView webView = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        webView = (WebView)findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setBlockNetworkLoads(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAppCacheEnabled(true);
        webSettings.setGeolocationEnabled(true);
        //启用数据库
        webSettings.setDatabaseEnabled(true);
//
//        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);

        //启用地理定位
        webSettings.setGeolocationEnabled(true);

        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);


        Intent intent = getIntent();
        String uristr =intent.getStringExtra("landmark_url");
        Log.d("dd", uristr);
        webView.loadUrl(uristr);


//        if (data!=null){
//            String uristr = "http://api.map.baidu.com/geocoder?" + data
//                    + "&output=html&src=yhc";
//            webView.loadUrl(uristr);
//
//        }
//        else {
//            String cityName =intent.getStringExtra("extra_data");
//            String uristr = "http://api.map.baidu.com/geocoder?" + cityName
//                    + "&output=html&src=yhc";
//
////            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WebActivity.this).edit();
////            editor.putString("cityname",uristr);
////            editor.apply();
//            webView.loadUrl(uristr);
//        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });







    }
    //点击返回键，返回上一个页面，而不是退出程序
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            // 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
