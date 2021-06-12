package com.example.ryan.gomap3;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.example.ryan.bean.UrlBean;
import com.example.ryan.db.Landmark;
import com.example.ryan.db.LandmarkRate;
import com.example.ryan.bean.LandmarkViewInfo;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.ScreenUtil;
import com.example.ryan.utill.StatusBarUtil;
import com.example.ryan.utill.Utility;
import com.stx.xhb.xbanner.XBanner;


import org.litepal.crud.DataSupport;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.ryan.gomap3.ImageActivity.URL_EXTRA;


/**
 * The type Landmark detail activity.
 *
 * @author devonwong
 */
public class Landmark_DetailActivity extends AppCompatActivity {
    public static final String LANDMARK_NAME = "Landmark_name";
    public  static final String LANDMARK_IMAGE_ID = "landmark_image_id";
    public  static final String LANDMARK_CITY_NAME = "landmark_city_name";
    public  static final String LANDMARK_COUNTRY_ID = "landmark_country_id";
    private int landmarkImageId;
    private XBanner mBanner;
    private String url;
    List<LandmarkViewInfo> data;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_landmark_detail);
        final Intent intent = getIntent();
        final String landmarkName = intent.getStringExtra(LANDMARK_NAME);
        final String cityName = intent.getStringExtra(LANDMARK_CITY_NAME);
        landmarkImageId = intent.getIntExtra(LANDMARK_IMAGE_ID,0);
        final int countryCode = intent.getIntExtra(LANDMARK_COUNTRY_ID,0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.navgation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                 findViewById(R.id.collapsing_toolbar);
        ImageView landmarkImageView = (ImageView) findViewById(R.id.landmark_image_view);
        TextView landmarkContext = (TextView) findViewById(R.id.landmark_context_text);
        RatingBar ratingBar = findViewById(R.id.ratingbar);
        TextView landmarkTime = findViewById(R.id.landmark_time_text);
        final TextView rateTextView = findViewById(R.id.landmark_rate);
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(landmarkName);
        Typeface font = Typer.set(this).getFont(Font.ROBOTO_BOLD);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));
        StatusBarUtil.setStatusBarColorForCollapsingToolbar(this,appBarLayout,collapsingToolbarLayout,toolbar,R.color.colorPrimary);
        Glide.with(this).load("https://devyn.wang/images/p"+ landmarkImageId +".jpg").apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)).into(landmarkImageView);
        final String landmarkContent = generateLandmarkContent(landmarkName);
        landmarkContext.setText(landmarkContent);
        ratingBar.setRating((generateLandmarkRate(landmarkName)));
        rateTextView.setText(String.valueOf((generateLandmarkRate(landmarkName))));
        String string = generateLandmarkTime(landmarkName);
        if(string.isEmpty()){
            landmarkTime.setText("未获取到开放时间");
        }else {
            landmarkTime.setText("开放时间 : "+ generateLandmarkTime(landmarkName));
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ContentValues values = new ContentValues();
                values.put("rate", rating);
                rateTextView.setText(String.valueOf(rating));
                DataSupport.updateAll(LandmarkRate.class,values,"landmarkName=?",landmarkName);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navi = new Intent(Landmark_DetailActivity.this,WebActivity.class);
                if (countryCode!=1){
                    url= " https://www.google.com/maps/search/?api=1&query="+cityName+landmarkName+"&hl=zh-cn";

                }else{
                    url= "https://uri.amap.com/search?keyword=" +landmarkName
                            + "&view=map&src=gowhere&coordinate=gaode&callnative=0";
                }
                Log.d("url", url);

                navi.putExtra("landmark_url",url);
                startActivity(navi);
            }
        });


        mBanner = findViewById(R.id.banner);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenWidth(this) / 2);
        mBanner.setLayoutParams(layoutParams);
        initBanner(mBanner);
        initData();
    }
    private String generateLandmarkContent(String landmarkName){
        return DataSupport.select("detail").where("landmarkName = ?",landmarkName).findFirst(Landmark.class).getDetail();
    }
    private float generateLandmarkRate(String landmarkName){
        return DataSupport.select("rate").where("landmarkName = ?",landmarkName).findFirst(LandmarkRate.class).getRate();
    }
    private String generateLandmarkTime(String landmarkName){
        return DataSupport.select("time").where("landmarkName = ?",landmarkName).findFirst(Landmark.class).getTime();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void actionStart(Context context,String landmarkName,String cityName,int countryId,int imageId){
        Intent intent = new Intent(context,Landmark_DetailActivity.class);
        intent.putExtra(LANDMARK_NAME,landmarkName);
        intent.putExtra(LANDMARK_CITY_NAME,cityName);
        intent.putExtra(LANDMARK_COUNTRY_ID,countryId);
        intent.putExtra(LANDMARK_IMAGE_ID,imageId);
        context.startActivity(intent);
    }

    private void initBanner(XBanner banner) {


        //设置广告图片点击事件
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                String url = ((LandmarkViewInfo)model).getXBannerUrl().toString();
                Bundle bundle = new Bundle();
                bundle.putParcelable(URL_EXTRA ,new UrlBean(url));
                ImageActivity.actionStart(Landmark_DetailActivity.this,bundle);
            }
        });
        //加载广告图片
        banner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                RoundedCorners roundedCorners = new RoundedCorners(20);
                RequestOptions coverRequestOptions = new RequestOptions()
                        .transforms(new CenterCrop(),roundedCorners)
                        .diskCacheStrategy(DiskCacheStrategy.NONE);
                Glide.with(Landmark_DetailActivity.this)
                        .load(((LandmarkViewInfo)model).getXBannerUrl())
                        .apply(coverRequestOptions
                                .placeholder(R.drawable.default_image)
                                .error(R.drawable.default_image))
                        .into((ImageView)view);
            }
        });
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                Log.i("onPageSelected===>", i + "");
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //加载网络图片资源
        String url = HttpUtill.oneIP+"imgamount/"+landmarkImageId;
        Log.d("kana", "url: "+url);
        HttpUtill.sendOkHttpRequest(url,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Landmark_DetailActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        Toast.makeText(Landmark_DetailActivity.this, "加载数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                int amount = Utility.getImageAmount(responseText);
                Log.d("kana", "onResponse:amount: "+amount);
                data = new ArrayList<>();
                if(amount!=0){
                    for (int i = 1; i <= amount; i++) {
                        data.add(new LandmarkViewInfo(landmarkImageId+"00"+i,true));
                    }
                }else {
                    for (int i=0;i<6;i++){
                        data.add(new LandmarkViewInfo( landmarkImageId+"" ,false));
                    }
                }

                Landmark_DetailActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        //刷新数据之后，需要重新设置是否支持自动轮播
                        mBanner.setAutoPlayAble(data.size() > 1);
                        mBanner.setIsClipChildrenMode(true);
                        mBanner.setBannerData(data);
                    }
                });
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
        mBanner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }






}
