package net.com.gopal.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;


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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeHome;
    private ProgressBar progressBar;
    private ImageView backgroundImage, search,iconView ;
    private TextView cityNameText, temperature,condition;
    private TextInputEditText editText;
    private RecyclerView recyclerView;
    private ArrayList<WeatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PREMISSION_CODE = 1;
    private String cityName;


    @SuppressLint({"MissingInflatedId", "ServiceCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        relativeHome = findViewById(R.id.relativeHome);
        progressBar = findViewById(R.id.progressBar);
        backgroundImage = findViewById(R.id.backgroundImage);
        search   = findViewById(R.id.iconSearch);
        iconView = findViewById(R.id.iconView);
        cityNameText = findViewById(R.id.cityNameText);
        temperature = findViewById(R.id.temperature);
        condition = findViewById(R.id.condition);
        editText = findViewById(R.id.editText);
        recyclerView = findViewById(R.id.recycleView);
        weatherModelArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModelArrayList);
        recyclerView.setAdapter(weatherAdapter);



        locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PREMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            Toast.makeText(this, "Unable to retrieve current Location", Toast.LENGTH_SHORT).show();
        }

//
//        Location location= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//        cityName = getCityName(location.getLongitude(), location.getLatitude());
//        getWeatherInfo(cityName);
//        if (location != null) {
//            cityName = getCityName(location.getLongitude(), location.getLatitude());
//        } else {
//            Toast.makeText(this, "Unable to retrieve current Location", Toast.LENGTH_SHORT).show();
//        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editText.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }else{
                    editText.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == PREMISSION_CODE){
                    if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "Please provide permission", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
    }

    private String getCityName(double Longitude, double Latitude)
    {
        String cityName = "sdfgdf";

        try {
            Geocoder geo = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation( Latitude,Longitude, 10);

           for(Address adr : addresses){
                    if (adr!=null ){
                        String city = adr.getLocality();
//                        if (city!=null && city.equals("")){
                            cityName= city;
//                        }
//                        else{
//                            Log.d("tag", "City not found");
//                            Toast.makeText(this, " City not found", Toast.LENGTH_SHORT).show();
//                        }
                    }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }
//
//    private String getCityName(double latitude, double longitude) {
//        String cityName = "";
//
//        try {
//            Geocoder geo = new Geocoder(getBaseContext(), Locale.getDefault());
//            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
//
//            if (!addresses.isEmpty()) {
//                Address address = addresses.get(0);
//                String city = address.getLocality();
//                if (city != null && !city.isEmpty()) {
//                    cityName = city;
//                } else {
//                    Log.d("tag", "City name not found in the top address");
//                }
//            } else {
//                Log.d("tag", "No address found for the given coordinates");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            cityName = "Error getting city name";
//        }
//
//        return cityName;
//    }



    private void getWeatherInfo(String cityName){
//        String url = "http://api.weatherapi.com/v1/forecast.json?key=bd35c3bc9e3147cba4c71830242501&q=" + cityName + "&days=1&aqi=no&alerts=no";
        String url="http://api.weatherapi.com/v1/forecast.json?key=bd35c3bc9e3147cba4c71830242501&q=Kathmandu&days=1&aqi=no&alerts=no";
        //        String url = "http://api.weatherapi.com/v1/forecast.json?key=bd35c3bc9e3147cba4c71830242501&q="+ this.cityName +"&days=1&aqi=no&alerts=no";
        cityNameText.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                weatherModelArrayList.clear();

                try {
                    String temperatureV = response.getJSONObject("current").getString("temp_c");
                    temperature.setText(temperatureV+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String conditionText = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
//                    String conditionCode = response.getJSONObject("current").getJSONObject("condition").getString("code");

                    Picasso.get().load("http:".concat(conditionIcon)).into(iconView);
                    condition.setText(conditionText);
                    if (isDay==1){
                        Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtxXsULAN66qD8BQ2DCcsxNfLzmWzki8M5tRLffQtnuFo_QCg-aCnhpZQwaBlBBoqdPEo&usqp=CAU").into(backgroundImage);

                    }
                    else{
                        Picasso.get().load("https://thumbs.dreamstime.com/b/vertical-photo-clear-night-sky-milky-way-huge-amount-stars-landscape-205856007.jpg").into(backgroundImage);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastToday= forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastToday.getJSONArray("hour");
                    for (int i=0; i<hourArray.length(); i++){
                        JSONObject hourobj = hourArray.getJSONObject(i);
                        String time = hourobj.getString("time");
                        String temp = hourobj.getString("temp_c");
                        String icon = hourobj.getJSONObject("condition").getString("icon");
                        String windSpeed = hourobj.getString("wind_kph");
                    weatherModelArrayList.add(new WeatherModel(time,temp, icon, windSpeed));

                    }
                    weatherAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter Valid City NAme", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}