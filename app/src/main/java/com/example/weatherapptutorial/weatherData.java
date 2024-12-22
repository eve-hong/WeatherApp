package com.example.weatherapptutorial;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class weatherData {

    String mTemperature;
    String micon;
    public String mWeatherType;
    private int mCondition;

    public static weatherData fromJson(JSONObject jsonObject)
    {

        try
        {
            weatherData weatherD=new weatherData();
            weatherD.mWeatherType=jsonObject.getString("parameterName");
            Log.d("TestLog3", "weatherD.mWeatherType-開始"+weatherD.mWeatherType);
            weatherD.micon=updateWeatherIcon(weatherD.mWeatherType);
            Log.d("TestLog3", "weatherD.micon-開始"+weatherD.micon);
            Log.d("TestLog3", "weatherD_Output-開始"+weatherD);
            return weatherD;
        }

         catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String updateWeatherIcon(String condition)
    {
        if(condition.contains("雷")==true)
        {
            return "thunderstrom1";
        }
        else if(condition.contains("短暫")==true&&condition.contains("雨")==true)
        {
            return "lightrain";
        }
        else if(condition.contains("雨")==true)
        {
            return "shower";
        }
       else  if(condition.contains("雪")==true)
        {
            return "snow1";
        }
        else if(condition.contains("霧")==true)
        {
            return "fog";
        }
        else if(condition.contains("雲")==true&&condition.contains("晴")==true)
        {
            return "overcast";
        }
        else if(condition.contains("雲")==true || condition.contains("陰")==true)
        {
            return "cloudy";
        }
        else if(condition.contains("晴")==true)
        {
            return "sunny";
        }

        return "dunno";
    }

    public String getmTemperature() {
        return mTemperature+"°C";
    }

    public String getMicon() {
        return micon;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
