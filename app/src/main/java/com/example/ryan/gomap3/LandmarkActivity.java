package com.example.ryan.gomap3;



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;


import com.example.ryan.adapter.LandmarkAdapter;
import com.example.ryan.db.Country;
import com.example.ryan.db.Landmark;
import com.example.ryan.bean.LandmarkList;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.Utility;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.header.PhoenixHeader;


import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * The type Landmark activity.
 *
 * @author devonwong
 */
public class LandmarkActivity extends AppCompatActivity {

    public static final String COUNTRY_NAME = "COUNTRY_NAME";

    /**
     * The constant COUNTRY_ID.
     */
    public final static String COUNTRY_ID = "COUNTRY_ID";
    /**
     * The constant CITY_CODE.
     */
    public final static String CITY_CODE = "CITY_CODE";

    /**
     * The M draw layout.
     */
    public DrawerLayout mDrawLayout;
    Toolbar toolbar;
    EditText editText;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    ActionBar actionBar;
    private  List<Landmark> landmarkList = new ArrayList<>();
    private LandmarkAdapter adapter;
    private List<LandmarkList>dataList = new ArrayList<>();
    private int country;
    private int city;
    private RefreshLayout swipeRefreshLayout;
    private PhoenixHeader header;
    private long clickTime = 0;
    private static boolean isFirstEnter = true;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        Intent intent = getIntent();
        country = intent.getIntExtra(COUNTRY_ID,0);
        city = intent.getIntExtra(CITY_CODE,0);

        initView();

        initFreshLayout();
    }
    public void initView(){

        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.toolbar_edit);
        fab = findViewById(R.id.trans_fab);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(100);
        layoutManager = new GridLayoutManager(this, 2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);
        mDrawLayout = findViewById(R.id.drawer_layout);
        swipeRefreshLayout =  findViewById(R.id.swipe);
        header = findViewById(R.id.header);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchLandmarkActivity.actionStart(LandmarkActivity.this);
                overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChooseLanguage= new Intent(LandmarkActivity.this,OcrTranslateActivity.class);
                startActivity(intentChooseLanguage);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LandmarkAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }

    public void initFreshLayout(){
        if (isFirstEnter) {
            isFirstEnter = false;
            swipeRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }else{
            queryLandmarks();
        }
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                DataSupport.deleteAll(Landmark.class,"cityId=?",city+"");
                String address = HttpUtill.oneIP + country+ "/" + city;
                queryFromServer(address);
                refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                landmarkList = DataSupport.where("cityid = ?",String.valueOf(city)).find(Landmark.class);
                if(landmarkList.size() > 0) {
                    for (Landmark landmark : landmarkList) {

                        dataList.add(new LandmarkList(country, landmark.getLandmarkName(), landmark.getImageId(), landmark.getCityName()));
                    }
                    adapter.notifyItemChanged(dataList.size(), dataList.size()+landmarkList.size());
                    refreshLayout.finishLoadMore(1000/*,false*/);//传入false表示刷新失败
                }else {
                    refreshLayout.finishLoadMore(false);//传入false表示刷新失败
                }
            }
        });
    }
    public void initData(){
        Country countryName = DataSupport.select("countyName").where("countryCode=?",country+"").findFirst(Country.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(COUNTRY_ID, country);
        editor.putString(COUNTRY_NAME, countryName.getCountyName());
        editor.putInt(CITY_CODE, city);
        editor.apply();
        Log.e("kana", "initData: "+ countryName.getCountyName()+"/"+country);
    }
    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LandmarkActivity.this).edit();
        switch (item.getItemId()){
            case R.id.location:
                Intent myLocation = new Intent(LandmarkActivity.this,WebActivity.class);
                String url= "https://uri.amap.com/search?keyword=&view=map&src=gowhere&coordinate=gaode&callnative=0";
                myLocation.putExtra("landmark_url",url);
                startActivity(myLocation);
                return true;
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.single_menu_01:
                editor.putInt("search_flag", 0);
                editor.apply();
                item.setChecked(true);
                Toast.makeText(this, "已切换到："+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.single_menu_02:
                editor.putInt("search_flag", 1);
                editor.apply();
                item.setChecked(true);
                Toast.makeText(this, "已切换到："+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Init landmark list.
     *
     * @param countryCode the country code
     * @param cityCode    the city code
     */
    public  void initLandmarkList(int countryCode,int cityCode){
        country = countryCode;
        city = cityCode;
        queryLandmarks();
    }


    /**
     * Get resource int.
     *
     * @param imageName the image name
     * @return the int
     */
    public int  getResource(String imageName){
        Context ctx=getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }
    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }
    private void queryLandmarks(){
        landmarkList = DataSupport.where("cityid = ?",String.valueOf(city)).find(Landmark.class);
        if(landmarkList.size() > 0) {
            int previousSize = dataList.size();
            dataList.clear();
            adapter.notifyItemRangeRemoved(0,previousSize);
            for (Landmark landmark : landmarkList) {
                dataList.add(new LandmarkList(country, landmark.getLandmarkName(), landmark.getImageId(), landmark.getCityName()));
            }
            adapter.notifyItemChanged(0,landmarkList.size());
        }else{
            String address = HttpUtill.oneIP +  country+ "/" + city;
            queryFromServer(address);
        }
        //swipeRefreshLayout.setRefreshing(false);
    }
    private void queryFromServer(String address){
        HttpUtill.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                result = Utility.handleLandmarkResponse(responseText,city);

                if (result){
                    LandmarkActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //swipeRefreshLayout.setRefreshing(false);
                            queryLandmarks();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                LandmarkActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        //swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(LandmarkActivity.this,"无网络连接，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Action start.
     *
     * @param context   the context
     * @param countryId the country id
     * @param cityCode  the city code
     */
    public static void actionStart(Context context,int countryId,int cityCode){
        Intent intent = new Intent(context,LandmarkActivity.class);
        intent.putExtra(COUNTRY_ID,countryId);
        intent.putExtra(CITY_CODE,cityCode);
        context.startActivity(intent);
    }
}

