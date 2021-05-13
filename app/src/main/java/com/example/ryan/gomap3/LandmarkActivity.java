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
import android.view.Window;
import android.view.WindowManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_landmark);



        Intent intent = getIntent();
        country = intent.getIntExtra("countrycode",0);
        city = intent.getIntExtra("citycode",0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.trans_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChooseLanguage= new Intent(LandmarkActivity.this,Choose_LanguageActivity.class);
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

        SharedPreferences sp=LandmarkActivity.this.getSharedPreferences("gowhere", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("citycode", city);
        editor.putInt("countrycode", country);
        editor.apply();

        queryLandmarks();


    }
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.more:
                Intent mylocation = new Intent(LandmarkActivity.this,WebActivity.class);
                String url= "http://api.map.baidu.com/geocoder?address=兰州&output=html&src=webapp.martin.gowhere";
                mylocation.putExtra("landmark_url",url);
                startActivity(mylocation);
                break;
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
        }


        return true;
    }

    public  void initLandmarkList(int countryCode,int cityCode){
        country = countryCode;
        city = cityCode;
        queryLandmarks();
    }






    /*public void initLandmark(int countryCode,int cityCode) {
        dataList.clear();

        if (countryCode == 1 && cityCode ==2) {
            getSupportActionBar().setTitle("大连");
            for (int i = 0; i < 50; i++) {

                int index = i%15;
                dataList.add(dalianList[index]);
            }
        } else if (countryCode == 3&& cityCode == 1) {
            getSupportActionBar().setTitle("纽约");
            for (int i = 0; i < 50; i++) {

                int index = i%10;
                dataList.add(newyorkList[index]);
            }

        } else if (countryCode ==2 && cityCode == 1) {
            getSupportActionBar().setTitle("东京");
            for (int i = 0; i < 50; i++) {

                int index = i%10;
                dataList.add(tokyoList[index]);
            }

        }


    }*/



    /**
     * 显示Loading框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(LandmarkActivity.this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     *关闭进度框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
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
            System.exit(0);
        }
    }
    private void queryLandmarks(){
        landmarkList = DataSupport.where("cityid = ?",String.valueOf(city)).find(Landmark.class);
        if(landmarkList.size() > 0) {
            dataList.clear();
            for (Landmark landmark : landmarkList) {
                dataList.add(new Landmarkdata(landmark.getLandmarkName(),landmark.getImageId(),landmark.getCityName()));
            }

            adapter.notifyDataSetChanged();
        }else{
            String address = "http://192.168.3.59:8080/landmark/" +  country+ "/" + city;
            queryFromServer(address);
        }
    }
    private void queryFromServer(String address){
        showProgressDialog();
        HttpUtill.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                Log.d("WWWWWWWwwwwwwwwww", responseText);
                result = Utility.handleLandmarkResponse(responseText,city);

                if (result){
                    LandmarkActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
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
                        closeProgressDialog();
                        Toast.makeText(LandmarkActivity.this,"无网络连接，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}

