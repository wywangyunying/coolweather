package com.example.wyy.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Suggestion {

    public Comf comf;

    public Sport sport;

    public CW cw;

    public class Comf {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class CW {
        @SerializedName("txt")
        public String info;
    }

}
