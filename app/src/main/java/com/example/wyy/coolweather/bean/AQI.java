package com.example.wyy.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/8.
 */

public class AQI {

    public AQICity city;

    public class AQICity {
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
    }

}
