package com.example.wyy.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wyy.coolweather.bean.Forecast;
import com.example.wyy.coolweather.bean.Weather;
import com.example.wyy.coolweather.service.WeatherUpdateService;
import com.example.wyy.coolweather.util.HttpUtil;
import com.example.wyy.coolweather.util.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    @BindView(R.id.tv_title_city)
    TextView tvTitleCity;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;
    @BindView(R.id.tv_degree)
    TextView tvDegree;
    @BindView(R.id.tv_weather_info)
    TextView tvWeatherInfo;
    @BindView(R.id.ll_forecast)
    LinearLayout llForecast;
    @BindView(R.id.tv_aqi)
    TextView tvAqi;
    @BindView(R.id.tv_pm25)
    TextView tvPm25;
    @BindView(R.id.tv_comf)
    TextView tvComf;
    @BindView(R.id.tv_cw)
    TextView tvCw;
    @BindView(R.id.tv_sport)
    TextView tvSport;
    @BindView(R.id.sv_weather)
    ScrollView svWeather;
    @BindView(R.id.iv_bing_pic)
    ImageView ivBingPic;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.btn_area)
    Button btnArea;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        btnArea.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);
        String weatherId;
        String imgUrl = sp.getString("bing_pic", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            svWeather.setVisibility(View.INVISIBLE);
            requestWeatherId(weatherId);
        }
        if (imgUrl != null) {
            Glide.with(this).load(imgUrl).into(ivBingPic);
        } else {
            requestImage();
        }

        swipeRefresh.setOnRefreshListener(() -> {
            requestWeatherId(weatherId);
        });
    }

    private void showWeatherInfo(Weather weather) {
        svWeather.setVisibility(View.VISIBLE);

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updataTime.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherInfo = weather.now.cond.txt;

        tvTitleCity.setText(cityName);
        tvUpdateTime.setText(updateTime);
        tvDegree.setText(degree);
        tvWeatherInfo.setText(weatherInfo);
        llForecast.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_weather_forecast_son, llForecast, false);
            TextView tvData = (TextView) view.findViewById(R.id.tv_data);
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
            TextView tvMax = (TextView) view.findViewById(R.id.tv_max);
            TextView tvMin = (TextView) view.findViewById(R.id.tv_min);
            tvData.setText(forecast.date);
            tvInfo.setText(forecast.cond.info);
            tvMax.setText(forecast.tmp.max);
            tvMin.setText(forecast.tmp.min);
            llForecast.addView(view);
        }
        if (weather.aqi != null) {
            tvAqi.setText(weather.aqi.city.aqi);
            tvPm25.setText(weather.aqi.city.pm25);
        }
        String comf = "舒适度：" + weather.suggestion.comf.info;
        String cw = "洗车指数：" + weather.suggestion.cw.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        tvComf.setText(comf);
        tvCw.setText(cw);
        tvSport.setText(sport);

        startService(new Intent(this, WeatherUpdateService.class));
    }

    public void requestWeatherId(String weatherId) {
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=b48e7ed6e7404d43837755957cb84dbe";
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.getStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                });
            }
        });

        requestImage();
    }

    private void requestImage() {
        String url = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.getStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                runOnUiThread(() -> {
                    if (responseText != null) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("bing_pic", responseText);
                        editor.apply();
                        Glide.with(WeatherActivity.this).load(responseText).into(ivBingPic);
                    }
                });
            }
        });
    }

}
