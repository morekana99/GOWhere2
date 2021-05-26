package com.example.ryan.gomap3;



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;





import com.example.ryan.db.Landmark;
import com.example.ryan.db.Landmarkdata;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.Utility;


import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ServiceConfigurationError;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author devonwong
 */
public class LandmarkActivity extends AppCompatActivity {
    public DrawerLayout mDrawLayout;
    private  List<Landmark> landmarkList = new ArrayList<>();
    private LandmarkAdapter adapter;
    private List<Landmarkdata>dataList = new ArrayList<>();
    private int country;
    private int city;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView ;
    GridLayoutManager layoutManager ;

    private static boolean isExit = false;

    private SwipeRefreshLayout swipeRefreshLayout;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_landmark);

        Intent intent = getIntent();
        country = intent.getIntExtra("countrycode",0);
        city = intent.getIntExtra("citycode",0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        EditText editText = (EditText) findViewById(R.id.toolbar_edit);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandmarkActivity.this,SearchLandmarkActivity.class);
                startActivity(intent);
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
/*
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
*/

    public  void initLandmarkList(int countryCode,int cityCode){
        country = countryCode;
        city = cityCode;
        queryLandmarks();
    }


    public int  getResource(String imageName){
        Context ctx=getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
        }
    }
    private void queryLandmarks(){
        landmarkList = DataSupport.where("cityid = ?",String.valueOf(city)).find(Landmark.class);
        if(landmarkList.size() > 0) {
            dataList.clear();
            for(int i=0;i<6;i++) {
                for (Landmark landmark : landmarkList) {

                    dataList.add(new Landmarkdata(country, landmark.getLandmarkName(), landmark.getImageId(), landmark.getCityName()));
                }
            }
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LandmarkActivity.this).edit();
            editor.putInt("citycode", city);
            editor.putInt("countrycode", country);
            editor.apply();
            adapter.notifyDataSetChanged();
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

}

