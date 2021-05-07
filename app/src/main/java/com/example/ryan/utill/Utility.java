package com.example.ryan.utill;

import android.text.TextUtils;
import android.util.Log;

import com.example.ryan.db.City;
import com.example.ryan.db.Country;
import com.example.ryan.db.Landmark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
                    Log.e("wwwwwwwwwwwwwwwww",countryObject.getInt("countryId")+countryObject.getString("countryName"));
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
            try {
                JSONArray allLandmarks = new JSONArray(response);
                for (int i=0;i<allLandmarks.length();i++){
                    JSONObject landmarksJSONObject = allLandmarks.getJSONObject(i);
                    Landmark landmark = new Landmark();
                    landmark.setLandmarkName(landmarksJSONObject.getString("name"));
                    landmark.setCityId(cityId);
                    landmark.setImageId(landmarksJSONObject.getInt("imageid"));
                    landmark.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
