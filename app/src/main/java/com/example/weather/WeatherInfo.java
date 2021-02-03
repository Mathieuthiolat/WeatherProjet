package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class WeatherInfo extends AppCompatActivity {
    private ImageView mImageViewHintHabit;
    private ImageButton getCurrentLocation;
    private LocationManager locationManager;
    @SuppressLint("ServiceCast")
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(WeatherInfo.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(WeatherInfo.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try {
                        getcurrentZipCode(location.getLatitude(),location.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }else{
            getCurrentWeather("83300","fr");
        }


        //On recupère les coords pour trouver le zipcode plus tard
        getCurrentLocation = findViewById(R.id.ButtonLocationAuto);
        getCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(WeatherInfo.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(WeatherInfo.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
                    ActivityCompat.requestPermissions(WeatherInfo.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600, 1, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            try {
                                getcurrentZipCode(location.getLatitude(),location.getLongitude());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
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


    //Methode pour recuperer le zipcode
    private void getcurrentZipCode(double latitude , double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        Address address=null;
        String zipcode="";
        String Country="";
        if (addresses != null && addresses.size() > 0) {
            Country = addresses.get(0).getCountryCode();
            for (int i = 0; i < addresses.size(); i++) {
                address = addresses.get(i);
                if (address.getPostalCode() != null) {
                    zipcode = address.getPostalCode();
                    break;
                }
            }
        }
        if(zipcode != null ){
            getCurrentWeather(zipcode,Country);
        }
    }
    private void getCurrentWeather(String zipCode,String Country) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        //Ancienne requete
        //String url = "https://api.openweathermap.org/data/2.5/weather?zip=" + zipCode +','+Country+ "&appid=7b84cb6c4628843c95c6aa9723b671fb";
        String url = "https://api.openweathermap.org/data/2.5/forecast/daily?q="+zipCode+"&cnt=2&appid=7b84cb6c4628843c95c6aa9723b671fb";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        updateUI(response,Country);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
    }
    private void updateUI(JSONObject response,String Country) {
        try {
            JSONArray listJour = response.getJSONArray("list");
            JSONObject secondDay = listJour.getJSONObject(1);
            int cloudness = secondDay.getInt("clouds");

            JSONArray weather = secondDay.getJSONArray("weather");
            JSONObject firstWeather = weather.getJSONObject(0);
            String weatherInfo = firstWeather.getString("main");

            mImageViewHintHabit = findViewById(R.id.ImageViewHintHabit);
            if((weatherInfo != "Clouds" || cloudness >50 ) &&  weatherInfo == "Rain" ){
                mImageViewHintHabit.setImageResource(R.drawable.umbrela);
            }else if (weatherInfo != "Rain" && cloudness < 35  ){
                mImageViewHintHabit.setImageResource(R.drawable.sunglasses);
            }else{
                mImageViewHintHabit.setImageResource(R.drawable.fine);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}