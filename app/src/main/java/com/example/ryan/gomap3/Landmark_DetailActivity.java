package com.example.ryan.gomap3;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

import com.amap.api.maps.model.Text;
import com.autonavi.ae.pos.LocGSVData;
import com.bumptech.glide.Glide;
import com.example.ryan.db.Landmark;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.Utility;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalApplication.getContext;


/**
 * The type Landmark detail activity.
 * @author devonwong
 */
public class Landmark_DetailActivity extends AppCompatActivity {
    public static final String LANDMARK_NAME = "Landmark_name";
    public  static final String LANDMARK_IMAGE_ID = "landmark_image_id";
    public  static final String LANDMARK_CITY_NAME = "landmark_city_name";
    public  static final String LANDMARK_COUNTRY_ID = "landmark_country_id";

    private String url;
;


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
        int landmarkImageid = intent.getIntExtra(LANDMARK_IMAGE_ID,0);
        final int countryCode = intent.getIntExtra(LANDMARK_COUNTRY_ID,0);
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
                if (countryCode!=1){
                    url= " https://www.google.com/maps/search/?api=1&query="+cityName+landmarkName+"&hl=zh-cn";

                }else{
                    url= "http://uri.amap.com/search?keyword=" +landmarkName
                            + "&view=map&src=gowhere&coordinate=gaode&callnative=0";
                }

                navi.putExtra("landmark_url",url);
                startActivity(navi);
            }
        });
    }
    private String generateLandmarkContent(String landmarkName){
        return DataSupport.select("detail").where("landmarkName = ?",landmarkName).findFirst(Landmark.class).getDetail();

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
