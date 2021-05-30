package com.example.ryan.gomap3;



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import com.example.ryan.entity.LandmarkList;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.Utility;


import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private  List<Landmark> landmarkList = new ArrayList<>();
    private LandmarkAdapter adapter;
    private List<LandmarkList>dataList = new ArrayList<>();
    private int country;
    private int city;
    /**
     * The Recycler view.
     */
    RecyclerView recyclerView ;
    /**
     * The Layout manager.
     */
    GridLayoutManager layoutManager ;
    private SwipeRefreshLayout swipeRefreshLayout;

    private long clickTime = 0;
    private static boolean isExit = false;





    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_landmark);

        Intent intent = getIntent();
        country = intent.getIntExtra(COUNTRY_ID,0);
        city = intent.getIntExtra(CITY_CODE,0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        EditText editText = (EditText) findViewById(R.id.toolbar_edit);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchLandmarkActivity.actionStart(LandmarkActivity.this);
                overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out);
            }
        });
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        mDrawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.trans_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChooseLanguage= new Intent(LandmarkActivity.this,OcrTranslateActivity.class);
                startActivity(intentChooseLanguage);

            }
        });
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_name);

        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LandmarkAdapter(dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //设置RecyclerView滑到顶部时，SwipeRefreshLayout才响应下拉刷新,否则响应RecyclerView下滑
                swipeRefreshLayout.setEnabled(recyclerView.getChildCount() == 0
                        || recyclerView.getChildAt(0).getTop() >= 0);

            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataSupport.deleteAll(Landmark.class,"cityId=?",city+"");
                String address = HttpUtill.oneIP + country+ "/" + city;
                queryFromServer(address);
            }
        });
        queryLandmarks();


    }
    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        MenuItem location = menu.findItem(R.id.location);
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.location:
                Intent myLocation = new Intent(LandmarkActivity.this,WebActivity.class);
                String url= "http://uri.amap.com/search?keyword=&view=map&src=gowhere&coordinate=gaode&callnative=0";
                myLocation.putExtra("landmark_url",url);
                startActivity(myLocation);
                break;
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }


        return true;
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
            for(int i=0;i<6;i++) {
                for (Landmark landmark : landmarkList) {

                    dataList.add(new LandmarkList(country, landmark.getLandmarkName(), landmark.getImageId(), landmark.getCityName()));
                }
            }
            Country countryName = DataSupport.select("countyName").where("id=?",country+"").findFirst(Country.class);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putInt(COUNTRY_ID, country);
            editor.putString(COUNTRY_NAME, countryName.getCountyName());
            editor.putInt(CITY_CODE, city);
            editor.apply();
            adapter.notifyItemChanged(0,landmarkList.size());
        }else{
            String address = HttpUtill.oneIP +  country+ "/" + city;
            queryFromServer(address);
        }
        swipeRefreshLayout.setRefreshing(false);
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
                            swipeRefreshLayout.setRefreshing(false);
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
                        swipeRefreshLayout.setRefreshing(false);
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

