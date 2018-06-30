package com.example.maask.tourmanagementsystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.WeatherFile.CurrentWeatherResponse;
import com.example.maask.tourmanagementsystem.WeatherFile.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends Fragment {

    TextView pressure, wind, humidity, temp, city_name, status, clouds, show_unit, set_separator;
    Switch switch_unit;
    ImageView show_img;

    private FusedLocationProviderClient client;

    public static final String BASE_URL_WEATHER = "http://api.openweathermap.org/data/2.5/";
    private WeatherService weatherService;
    private String unit = "metric";

    double lat, lon;

    String urlString;

    SwipeRefreshLayout swipeRefreshLayout;

    SharedPreferences sharedPreferences;

    private static final String PREFERENCES_KEY = "store_locally";
    private static final String TEMP = "temp";
    private static final String ICON = "icon";
    private static final String CITY = "city";
    private static final String STATUS = "status";
    private static final String HUMIDITY = "humidity";
    private static final String PRESSURE = "pressure";
    private static final String CLOUD = "cloud";
    private static final String WIND = "wind";
    private static final String LAT = "lat";
    private static final String LON = "lon";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        clouds = view.findViewById(R.id.clouds);
        temp = view.findViewById(R.id.temp);
        city_name = view.findViewById(R.id.city_name);
        status = view.findViewById(R.id.status);
        pressure = view.findViewById(R.id.pressure);
        wind = view.findViewById(R.id.wind);
        humidity = view.findViewById(R.id.humidity);
        show_img = view.findViewById(R.id.show_img);
        show_unit = view.findViewById(R.id.show_unit);
        switch_unit = view.findViewById(R.id.switch_unit);
        set_separator = view.findViewById(R.id.set_separator);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        final String api_key = getString(R.string.weather_api_key);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},133);
            return getView();
        }

        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isComplete() && task.getResult() != null) {

                    Location lastLocation = task.getResult();
                    lat = lastLocation.getLatitude();
                    lon = lastLocation.getLongitude();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(LAT, String.valueOf(lat));
                    editor.putString(LON, String.valueOf(lon));
                    editor.commit();

                }
            }
        });

        if (!sharedPreferences.getString(LAT,"").isEmpty() && !sharedPreferences.getString(LON,"").isEmpty() ){

            double getLocalLat = Double.parseDouble(sharedPreferences.getString(LAT,""));
            double getLocalLon = Double.parseDouble(sharedPreferences.getString(LON,""));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_WEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            weatherService = retrofit.create(WeatherService.class);

            urlString = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",getLocalLat,getLocalLon,unit,api_key);

            getResponseFromURL(urlString);

        }

        city_name.setText(sharedPreferences.getString(CITY,""));
        pressure.setText(sharedPreferences.getString(PRESSURE,"")+" mb");
        wind.setText(sharedPreferences.getString(WIND,"")+" km/h");
        status.setText(sharedPreferences.getString(STATUS,""));
        humidity.setText(sharedPreferences.getString(HUMIDITY,"")+" %");
        clouds.setText(sharedPreferences.getString(CLOUD,"")+" %");
        setImage(sharedPreferences.getString(ICON,""));


        if (sharedPreferences.getString(TEMP,"").isEmpty()){
            temp.setText("N/A");
        }else {
            temp.setText(sharedPreferences.getString(TEMP,""));
            set_separator.setText("/");
            show_unit.setText("C");
        }

        if (!sharedPreferences.getString(LAT,"").isEmpty()) {

            switch_unit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                    if (status == true) {

                        show_unit.setText("F");
                        double fahrenheit = Double.parseDouble(sharedPreferences.getString(TEMP, "")) * 1.8 + 35;
                        temp.setText(String.valueOf(new DecimalFormat("##.##").format(fahrenheit)));

                    } else {

                        show_unit.setText("C");
                        temp.setText(sharedPreferences.getString(TEMP, ""));
                    }
                }
            });
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;

    }

    private void refreshFragment() {

        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void getResponseFromURL(String urlString) {

        Call<CurrentWeatherResponse> call = weatherService.getCurrentWeatherResponse(urlString);
        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if(response.code() == 200){
                    CurrentWeatherResponse currentWeatherResponse = response.body();


                    String getCity = currentWeatherResponse.getName();
                    double getTemp = currentWeatherResponse.getMain().getTemp();
                    int getPressure = currentWeatherResponse.getMain().getPressure();
                    double getWind = currentWeatherResponse.getWind().getSpeed();
                    List<CurrentWeatherResponse.Weather> getStatus = currentWeatherResponse.getWeather();
                    int getCloud = currentWeatherResponse.getClouds().getAll();
                    int getHumidity = currentWeatherResponse.getMain().getHumidity();

                    String icon = getStatus.get(0).getIcon();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TEMP,String.valueOf(getTemp));
                    editor.putString(CITY,getCity);
                    editor.putString(PRESSURE,String.valueOf(getPressure));
                    editor.putString(WIND,String.valueOf(getWind));
                    editor.putString(CLOUD,String.valueOf(getCloud));
                    editor.putString(HUMIDITY,String.valueOf(getHumidity));
                    editor.putString(STATUS,String.valueOf(getStatus.get(0).getMain()));
                    editor.putString(ICON,icon);
                    editor.commit();

                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Log.e("res", "onFailure: "+t.getMessage());
            }
        });

    }

    private void setImage(String icon) {

        if (icon.equals("50d") || icon.equals("50n") ){

            show_img.setImageResource(R.drawable.fog);
        }else if (icon.equals("01d") || icon.equals("01n") ){
            show_img.setImageResource(R.drawable.sunny);
        }else if (icon.equals("02d") || icon.equals("02n") ){
            show_img.setImageResource(R.drawable.few_cloude);
        }else if (icon.equals("03d") || icon.equals("03n") ){
            show_img.setImageResource(R.drawable.scattered_cloude);
        }else if (icon.equals("09d") || icon.equals("09n") ){
            show_img.setImageResource(R.drawable.shower_rain);
        }else if (icon.equals("10d") || icon.equals("10n") ){
            show_img.setImageResource(R.drawable.rain);
        }else if (icon.equals("13d") || icon.equals("13n") ){
            show_img.setImageResource(R.drawable.extreme_rain);
        }else if (icon.equals("11d") || icon.equals("11n") ){
            show_img.setImageResource(R.drawable.extreme_rain);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
