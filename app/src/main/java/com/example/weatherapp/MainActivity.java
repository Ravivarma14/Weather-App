package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.Adapter.WeatherRVAdapter;
import com.example.weatherapp.Model.WeatherRecyclerViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    RelativeLayout homeRL;
    ProgressBar loadingPB;
    TextView cityNameTV, temperatureTV, conditionTV, forecastTextTV;
    RecyclerView weatherRV;
    TextInputEditText cityEDT;
    TextInputLayout hintTV;
    ImageView backIV, iconMoonIV, iconSunIV, searchIV;
    ArrayList<WeatherRecyclerViewModel> weatherRecyclerViewModelList;
    WeatherRVAdapter weatherRVAdapter;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    int PERMISSION_CODE = 1;
    String cityName;

    public Criteria criteria;
    public String bestProvider;

    static final String TAG = "Weather.MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dexOutputDir = getCodeCacheDir();
        dexOutputDir.setReadOnly();

        //dc67a373f2e546f7915183247241704 -----real one

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        homeRL = findViewById(R.id.rlHome);
        loadingPB = findViewById(R.id.pbLoading);
        cityNameTV = findViewById(R.id.tvCityName);
        temperatureTV = findViewById(R.id.tvTemperature);
        conditionTV = findViewById(R.id.tvCondition);
        forecastTextTV=findViewById(R.id.tvForecastText);
        weatherRV = findViewById(R.id.rvWeather);
        cityEDT = findViewById(R.id.edtCity);
        hintTV=findViewById(R.id.tilCity);
        backIV = findViewById(R.id.ivBack);
        iconMoonIV = findViewById(R.id.ivMoonIcon);
        iconSunIV = findViewById(R.id.ivSunIcon);
        searchIV = findViewById(R.id.ivSearch);

        weatherRecyclerViewModelList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRecyclerViewModelList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    cityName = getCityName(location.getLongitude(), location.getLatitude());
                    Log.d(TAG, "cityName: " + cityName);
                    getWeatherInfo(cityName);
                }
            }
        });


        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEDT.getText().toString();
                Log.d(TAG, "search text: " + city);
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    //to hide keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    getWeatherInfo(city);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address add : addresses) {
                if (add != null) {
                    String city = add.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Toast.makeText(this, "City not found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=d654ccdf298d477db2963703242104&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        //cityNameTV.setText(cityName);
        Log.d(TAG, "wether info: cityname= " + cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "on getting response");
                cityEDT.setText("");
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRecyclerViewModelList.clear();
                cityNameTV.setText(cityName);

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Log.d(TAG, "condition icon url: " + conditionIcon);
                    //Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    Log.d(TAG, "is day: " + (isDay == 1));
                    if (isDay == 1) {

                        //morning
                        Log.d(TAG, "morning");
                        //Picasso.get().load("https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTBfZmFudGFzeV9jbG91ZHNfYmFja2dyb3VuZF9faGludF9vZl9wcmVjaXNpb25pc18xNGRmMDQ2ZS01NTlmLTQ4ZGEtYTUyZS01Y2VmMzI5M2VkMDNfMS5qcGc.jpg").into(backIV);
                        backIV.setImageDrawable(getDrawable(R.drawable.sky_bg));
                        backIV.startAnimation(animate());

                        iconSunIV.setImageDrawable(getDrawable(R.drawable.sun));
                        Animation anim = new TranslateAnimation(iconSunIV.getLeft(), iconSunIV.getLeft(), 500f, iconSunIV.getTop());
                        anim.setDuration(1200);
                        anim.setFillAfter(true);
                        anim.setFillEnabled(true);
                        iconSunIV.setAnimation(anim);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            setColorForViews(getColor(R.color.black));
                            searchIV.setImageDrawable(getDrawable(R.drawable.search_icon));
                        }
                    } else {
                        Log.d(TAG, "night");
                        //Picasso.get().load("https://www.mordeo.org/files/uploads/2020/05/Extreme-Weather-Dark-Clouds-Lightning-4K-Ultra-HD-Mobile-Wallpaper-1152x2048.jpg").into(backIV);
                        backIV.setImageDrawable(getDrawable(R.drawable.night));
                        backIV.startAnimation(animate());

                        iconMoonIV.setImageDrawable(getDrawable(R.drawable.moon));
                        Animation anim = new TranslateAnimation(-750f, iconMoonIV.getLeft(), iconMoonIV.getTop(), iconMoonIV.getTop());
                        anim.setDuration(1500);
                        anim.setFillAfter(true);
                        anim.setFillEnabled(true);
                        iconMoonIV.setAnimation(anim);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            setColorForViews(getColor(R.color.white));
                            searchIV.setImageDrawable(getDrawable(R.drawable.search_icon_white));
                        }
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        int isday = hourObj.getInt("is_day");
                        weatherRecyclerViewModelList.add(new WeatherRecyclerViewModel(time, temper, img, wind, isday));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "on error listner");
                cityEDT.setText("");
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void setColorForViews(int color) {
        temperatureTV.setTextColor(color);
        conditionTV.setTextColor(color);
        cityNameTV.setTextColor(color);
        forecastTextTV.setTextColor(color);
        cityEDT.setTextColor(color);
        hintTV.setHintTextColor(ColorStateList.valueOf(color));
    }
    private AlphaAnimation animate(){
        AlphaAnimation animation1 = new AlphaAnimation(0.4f, 1.0f);
        animation1.setDuration(750);
        animation1.setStartOffset(1000);
        animation1.setFillAfter(true);
        return animation1;
    }
}