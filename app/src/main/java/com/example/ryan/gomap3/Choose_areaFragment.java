package com.example.ryan.gomap3;

import android.annotation.TargetApi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ryan.adapter.AutoCompleteAdapter;
import com.example.ryan.db.City;
import com.example.ryan.db.Country;
import com.example.ryan.db.Landmark;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.StatusBarUtil;
import com.example.ryan.utill.Utility;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;


import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.ryan.gomap3.LandmarkActivity.CITY_CODE;
import static com.example.ryan.gomap3.LandmarkActivity.COUNTRY_ID;
import static com.example.ryan.gomap3.LandmarkActivity.COUNTRY_NAME;

/**
 * @author devonwong
 */
public class Choose_areaFragment extends Fragment {
    public static final int LEVEL_COUNTRY = 0;
    public static final int LEVEL_CITY = 1;


    private Button backBotton;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    private ArrayList<String>dataList = new ArrayList<>();
    private List<Country> countryList;
    private List<City> cityList;
    private List<Landmark> landmarkList;
    private Country selectCountry = new Country();
    private int spCountryId = 0;
    private int spCityId = 0;
    private String spCountryName = "";
    private  int currentLevel;
    private AutoCompleteTextView autoCompleteTextView;
    private RefreshLayout refreshLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        backBotton = (Button) view.findViewById(R.id.back_button);
        listview = (ListView) view.findViewById(R.id.list_view);
        autoCompleteTextView = (AutoCompleteTextView)view.findViewById(R.id.title_text);
        refreshLayout = view.findViewById(R.id.area_refresh);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listview.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        StatusBarUtil.translucentStatusBar(getActivity(),true);
        initAutoCompareView();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel==LEVEL_COUNTRY){
                    selectCountry=countryList.get(position);
                    queryCities();
                }
                else if (currentLevel==LEVEL_CITY){
                    if(getActivity() instanceof  LaunchActivity) {
                        LandmarkActivity.actionStart(getActivity(),cityList.get(position).getCountryId(),cityList.get(position).getCityCode());
                        saveData(cityList.get(position).getCountryId(),spCityId = cityList.get(position).getCityCode(),selectCountry.getCountyName());
                        getActivity().finish();
                    }
                    else if(getActivity() instanceof LandmarkActivity){
                        LandmarkActivity activity = (LandmarkActivity)getActivity();
                        spCityId = cityList.get(position).getCityCode();
                        spCountryId = cityList.get(position).getCountryId();
                        spCountryName = selectCountry.getCountyName();
                        activity.initLandmarkList(spCountryId,spCityId);
                        activity.mDrawLayout.closeDrawers();
                        saveData(spCountryId,spCityId,spCountryName);
                        Log.d("kana", "onCreate: "+spCityId+"/"+spCountryId+"/"+spCountryName);
                    }
                }
            }
        });
        backBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v) {
                if (currentLevel==LEVEL_CITY){
                    queryCountries();
                }
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(currentLevel==LEVEL_COUNTRY){
                    DataSupport.deleteAll(Country.class);
                    queryCountries();
                    refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                }else if(currentLevel==LEVEL_CITY){
                    DataSupport.deleteAll(City.class);
                    queryCities();
                    refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                }else{
                    refreshLayout.finishRefresh(false);//传入false表示刷新失败
                }
            }
        });
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences( getActivity());
        int cache = prfs.getInt(COUNTRY_ID,0);
        String cacheName = prfs.getString(COUNTRY_NAME,"");
        Log.e("kana", "SharedPreferences: "+cache+"/"+cacheName);
        if (cache != 0) {
            selectCountry.setCountryCode(cache);
            selectCountry.setCountyName(cacheName);
            queryCities();
        }else {
            queryCountries();
        }
    }

    private void queryCountries(){
        autoCompleteTextView.setHint("搜索国家");
        backBotton.setVisibility(View.GONE);
        countryList = DataSupport.select("countryCode","countyName").order("countryCode").find(Country.class);
        if(countryList.size() > 0){
            dataList.clear();
            for(Country country : countryList){
                dataList.add(country.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else {
            String address = HttpUtill.oneIP;
            queryFromServer(address,"country");
        }

    }
    private void queryCities(){
        autoCompleteTextView.setHint("在"+selectCountry.getCountyName()+"内搜索");
        backBotton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("countryId = ?", selectCountry.getCountryCode()+"").order("cityCode").find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int countryCode= selectCountry.getCountryCode();
            String address = HttpUtill.oneIP + countryCode;
            queryFromServer(address,"city");
        }
    }

    private void queryFromServer(String address, final String type){
        HttpUtill.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("country".equals(type)){
                    result = Utility.handleCountryResponse(responseText);
                } else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectCountry.getCountryCode());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("country".equals(type)){
                                queryCountries();
                            }
                            else if("city".equals(type)) {
                                queryCities();
                            }

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"无网络连接，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void initAutoCompareView() {
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(getActivity(), dataList);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_CITY){
                    actionSearchCity(parent.getItemAtPosition(position).toString());
                }else if(currentLevel==LEVEL_COUNTRY){
                    selectCountry=DataSupport.where("countyName=?",parent.getItemAtPosition(position).toString()).findFirst(Country.class);
                    queryCities();
                    autoCompleteTextView.setText("");
                }

            }
        });

    }

    public void actionSearchCity(String cityName) {
        City city = DataSupport.select("countryId","cityCode").where("cityName=?",cityName).findFirst(City.class);
        if(city==null){
            Toast.makeText(getActivity(),"未查询到该城市:"+ cityName, Toast.LENGTH_SHORT).show();
        }else{
            if(getActivity() instanceof  LaunchActivity) {
                LandmarkActivity.actionStart(
                        getActivity() ,
                        city.getCountryId(),
                        city.getCityCode()
                );

            }
            else if(getActivity() instanceof LandmarkActivity){
                LandmarkActivity activity = (LandmarkActivity)getActivity();
                activity.mDrawLayout.closeDrawers();
                activity.initLandmarkList(city.getCountryId(),city.getCityCode());
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm.isActive()&&getActivity().getCurrentFocus()!=null){
                if (getActivity().getCurrentFocus().getWindowToken()!=null) {
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            autoCompleteTextView.setText("");
        }


    }
    public void saveData(int countryId,int cityCode,String countryName){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putInt(COUNTRY_ID, countryId);
        editor.putInt(CITY_CODE,cityCode);
        editor.putString(COUNTRY_NAME, countryName);
        editor.apply();
    }
}

