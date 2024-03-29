package com.example.ryan.utill;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.ryan.db.City;
import com.example.ryan.db.Country;
import com.example.ryan.db.Landmark;
import com.example.ryan.db.LandmarkRate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * @author devonwong
 */
public class  Utility {
    public  static  boolean handleCountryResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountries = new JSONArray(response);
                for (int i=0;i<allCountries.length();i++){
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryCode(countryObject.getInt("countryId"));
                    country.setCountyName(countryObject.getString("countryName"));
                    country.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public  static  boolean handleCityResponse(String response,int countryId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityJSONObject= allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityJSONObject.getInt("cityId"));
                    city.setCityName(cityJSONObject.getString("cityName"));
                    city.setCountryId(countryId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public  static  boolean handleLandmarkResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            List<Landmark> gsonLandmarkList = new Gson().fromJson(response, new TypeToken<List<Landmark>>(){}.getType());
            for (Landmark mLandmark :gsonLandmarkList) {
                Landmark landmark = new Landmark();
                LandmarkRate landmarkRate  = new LandmarkRate();
                landmark.setLandmarkName(mLandmark.getLandmarkName());
                landmark.setCityId(cityId);
                landmark.setCountryId(mLandmark.getCountryId());
                landmark.setImageId(mLandmark.getImageId());
                landmark.setCityName(mLandmark.getCityName());
                landmarkRate.setLandmarkName(mLandmark.getLandmarkName());
                landmarkRate.setRate(mLandmark.getRate()!=0.0f?mLandmark.getRate():0.0f);
                landmark.setTime(mLandmark.getTime()!=null?mLandmark.getTime():"");
                landmark.setDetail(mLandmark.getDetail()!=null?mLandmark.getDetail():"很抱歉，该景点暂无简介信息，工程师正在抓紧完善");
                landmark.save();
                landmarkRate.save();
            }
            return true;
        }
        return false;
    }
    public  static  int getImageAmount(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject object = jsonArray.getJSONObject(0);
                return object.getInt("imageAmount");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return 0;
    }
}
