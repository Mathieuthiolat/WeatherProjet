package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class WeatherInfo extends AppCompatActivity {
    private TextView mTextViewDate;
    private ImageView mImageViewHintHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        ActionBar actionBar = getSupportActionBar();
        Log.v(Config.LOG_TAG, actionBar != null ? "actionBar not null" : "actionBar null");
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Date date = new Date();
        mTextViewDate = findViewById(R.id.MessageIntroInfo);
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String formattedText = String.format(getString(R.string.introActyInfo), DateFormat.getDateInstance(DateFormat.FULL).format(date));
        mTextViewDate.setText(formattedText);
        getCurrentWeather("83300");
    }


    private void getCurrentWeather(String zipCode) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + ",fr&appid=7b84cb6c4628843c95c6aa9723b671fb";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        updateUI(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }
    private void updateUI(JSONObject response) {
        try {
            String name = response.getString("name");
            JSONObject clouds = response.getJSONObject("clouds");
            JSONObject main = response.getJSONObject("main");
            double temp = main.getDouble("temp");
            temp -= 273.15;
            int cloudness = clouds.getInt("all");
            JSONArray weather = response.getJSONArray("weather");
            JSONObject firstWeather = weather.getJSONObject(0);
            String infoWeather = firstWeather.getString("main");
            mImageViewHintHabit = findViewById(R.id.ImageViewHintHabit);

            if(temp >= 28 && cloudness <= 35 && (infoWeather != "Rain" || infoWeather != "Snow" ||infoWeather !=  "Extreme ")){ //Moyenne environs pour savoir si il fait "Beau"
                mImageViewHintHabit.setImageResource(R.drawable.sunglasses);
            }else if(infoWeather != "Rain" || infoWeather != "Snow" ||infoWeather !=  "Extreme "){
                mImageViewHintHabit.setImageResource(R.drawable.umbrela);
            }else{
                mImageViewHintHabit.setImageResource(R.drawable.fine);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

}