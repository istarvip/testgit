package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.Weather;
import com.walnutin.entity.WeatherInfo;
import com.walnutin.hard.R;
import com.walnutin.util.CityUtils;
import com.walnutin.util.TimeUtil;
import com.walnutin.util.WeatherSpUtil;
import com.walnutin.view.LoopView;
import com.walnutin.Jinterface.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/6.
 */
public class RunningActivity extends Activity implements APICallback {
    AQuery aQuery;
    private static final String[] PLANETS = new String[]{"3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00"};
    String APP_KEY = "131b083c81290";
    Weather weather;
    WeatherInfo weatherInfo;
    WeatherSpUtil weatherSpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_running);
        aQuery = new AQuery(this);
        aQuery.id(R.id.running_txt_commomMode).clicked(this, "switchToCommomMode");
        aQuery.id(R.id.running_txt_speedMode).clicked(this, "switchToSpeedMode");
        aQuery.id(R.id.running_txt_runMode).clicked(this, "switchToRunMode");
        aQuery.id(R.id.register_back).clicked(this, "register_back");
        aQuery.id(R.id.running_img_speed_run).clicked(this, "startSpeedMode");
        aQuery.id(R.id.running_img_common).clicked(this, "startCommonMode");
        MobAPI.initSDK(getApplicationContext(), APP_KEY);

        initEvent();

        Intent intent2 = new Intent();
        intent2.setClass(this,PhoneSettingActivity.class);
        startActivity(intent2);

    }


    public void initEvent() {
        LoopView loopView = (LoopView) findViewById(R.id.wheel_view_wv);
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Log.d("debug", "Item " + index);
                Toast.makeText(getApplicationContext(), PLANETS[index], Toast.LENGTH_SHORT).show();
            }
        });
        //设置原始数据
        loopView.setItems(Arrays.asList(PLANETS));
        //设置初始位置
        loopView.setInitPosition(5);
        //设置字体大小
        loopView.setTextSize(15);
        loopView.setViewPadding(80, 0, 0, 0);
        switchToCommomMode(null);
        weatherSpUtil = WeatherSpUtil.instance(getApplicationContext());
        weatherInfo = weatherSpUtil.getDailyInfolistFromSp();
        if (weatherInfo == null || (System.currentTimeMillis() - weatherInfo.getTimeStamp()) / (1000 * 3600) >= 1) {  //一个小时更新一次天气
            weather = (Weather) MobAPI.getAPI(Weather.NAME);
            weather.getSupportedCities(this);
            new GetIP().execute();
        } else {
            updateWeather(weatherInfo);

        }

    }

    public void register_back(View v) {

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cityUtils != null) {
            cityUtils.release();
        }
    }

    public void startSpeedMode(View v) {
        finish();
    }

    public void startCommonMode(View v) {
        //  finish();
        Intent intent = new Intent(RunningActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void clearBackground() {
        aQuery.id(R.id.rlrunning_commom_setting).visibility(View.GONE);
        aQuery.id(R.id.rlrunning_speed_setting).visibility(View.GONE);
        aQuery.id(R.id.rlrunning_run_setting).visibility(View.GONE);

        aQuery.id(R.id.running_txt_commomMode).backgroundColor(this.getResources().getColor(R.color.white)).
                textColor(this.getResources().getColor(R.color.half_gray));
        aQuery.id(R.id.running_txt_speedMode).backgroundColor(this.getResources().getColor(R.color.white)).
                textColor(this.getResources().getColor(R.color.half_gray));
        aQuery.id(R.id.running_txt_runMode).backgroundColor(this.getResources().getColor(R.color.white)).
                textColor(this.getResources().getColor(R.color.half_gray));

    }

    public void switchToCommomMode(View v) {
        clearBackground();
        aQuery.id(R.id.rlrunning_commom_setting).visibility(View.VISIBLE);
        aQuery.id(R.id.running_txt_commomMode).backgroundColor(this.getResources().getColor(R.color.red_background_notselected)).
                textColor(this.getResources().getColor(R.color.white));

    }

    public void switchToSpeedMode(View v) {
        clearBackground();
        aQuery.id(R.id.rlrunning_speed_setting).visibility(View.VISIBLE);
        aQuery.id(R.id.running_txt_speedMode).backgroundColor(this.getResources().getColor(R.color.red_background_notselected)).
                textColor(this.getResources().getColor(R.color.white));

    }

    public void switchToRunMode(View v) {
        clearBackground();
        aQuery.id(R.id.rlrunning_run_setting).visibility(View.VISIBLE);
        aQuery.id(R.id.running_txt_runMode).backgroundColor(this.getResources().getColor(R.color.red_background_notselected)).
                textColor(this.getResources().getColor(R.color.white));

    }

    String devIp = "";
    String city = null;

    @Override
    public void onSuccess(API api, int action, Map<String, Object> map) {
        switch (action) {
            case Weather.ACTION_IP:
                onWeatherDetailsGot(map);
                break;
            case Weather.ACTION_QUERY:
                System.out.println("cityUtil ..........c");
                onWeatherDetailsGot(map);
                break;
        }
    }

    @Override
    public void onError(API api, int i, Throwable throwable) {

    }

    CityUtils cityUtils;

    class GetIP extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cityUtils = CityUtils.getInstance();
        }

        @Override
        protected Void doInBackground(Void... params) {
//            devIp = NetUtils.getIp();
//            if ((devIp == null) || devIp.equals("")) {
//                return null;
//            }
//            weather.queryByIPAddress(devIp, RunningActivity.this);
            cityUtils.startLoc();
            while (city == null) {
                try {
                    city = cityUtils.getCity();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cityUtils.release();
            weather.queryByCityName(city.substring(0, 2), RunningActivity.this);
            //  weather.queryByCityName("",RunningActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public class MyLocationListenner implements BDLocationListener { // 定位
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            location.getLatitude();
            location.getLongitude();
            location.getCity();

        }
    }
    //   Geocoder geocoder;

    private void onWeatherDetailsGot(Map<String, Object> result) {

        ArrayList<HashMap<String, Object>> results = (ArrayList<HashMap<String, Object>>) result.get("result");
        HashMap<String, Object> weathers = results.get(0);
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setColdIndex(com.mob.tools.utils.R.toString(weathers.get("coldIndex")));
        weatherInfo.setDate(TimeUtil.getCurrentDate());
        weatherInfo.setDressingIndex(com.mob.tools.utils.R.toString(weathers.get("dressingIndex")));
        weatherInfo.setPollutionIndex(com.mob.tools.utils.R.toString(weathers.get("pollutionIndex")));
        weatherInfo.setHumidity(com.mob.tools.utils.R.toString(weathers.get("humidity")));
        weatherInfo.setSunrise(com.mob.tools.utils.R.toString(weathers.get("sunrise")));
        weatherInfo.setSunset(com.mob.tools.utils.R.toString(weathers.get("sunset")));
        weatherInfo.setWind(com.mob.tools.utils.R.toString(weathers.get("wind")));
        weatherInfo.setExerciseIndex(com.mob.tools.utils.R.toString(weathers.get("exerciseIndex")));
        weatherInfo.setTemperature(com.mob.tools.utils.R.toString(weathers.get("temperature")));
        weatherInfo.setTimeStamp(System.currentTimeMillis());
        updateWeather(weatherInfo);
        weatherSpUtil.saveWeatherInfolistToSp(weatherInfo);
//        System.out.println((com.mob.tools.utils.R.toString(weathers.get("weather"))));
//        System.out.println((com.mob.tools.utils.R.toString(weathers.get("temperature"))));
//        System.out.println((com.mob.tools.utils.R.toString(weathers.get("humidity"))));
//        System.out.println((com.mob.tools.utils.R.toString(weathers.get("wind"))));
//        System.out.println((com.mob.tools.utils.R.toString(weathers.get("sunrise"))));
    }

    private void updateWeather(WeatherInfo weatherInfo) {
        aQuery.id(R.id.weather_coldIndex).text(weatherInfo.getColdIndex());
        aQuery.id(R.id.weather_dressingIndex).text(weatherInfo.getDressingIndex());
        String str = weatherInfo.getHumidity();
        if (str.length() > 0)
            aQuery.id(R.id.weather_humidity).text(str.substring(str.length() - 3, str.length()));
        aQuery.id(R.id.weather_pollutionIndex).text(weatherInfo.getPollutionIndex());
        aQuery.id(R.id.weather_sunrise).text(weatherInfo.getSunrise());
        aQuery.id(R.id.weather_sunset).text(weatherInfo.getSunset());
        aQuery.id(R.id.weather_wind).text(weatherInfo.getWind());
        aQuery.id(R.id.weather_exerciseIndex).text(weatherInfo.getExerciseIndex());
        aQuery.id(R.id.currentTemp).text(weatherInfo.getTemperature());

    }

}

