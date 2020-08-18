package com.simple.myweather.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simple.myweather.R;
import com.simple.myweather.db.City;
import com.simple.myweather.db.County;
import com.simple.myweather.db.Province;
import com.simple.myweather.util.HttpUtil;
import com.simple.myweather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    TextView titleText;
    Button backbtn;
    ListView areaList;

    private static final String TAG = "ChooseAreaFragment";
    private static int LEVEL;
    private static final int PROVINCE_LEVEL = 0;
    private static final int CITY_LEVEL = 1;
    private static final int COUNTY_LEVEL = 2;
    ArrayAdapter adapter;
    List<String> dataList = new ArrayList<>();
    List<Province> provinceList;
    List<City> cityList;
    List<County> countyList;
    Province selectedProvince;
    City selectedCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.choose_text);
        areaList = view.findViewById(R.id.choose_list);
        backbtn = view.findViewById(R.id.back);

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, dataList);
        areaList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (LEVEL == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (LEVEL == CITY_LEVEL) {
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LEVEL == COUNTY_LEVEL){
                    queryCity();
                }else if (LEVEL == CITY_LEVEL){
                    queryProvince();
                }else
                    queryProvince();
            }
        });

        queryProvince();
    }

    void queryProvince() {
        titleText.setText("中国");
        backbtn.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            LEVEL = PROVINCE_LEVEL;
        } else {
            String addr = "http://guolin.tech/api/china";
            queryProvinceFromServer(addr, "province");
        }
    }

    void queryCity() {
        backbtn.setVisibility(View.VISIBLE);
        titleText.setText(selectedProvince.getProvinceName());
        cityList = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getProvinceId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            Log.d(TAG, "-----result-----" + "\n" + dataList);
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            LEVEL = CITY_LEVEL;
        } else {
            String addr = "http://guolin.tech/api/china" + "/" + selectedProvince.getProvinceId();
            queryProvinceFromServer(addr, "city");
        }
    }

    void queryCounty() {
        titleText.setText(selectedCity.getCityName());
        countyList = LitePal.where("cityid = ?", String.valueOf(selectedCity.getCityId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            Log.d(TAG, "-----result-----" + "\n" + dataList);
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            LEVEL = COUNTY_LEVEL;
        } else {
            String addr = "http://guolin.tech/api/china" + "/" + selectedProvince.getProvinceId() + "/" + selectedCity.getCityId();
            queryProvinceFromServer(addr, "county");
        }
    }

    private void queryProvinceFromServer(String addr, final String type) {

        HttpUtil.sendOKHttpRequest(addr, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Problem", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                boolean result = false;
                try {
                    if (type.equals("province")) {
                        result = Utility.handleProvince(res);
                    } else if (type.equals("city")) {
                        result = Utility.handleCity(res, selectedProvince.getProvinceId());
                    } else if (type.equals("county")) {
                        result = Utility.handleCounty(res, selectedCity.getCityId());
                    }
                    if (result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (type.equals("province")) {
                                    queryProvince();
                                } else if (type.equals("city")) {
                                    queryCity();
                                } else{
                                    queryCounty();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
