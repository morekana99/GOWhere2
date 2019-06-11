package com.example.ryan.gomap3;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;





import com.example.ryan.db.Landmark;
import com.example.ryan.db.Landmarkdata;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.Utility;


import java.io.IOException;
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
    private Landmarkdata[] dalianList={new Landmarkdata("大连理工大学",R.drawable.p1,"大连")
            ,new Landmarkdata("星海广场",R.drawable.p2,"大连"),new Landmarkdata("大连森林动物园",R.drawable.p3,"大连"),
            new Landmarkdata("老虎滩风景区",R.drawable.p4,"大连"),new Landmarkdata("金石滩风景区",R.drawable.p5,"大连"),
            new Landmarkdata("童牛岭",R.drawable.p35,"大连"), new Landmarkdata("东港音乐喷泉",R.drawable.p6,"大连"),
            new Landmarkdata("旅顺日俄监狱",R.drawable.p7,"大连"), new Landmarkdata("星海大桥",R.drawable.p8,"大连"),
            new Landmarkdata("渔人码头",R.drawable.p25,"大连"), new Landmarkdata("发现王国",R.drawable.p26,"大连"),
            new Landmarkdata("圣亚海洋公园",R.drawable.p27,"大连"), new Landmarkdata("旅顺军港",R.drawable.p28,"大连"),
            new Landmarkdata("旅顺口",R.drawable.p29,"大连"), new Landmarkdata("滨海路",R.drawable.p30,"大连")};
    private  Landmarkdata[] newyorkList={new Landmarkdata("自由女神像",R.drawable.p9,"纽约"),
            new Landmarkdata("时代广场",R.drawable.p10,"纽约"),new Landmarkdata("帝国大厦",R.drawable.p11,"纽约"),
            new Landmarkdata("纽约博物馆",R.drawable.p12,"纽约"),new Landmarkdata("布鲁克林大桥",R.drawable.p13,"纽约"),
            new Landmarkdata("百老汇大道",R.drawable.p14,"纽约"),new Landmarkdata("第五大道",R.drawable.p15,"纽约"),
            new Landmarkdata("洛克菲勒中心",R.drawable.p16,"纽约"),new Landmarkdata("中央公园",R.drawable.p31,"纽约"),
            new Landmarkdata("复仇者大厦",R.drawable.p32,"纽约")};
    private Landmarkdata[] tokyoList={new Landmarkdata("银座",R.drawable.p33,"东京"),new Landmarkdata("富士山",R.drawable.p18,"东京"),
            new Landmarkdata("天空树",R.drawable.p19,"东京"),new Landmarkdata("东京塔",R.drawable.p20,"东京"),
            new Landmarkdata("上野公园",R.drawable.p21,"东京"),new Landmarkdata("涩谷",R.drawable.p22,"东京"),
            new Landmarkdata("东京竞马场",R.drawable.p23,"东京"),new Landmarkdata("印刷博物馆",R.drawable.p24,"东京"),
            new Landmarkdata("秋叶原",R.drawable.p17,"东京"),new Landmarkdata("迪士尼乐园",R.drawable.p34,"东京")};

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




        initLandmark(country,city);


    }
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.more:
                Intent mylocation = new Intent(LandmarkActivity.this,WebActivity.class);
                String url= "http://api.map.baidu.com/geocoder?address=大连&output=html&src=webapp.martin.gowhere";
                mylocation.putExtra("landmark_url",url);
                startActivity(mylocation);
                break;
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
        }


        return true;
    }





    public void initLandmark(int countryCode,int cityCode) {
        dataList.clear();
        if (countryCode == 1 && cityCode == 7) {
            getSupportActionBar().setTitle("大连");
            for (int i = 0; i < 50; i++) {

                int index = i%15;
                dataList.add(dalianList[index]);
            }
        } else if (countryCode == 2 && cityCode == 1) {
            getSupportActionBar().setTitle("纽约");
            for (int i = 0; i < 50; i++) {

                int index = i%10;
                dataList.add(newyorkList[index]);
            }

        } else if (countryCode == 3 && cityCode == 1) {
            getSupportActionBar().setTitle("东京");
            for (int i = 0; i < 50; i++) {

                int index = i%10;
                dataList.add(tokyoList[index]);
            }

        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LandmarkAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }



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

}

