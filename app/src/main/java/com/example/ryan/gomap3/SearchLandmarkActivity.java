package com.example.ryan.gomap3;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.ryan.adapter.AutoCompleteAdapter;
import com.example.ryan.adapter.QuickAdapter;
import com.example.ryan.db.Landmark;
import com.example.ryan.db.SearchHistory;

import com.example.ryan.utill.StatusBarUtil;
import com.example.ryan.view.RecyclerViewWithContextMenu;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static com.example.ryan.gomap3.LandmarkActivity.CITY_CODE;


/**
 * @author devonwong
 */
public class SearchLandmarkActivity extends BaseActivity {
    private RecyclerViewWithContextMenu recyclerView;
    private QuickAdapter mAdapter;
    private List<String> dataList;
    private AutoCompleteTextView autoCompleteTextView;
    private int cityCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_landmark);

        StatusBarUtil.translucentStatusBar(this,true);

        initData();

        initView();

        initEditView();

        initEvent();


    }
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        TextView textView = (TextView) findViewById(R.id.search_cancel);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.search_recycler);
        //尺寸大小确定，可以设置该参数，避免重新计算大小
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        dataList = createDataList();
        mAdapter = new QuickAdapter(R.layout.item_search,dataList);
        recyclerView.setAdapter(mAdapter);
        //重写onCreateContextMenu方案一：
        registerForContextMenu(recyclerView);

//        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(SearchLandmarkActivity.this,R.layout.item_auto_compare,R.id.ss_title);
//        filter = new MySearchFilter(createSuggestList(),autoAdapter);
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, createSuggestList());
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.search_toolbar_edit);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
    }
    public void initEditView(){
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_toolbar_edit);
        autoCompleteTextView.setFocusable(true);
        autoCompleteTextView.setFocusableInTouchMode(true);
        autoCompleteTextView.requestFocus();
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId== EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = v.getText().toString();
                    if(actionSearch(searchStr)) {
                       changeAfterSearch(searchStr);
                    }


                }

                return false;
            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actionSearch(parent.getItemAtPosition(position).toString());
                Log.d("kana", parent.getItemAtPosition(position).toString());
                changeAfterSearch(parent.getItemAtPosition(position).toString());
            }
        });
    }

    private void initData() {
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(this);
        cityCode = prfs.getInt(CITY_CODE,0);

    }

    private void initEvent() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                actionSearch(Objects.requireNonNull(adapter.getItem(position)).toString());
                changeAfterSearch(Objects.requireNonNull(adapter.getItem(position)).toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterForContextMenu(recyclerView);
    }



    public static void actionStart(Context context){
        Intent intent = new Intent(context,SearchLandmarkActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(menuInfo instanceof RecyclerViewWithContextMenu.RecyclerViewContextMenuInfo){
            RecyclerViewWithContextMenu.RecyclerViewContextMenuInfo contextMenuInfo = (RecyclerViewWithContextMenu.RecyclerViewContextMenuInfo) menuInfo;
            if(contextMenuInfo != null && contextMenuInfo.getPostion() >= 0){
                menu.setHeaderTitle("点击："+mAdapter.getItem(contextMenuInfo.getPostion()));
                getMenuInflater().inflate(R.menu.context_menu,menu);
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getMenuInfo() instanceof RecyclerViewWithContextMenu.RecyclerViewContextMenuInfo){
            RecyclerViewWithContextMenu.RecyclerViewContextMenuInfo contextMenuInfo = (RecyclerViewWithContextMenu.RecyclerViewContextMenuInfo) item.getMenuInfo();
            if(contextMenuInfo != null && contextMenuInfo.getPostion() >= 0){
                int previousSize = mAdapter.getItemCount();
                switch (item.getItemId()){
                    case R.id.context_menu_del:
                        DataSupport.deleteAll(SearchHistory.class,"name = ?",mAdapter.getItem(contextMenuInfo.getPostion()));
                        Toast.makeText(this,"已删除"+mAdapter.getItem(contextMenuInfo.getPostion()) , Toast.LENGTH_SHORT).show();
                        dataList.remove(contextMenuInfo.getPostion());
                        mAdapter.notifyItemRemoved(contextMenuInfo.getPostion());
                        mAdapter.notifyItemRangeChanged(contextMenuInfo.getPostion(),previousSize-contextMenuInfo.getPostion());
                        return true;
                    case R.id.context_menu_save_delete_all:
                        DataSupport.deleteAll(SearchHistory.class);
                        dataList.clear();
                        mAdapter.notifyItemRangeChanged(0,previousSize);
                        Toast.makeText(this,"已清除所有记录" , Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        break;
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    //生成数据List
    @NonNull
    private List<String> createDataList(){
        List<String> list=new ArrayList<>();

        //全局搜索
        //List<SearchHistory> searchHistoryList = DataSupport.select("name").order("id desc").find(SearchHistory.class);
        List<SearchHistory> searchHistoryList = DataSupport
                .select("name")
                .where("cityCode=?",String.valueOf(cityCode))
                .order("id desc")
                .find(SearchHistory.class);

        for(SearchHistory i:searchHistoryList){
            list.add(i.getName());
        }
        return list;
    }
    public boolean actionSearch(String landmarkName){
        Landmark landmark = DataSupport.select("landmarkName","cityName","countryId","imageId").where("landmarkName=?",landmarkName).findFirst(Landmark.class);
        if(landmark==null){
            Toast.makeText(SearchLandmarkActivity.this,"未查询到该景点:"+ landmarkName, Toast.LENGTH_SHORT).show();
            return false;
        }else{
            Landmark_DetailActivity.actionStart(
                    SearchLandmarkActivity.this ,
                    landmark.getLandmarkName(),
                    landmark.getCityName(),
                    landmark.getCountryId(),
                    landmark.getImageId()
            );
            autoCompleteTextView.setText("");
            return true;
        }
    }
    public void changeAfterSearch(String searchStr){
        int previousSize = dataList.size();
        DataSupport.deleteAll(SearchHistory.class,"name = ?",searchStr);
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setCityCode(cityCode);
        searchHistory.setName(searchStr);
        searchHistory.save();
        dataList.clear();
        mAdapter.notifyItemRangeRemoved(0,previousSize);
        List<String>list  = createDataList();
        dataList.addAll(list);
        mAdapter.notifyItemRangeChanged(0,dataList.size());
    }

    public ArrayList<String> createSuggestList(){
        ArrayList<String> list=new ArrayList<>();
        List<Landmark> landmarkList = DataSupport.select("landmarkName")
                .where("cityId=?",String.valueOf(cityCode))
                .find(Landmark.class);
        for(Landmark i:landmarkList){
            list.add(i.getLandmarkName());
        }
        return list;
    }


}