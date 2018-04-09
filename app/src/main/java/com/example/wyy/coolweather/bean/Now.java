package com.example.wyy.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Now {

    @SerializedName("tmp")
    public String tmp;

    public Cond cond;

    public class Cond{
        @SerializedName("txt")
        public String txt;
    }

}
