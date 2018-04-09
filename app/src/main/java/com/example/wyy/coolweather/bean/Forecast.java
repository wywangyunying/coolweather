package com.example.wyy.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Forecast {

    @SerializedName("date")
    public String date;

    public Cond cond;

    public Tmp tmp;

    public class Cond {
        @SerializedName("txt_d")
        public String info;
    }

    public class Tmp {
        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }

}
