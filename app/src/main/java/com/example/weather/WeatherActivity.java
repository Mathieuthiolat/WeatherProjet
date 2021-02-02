package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity {

    private static final String FILENAME = "last-weather";

    private TextView mTextViewDate;
    private TextView mTextViewCityName;
    private TextView mTextViewTempValue;
    private TextView mTextViewHumidityValue;
    private Button mButtonLocationSearch;
    private TextInputEditText mTextInputEditTextLocationSearch;
    private ImageView mImageViewWeatherCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.MESSAGE_KEY);

        ActionBar actionBar = getSupportActionBar();
        Log.v(Config.LOG_TAG, actionBar != null ? "actionBar not null" : "actionBar null");
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTextViewDate = findViewById(R.id.TextViewDate);
        mTextViewCityName = findViewById(R.id.TextViewCityName);
        mTextViewTempValue = findViewById(R.id.TextViewTempValue);
        mTextViewHumidityValue = findViewById(R.id.TextViewHumidityValue);
        mImageViewWeatherCondition = findViewById(R.id.ImageViewWeatherCondition);

        Date date = new Date();
        mTextViewDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));

//        getCurrentWeather("33000");

        JSONObject response = readFromCache();
        if (response != null) {
            updateUI(response);
        }

        mButtonLocationSearch = findViewById(R.id.ButtonLocationSearch);
        mTextInputEditTextLocationSearch = findViewById(R.id.TextInputEditTextLocationSearch);
        mButtonLocationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zipCode = "";
                if (mTextInputEditTextLocationSearch.getText() != null)
                    zipCode = mTextInputEditTextLocationSearch.getText().toString();
                if (zipCode.length() == 5) {
                    getCurrentWeather(zipCode);
                }
                View focusedView = WeatherActivity.this.getCurrentFocus();
                if (focusedView != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getCurrentWeather(String zipCode) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + ",fr&appid=7b84cb6c4628843c95c6aa9723b671fb";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                            storeInCache(response);
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
            JSONObject main = response.getJSONObject("main");
            double temp = main.getDouble("temp");
            temp -= 273.15;
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.CEILING);
            String fTemp = df.format(temp);
            int humidity = main.getInt("humidity");
            JSONArray weather = response.getJSONArray("weather");
            JSONObject firstWeather = weather.getJSONObject(0);
            String icon = firstWeather.getString("icon");
            mTextViewCityName.setText(name);
            mTextViewTempValue.setText(fTemp);
            mTextViewHumidityValue.setText(String.valueOf(humidity));
            Picasso.get().load("https://openweathermap.org/img/wn/" + icon + "@2x.png").into(mImageViewWeatherCondition);
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private void storeInCache(JSONObject response) {
        File file = new File(getCacheDir(), FILENAME);
        String sResponse = response.toString();
        try (FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(sResponse.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readFromCache() {
        String contents;
        JSONObject response = null;
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILENAME);
            if (fis == null) {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            contents = stringBuilder.toString();
        }
        try {
            response = new JSONObject(contents);
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }
        return response;
    }
}
