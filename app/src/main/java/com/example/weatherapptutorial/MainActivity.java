package com.example.weatherapptutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private String mTemperature,micon,mcity,mWeatherType;
    final String API_KEY = "CWA-4BC72D4A-521E-48EC-B3D6-20ADA92E7505";
    final String WEATHER_URL = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=";

    //final long MIN_TIME = 5000;
    //final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    TextView NameofCity, weatherState, Temperature;
    GifImageView mweatherIcon;
    private RequestQueue queue;

    RelativeLayout mCityFinder;


    LocationManager mLocationManager;
    LocationListener mLocationListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);

        weatherState = findViewById(R.id.weatherCondition);
        Temperature = findViewById(R.id.TempRange);
        mweatherIcon = findViewById(R.id.weatherIcon);
        mCityFinder = findViewById(R.id.cityFinder);
        NameofCity = findViewById(R.id.cityName);

        //選擇其他縣市名稱
        mCityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cityFinder.class);
                startActivity(intent);
            }
        });

        // 接收來自 cityFinder 傳遞的縣市名稱
        Intent intent = getIntent();
        String selectedCity = intent.getStringExtra("City");

        // 設定預設或使用者選擇的縣市
        String fullWeatherUrl = WEATHER_URL + API_KEY + "&locationName=" +
                (selectedCity != null ? selectedCity : "新北市");

        // 呼叫方法請求天氣數據
        fetchWeatherData(fullWeatherUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 每次返回時檢查更新縣市資料
        Intent mIntent=getIntent();
        String city= mIntent.getStringExtra("City");

        // 若縣市為 null，設定預設值
        getWeatherForNewCity(city != null ? city : "新北市");
    }

    private void fetchWeatherData(String urlString) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null,
                response -> {
                    try {
                        Log.d("TestLog3", "程式進入 fetchWeatherData 方法");

                            // 使用 JSON 解析數據
                            if (!response.has("records")) {
                                Log.e("TestLog3", "API 回應中沒有 records 欄位");
                                return;
                            }
                            JSONObject records = response.getJSONObject("records");

                            JSONArray locations = records.getJSONArray("location");
                            if (locations.length() == 0) {
                                Log.e("TestLog3", "location 資料為空");
                                return;
                            }
                            Log.d("TestLog3","fetchWeatherData_Input_locations開始-"+ locations);

                            if (locations.length() > 0) {
                                JSONObject cityData = locations.getJSONObject(0);
                                Log.d("TestLog3","fetchWeatherData_Input_cityData開始-"+ cityData);
                                String cityName = cityData.getString("locationName");
                                Log.d("TestLog3","fetchWeatherData_Input_cityName開始-"+ cityName);
                                JSONArray weatherElements = cityData.getJSONArray("weatherElement");
                                Log.d("TestLog3","fetchWeatherData_Input_weatherElements開始-"+ weatherElements);
                                TextView CurrentCity = findViewById(R.id.cityName);
                                CurrentCity.setText(cityName);

                                // 初始化變數
                                JSONArray minTArray = null;
                                JSONArray maxTArray = null;
                                JSONObject WxObject = null;

                                // 設定 GMT 時區
                                SimpleDateFormat sdfGMT  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                sdfGMT .setTimeZone(TimeZone.getTimeZone("GMT"));

                                // 定義本地時區的 SimpleDateFormat 用於顯示本地時間
                                SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                sdfLocal.setTimeZone(TimeZone.getDefault()); // 設定為本地時區

                                // 獲取當前本地時間
                                Date currentDate  = new Date(); // 系統時間的 Date 對象，默認為本地時間

                                // 提取 MinT 和 MaxT
                                for (int i = 0; i < weatherElements.length(); i++) {

                                    String element = weatherElements.getJSONObject(i).getString("elementName");
                                    Log.d("TestLog3", "JSONObject_element-開始"+element);
                                    //String elementName = element.getJSONObject("elementName").getString("elementName");
                                    if (element.equals("MinT")) {

                                        minTArray = weatherElements.getJSONObject(i).getJSONArray("time");
                                        Log.d("TestLog3", "minTArray-開始"+minTArray);
                                    } else if (element.equals("MaxT")) {
                                        maxTArray = weatherElements.getJSONObject(i).getJSONArray("time");
                                        Log.d("TestLog3", "maxTArray-開始"+maxTArray);
                                    }

                                    if(element.equals("Wx"))
                                    {
                                        WxObject = weatherElements.getJSONObject(i).getJSONArray("time").getJSONObject(i).getJSONObject("parameter");
                                        Log.d("TestLog3", "WxArray-開始"+WxObject);
                                        weatherData weatherD=weatherData.fromJson(WxObject);

                                        if (weatherD == null) {
                                            Log.e("TestLog3", "weatherD為null");
                                        }
                                        Log.d("TestLog3", "updateUI-開始");
                                        updateUI(weatherD);
                                    }
                                }

                                if (minTArray == null || maxTArray == null) {
                                    Log.e("TestLog3", "未找到 MinT 或 MaxT 資料");
                                }


                                JSONObject minT = minTArray.getJSONObject(0);
                                JSONObject maxT = maxTArray.getJSONObject(0);

                                // 提取時間範圍和溫度值
                                String startTime = minT.getString("startTime");
                                String endTime = minT.getString("endTime");
                                String minTemperature = minT.getJSONObject("parameter").getString("parameterName");
                                String maxTemperature = maxT.getJSONObject("parameter").getString("parameterName");

                                // 將時間字串轉為 Date 對象
                                Date startDate = sdfGMT.parse(startTime); // 保持 GMT
                                Date endDate = sdfGMT.parse(endTime); // 保持 GMT

                                // 調整本地時間
                                sdfLocal.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                                String currentDateLocal = sdfLocal.format(currentDate);

                                Log.d("TestLog3", "startDate: " + startDate + ", endDate: " + endDate + ", currentDate : " + currentDateLocal );

                                // 更新 UI
                                runOnUiThread(() -> {
                                    TextView timeView = findViewById(R.id.timeTextView);
                                    TextView weatherInfo = findViewById(R.id.TempRange);
                                    timeView.setText(startTime.substring(0, 16) + " - " + endTime.substring(0, 16));
                                    weatherInfo.setText(minTemperature + " - " + maxTemperature + "°C");
                                    Log.d("TestLog3", "當前時間範圍內的天氣數據已更新到 UI");
                                });
                            }

                    } catch (JSONException | ParseException e) {
                        Log.e("TestLog3", "解析 JSON 時出現錯誤: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("TestLog3", "API 請求錯誤: " + error.getMessage());
                });
        queue.add(jsonObjectRequest);
    }

    private void getWeatherForNewCity(String city)
    {
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("apiKey",API_KEY);
        letsdoSomeNetworking(params);
        Log.d("TestLog1", "getWeatherForNewCity方法-完成");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Location successfully obtained ",Toast.LENGTH_SHORT).show();
                //getWeatherForCurrentLocation();
            }
            else
            {
                //user denied the permission
            }
        }
    }


    private  void letsdoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(MainActivity.this , "即時天氣數據更新", Toast.LENGTH_SHORT).show();
                Log.d("TestLog2", "letsdoSomeNetworking_I-開始"+ response);
                weatherData weatherD=weatherData.fromJson(response);
                updateUI(weatherD);
                Log.d("TestLog2", "letsdoSomeNetworking_O-結束"+ weatherD);
               // super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TestLog3", "程式進入letsdoSomeNetworking方法[失敗]");
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private  void updateUI(weatherData weather){
        //Temperature.setText(weather.getmTemperature());
        //NameofCity.setText(weather.getMcity());
        Log.d("TestLog3", "weather-開始:"+ weather.getmWeatherType());

        weatherState.setText(weather.getmWeatherType());
        Log.d("TestLog3", "resourceID-開始"+ getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName()));
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());

        mweatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }
}