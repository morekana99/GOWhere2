package com.example.ryan.gomap3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ryan.bean.UrlBean;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author devonwong
 */
public class ImageActivity extends AppCompatActivity {
    public static final String URL_EXTRA = "URL_EXTRA";

    RequestOptions coverRequestOptions;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.big_image);
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        coverRequestOptions = new RequestOptions()
                .override(5000)
                .transforms(new FitCenter())
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        UrlBean urlBean = Objects.requireNonNull(getIntent().getExtras()).getParcelable(URL_EXTRA);
        Log.e("kana", "Bundle: "+urlBean.getFlag()+"/"+urlBean.getStringUrl()+"/"+ Arrays.toString(urlBean.getByteUrl()));
        if(urlBean.getFlag()==0){
            showImage(urlBean.getStringUrl());
        }else if(urlBean.getFlag()==1){
            showImage(urlBean.getByteUrl());
        }
    }
    public static void actionStart(Context context ,Bundle bundle ){
        Intent intent = new Intent(context,ImageActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void showImage(String url){
        Glide.with(this)
                .load(url)
                .apply(coverRequestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(drawable);
                        imageView.setAdjustViewBounds(true);
                    }
                });
    }
    public void showImage(byte[] url){
        Log.e("kana", "showImage: "+url);
        Glide.with(this)
                .load(url)
                .apply(coverRequestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(drawable);
                        imageView.setAdjustViewBounds(true);
                    }
                });
    }

}