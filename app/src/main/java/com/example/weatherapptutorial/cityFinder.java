package com.example.weatherapptutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

public class cityFinder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);
        Spinner citySpinner = findViewById(R.id.citySpinner);
        ImageView backButton=findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        // 設定下拉選單選項
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.city_array, // 使用 string.xml 中的 city_array
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        // 預設選擇第一個項目 (提示文字)
        citySpinner.setSelection(0, false);

        // 返回按鈕點擊事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCity = parent.getItemAtPosition(position).toString();
                    // 傳遞選取縣市值到下一個 Activity
                    Intent intent = new Intent(cityFinder.this, MainActivity.class);
                    intent.putExtra("City", selectedCity);
                    startActivity(intent);
                    Log.d("MainActivity", "選擇的城市: " + intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 當未選擇項目時，無需執行操作
            }
        });
    }
}