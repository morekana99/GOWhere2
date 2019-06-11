package com.example.ryan.gomap3;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ryan.db.City;
import com.example.ryan.db.Country;
import com.example.ryan.db.Landmark;
import com.example.ryan.utill.HttpUtill;
import com.example.ryan.utill.Utility;


import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Choose_areaFragment extends Fragment {
    public static final int LEVEL_COUNTRY = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_LANDMARK = 2;
    private ProgressDialog progressDialog;
    private TextView titilText;
    private Button backBotton;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    private List<String>dataList = new ArrayList<>();
    private List<Country> countryList;
    private List<City> cityList;
    private List<Landmark> landmarkList;
    private Country selectCountry;
    private City selectCity;
    private Landmark selectLandmark;
    private  int currentLevel;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titilText = (TextView) view.findViewById(R.id.title_text);
        backBotton = (Button) view.findViewById(R.id.back_button);
        listview = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listview.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel==LEVEL_COUNTRY){
                    selectCountry=countryList.get(position);
                    queryCities();
                }
                else if (currentLevel==LEVEL_CITY){
                    String cityName=cityList.get(position).getCityName();
                    Toast.makeText(getContext(),cityName,Toast.LENGTH_SHORT).show();
                    if(getActivity() instanceof  LaunchActivity) {
                        Intent intent = new Intent(getActivity(), LandmarkActivity.class);
                        intent.putExtra("countrycode", cityList.get(position).getCountryId());
                        intent.putExtra("citycode", cityList.get(position).getCityCode());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(getActivity() instanceof LandmarkActivity){
                        LandmarkActivity activity = (LandmarkActivity) getActivity();
                        activity.mDrawLayout.closeDrawers();
                        activity.initLandmark(cityList.get(position).getCountryId(),cityList.get(position).getCityCode());

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
        queryCountries();
    }
    private void queryCountries(){
        titilText.setText("选择国家");
        backBotton.setVisibility(View.GONE);
        countryList = DataSupport.findAll(Country.class);
        if(countryList.size() > 0){
            dataList.clear();
            for(Country country : countryList){
                dataList.add(country.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else {
            String address = "http://uitlearn.top/api/country.json";
            queryFromServer(address,"country");
        }
    }
    private void queryCities(){
        titilText.setText(selectCountry.getCountyName());
        backBotton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("countryid = ?", String.valueOf(selectCountry.getId())).find(City.class);
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
            String address = "http://uitlearn.top/api/country/" + countryCode+".json";
            queryFromServer(address,"city");
        }
    }
//    private void queryLandmarks(){
//        titilText.setText(selectCity.getCityName());
//        backBotton.setVisibility(View.VISIBLE);
//        landmarkList = DataSupport.where("cityid = ?",String.valueOf(selectCity.getId())).find(Landmark.class);
//        if(landmarkList.size() > 0) {
//            dataList.clear();
//            for (Landmark landmark : landmarkList) {
//                dataList.add(landmark.getLandmarkName());
//            }
//            adapter.notifyDataSetChanged();
//            listview.setSelection(0);
//            currentLevel = LEVEL_LANDMARK;
//        }else{
//            int countryCode = selectCountry.getCountryCode();
//            int cityCode = selectCity.getCityCode();
//            String address = "http://uitlearn.top/api/country/" +  countryCode+ "/" + cityCode+".json";
//            queryFromServer(address,"landmark");
//        }
//    }
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        Log.e("Choose_areaFragment","7");
        HttpUtill.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("country".equals(type)){
                    result = Utility.handleCountryResponse(responseText);
                } else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectCountry.getId());
                }
                else if("landmark".equals(type)){
                    result = Utility.handleLandmarkResponse(responseText,selectCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
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
                        closeProgressDialog();
                        Toast.makeText(getContext(),"无网络连接，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /**
     * 显示Loading框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
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

}

