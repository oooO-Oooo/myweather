package com.simple.myweather.util;

import android.text.TextUtils;

import com.simple.myweather.db.City;
import com.simple.myweather.db.County;
import com.simple.myweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Utility {

    //处理省数据
    public static boolean handleProvince(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)) {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                Province province = new Province();
                JSONObject object = array.getJSONObject(i);
                province.setProvinceId(object.getInt("id"));
                province.setProvinceName(object.getString("name"));
                province.save();
            }
            return true;
        }
        return false;
    }

    //处理市数据
    public static boolean handleCity(String res, int provinceId) throws JSONException {
        if (!TextUtils.isEmpty(res)) {
            JSONArray array = new JSONArray(res);
            for (int i = 0; i < array.length(); i++) {
                City city = new City();
                JSONObject object = array.getJSONObject(i);
                city.setCityId(object.getInt("id"));
                city.setCityName(object.getString("name"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return true;
        }
        return false;
    }

    //处理县数据
    public static boolean handleCounty(String res, int cityId) throws JSONException {
        if (!TextUtils.isEmpty(res)) {

            JSONArray array = new JSONArray(res);
            for (int i = 0; i < array.length(); i++) {
                County county = new County();
                JSONObject object = array.getJSONObject(i);
                county.setCountyId(object.getInt("id"));
                county.setCountyName(object.getString("name"));
                county.setCityId(cityId);
                county.setWeatherId(object.getString("weather_id"));
                county.save();
            }
            return true;
        }
        return false;
    }
}
