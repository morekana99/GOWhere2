package com.example.ryan.gomap3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;




/**
 * The type Landmark detail activity.
 */
public class Landmark_DetailActivity extends AppCompatActivity {
    public static final String LANDMARK_NAME = "Landmark_name";
    public  static final String LANDMARK_IMAGE_ID = "landmark_image_id";
    public  static final String LANDMARK_CITY_NAME = "landmark_cityname";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_detail);
        final Intent intent = getIntent();
        final String landmarkName = intent.getStringExtra(LANDMARK_NAME);
        final String cityName = intent.getStringExtra(LANDMARK_CITY_NAME);
        int landmarkImageid = intent.getIntExtra(LANDMARK_IMAGE_ID,0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.navgation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                 findViewById(R.id.collapsing_toolbar);
        ImageView landmarkImageView = (ImageView) findViewById(R.id.landmark_image_view);
        TextView landmarkContext = (TextView) findViewById(R.id.landmark_context_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(landmarkName);
        Glide.with(this).load("http://uitlearn.top/images/p"+landmarkImageid+".jpg").into(landmarkImageView);
        final String landmarkContent = generateLandmarkContent(landmarkName);
        Toast.makeText(this,landmarkName,Toast.LENGTH_SHORT).show();
        landmarkContext.setText(landmarkContent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navi = new Intent(Landmark_DetailActivity.this,WebActivity.class);
                String url= "http://uri.amap.com/search?keyword=" +landmarkName
                        + "&view=map&src=gowhere&coordinate=gaode&callnative=0";
                navi.putExtra("landmark_url",url);
                startActivity(navi);
            }
        });
    }
    private String generateLandmarkContent(String landmarkName){
        if(landmarkName.equals("大连理工大学")) {
            return this.getString(R.string.大连理工大学);
        }
        else if(landmarkName.equals("星海广场")){
            return this.getString(R.string.星海广场);
        }
        else if(landmarkName.equals("自由女神像")){
            return this.getString(R.string.自由女神像);
        }
        else if(landmarkName.equals("复仇者大厦")){
            return this.getString(R.string.复仇者大厦);
        }
        else if(landmarkName.equals("东京塔")){
            return this.getString(R.string.东京塔);
        }
        else if(landmarkName.equals("童牛岭")) {
            return this.getString(R.string.童牛岭);
        }
        else if(landmarkName.equals("布鲁克林大桥")) {
            return this.getString(R.string.童牛岭);
        }
        else {
            return "     未能获取到信息...";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
