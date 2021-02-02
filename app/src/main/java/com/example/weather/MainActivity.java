package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String firstName = "Mathieu";
    private TextView mTextViewHomeGreeting;
    public static String MESSAGE_KEY = "Hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewHomeGreeting = findViewById(R.id.TextViewHomeGreeting);
        String formattedText = String.format(getString(R.string.home_greeting), firstName);
        mTextViewHomeGreeting.setText(formattedText);


        Button mButtonHomeScreen1 = findViewById(R.id.ButtonHomeScreen1);
        mButtonHomeScreen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });
        Button mButtonHomeScreen2 = findViewById(R.id.ButtonHomeScreen2);
        mButtonHomeScreen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherInfo.class);
                startActivity(intent);
            }
        });

        ImageView mImageViewHomeMeteoFranceLogo = findViewById(R.id.ImageViewHomeMeteoFranceLogo);
        mImageViewHomeMeteoFranceLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebViewMeteoFranceActivity.class);
                startActivity(intent);
            }
        });
    }

}