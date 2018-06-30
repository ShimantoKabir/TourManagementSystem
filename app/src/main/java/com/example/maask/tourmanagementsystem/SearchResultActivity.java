package com.example.maask.tourmanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.SearchManager;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.maask.tourmanagementsystem.WeatherFile.CitySuggestions;
import com.example.maask.tourmanagementsystem.WeatherFile.CityWeatherResponse;
import com.example.maask.tourmanagementsystem.WeatherFile.ForecastResponse;
import com.example.maask.tourmanagementsystem.WeatherFile.HourlyForecastAdapter;
import com.example.maask.tourmanagementsystem.WeatherFile.WeatherService;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;

    TextView pressure, wind, humidity, temp, city_name, status, clouds,show_unit,set_separator;

    public static final String BASE_URL_WEATHER = "http://api.openweathermap.org/data/2.5/";

    private WeatherService weatherService;

    ImageView show_img;

    Switch switch_unit;

    String urlString;
    String urlStringForForecast;

    private String unit = "metric";

    String getCityNameFormSearch;

    private RecyclerView forecastRecyclerView;
    private List<ForecastResponse.Forecast> forecastList;
    Button hourlyForecastBT;
    HourlyForecastAdapter hourlyForecastAdapter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        getCityNameFormSearch = intent.getStringExtra(SearchManager.QUERY);
        SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, CitySuggestions.AUTHORITY, CitySuggestions.MODE);
        searchRecentSuggestions.saveRecentQuery(getCityNameFormSearch, null);

        clouds = findViewById(R.id.clouds);
        temp = findViewById(R.id.temp);
        city_name = findViewById(R.id.city_name);
        status = findViewById(R.id.status);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        show_img = findViewById(R.id.show_img);
        show_unit = findViewById(R.id.show_unit);
        switch_unit = findViewById(R.id.switch_unit);
        set_separator = findViewById(R.id.set_separator);
        hourlyForecastBT = findViewById(R.id.hourly_forecast);

        toolbar = findViewById(R.id.custom_toolbar);
        toolbar.setTitle(getCityNameFormSearch);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ... ");
        progressDialog.show();

        final String api_key = getString(R.string.weather_api_key);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_WEATHER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherService = retrofit.create(WeatherService.class);

        urlString = String.format("weather?q=%s&units=%s&appid=%s", getCityNameFormSearch,unit,api_key);

        Call<CityWeatherResponse> call = weatherService.getCityWeatherResponse(urlString);
        call.enqueue(new Callback<CityWeatherResponse>() {
            @Override
            public void onResponse(Call<CityWeatherResponse> call, Response<CityWeatherResponse> response) {

                if(response.code() == 200){

                    CityWeatherResponse cityWeatherResponse = response.body();

                    String getCity = cityWeatherResponse.getName();
                    double getTemp = cityWeatherResponse.getMain().getTemp();
                    Double getPressure = cityWeatherResponse.getMain().getPressure();
                    double getWind = cityWeatherResponse.getWind().getSpeed();
                    List<CityWeatherResponse.Weather> getStatus = cityWeatherResponse.getWeather();
                    int getCloud = cityWeatherResponse.getClouds().getAll();
                    int getHumidity = cityWeatherResponse.getMain().getHumidity();

                    String icon = getStatus.get(0).getIcon();

                    city_name.setText(getCity);
                    temp.setText(String.valueOf(getTemp));
                    pressure.setText(String.valueOf(getPressure)+" mb");
                    wind.setText(String.valueOf(getWind)+" km/h");
                    status.setText(getStatus.get(0).getMain());
                    humidity.setText(String.valueOf(getHumidity)+" %");
                    clouds.setText(String.valueOf(getCloud)+" %");
                    set_separator.setText("/");
                    show_unit.setText("C");

                    setImage(icon);

                    setUnit(response,getTemp);

                    progressDialog.dismiss();

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SearchResultActivity.this, "ERROR : City not found !", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<CityWeatherResponse> call, Throwable t) {
                Log.e("res", "onFailure: "+t.getMessage());
            }
        });

        urlStringForForecast = String.format("forecast?q=%s&units=%s&appid=%s", getCityNameFormSearch,unit,api_key);

        Call<ForecastResponse> callForForecast = weatherService.getForecastResponse(urlStringForForecast);
        callForForecast.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if(response.code() == 200){

                    ForecastResponse forecastResponse = response.body();
                    forecastList = forecastResponse.getList();

                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("res", "onFailure: "+t.getMessage());
            }
        });

        hourlyForecastBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
                View inflateView = getLayoutInflater().inflate(R.layout.show_city_forecast_lv,null);
                forecastRecyclerView = inflateView.findViewById(R.id.rv_for_show_hourly_forecast);

                LinearLayoutManager lm = new LinearLayoutManager(SearchResultActivity.this);
                lm.setOrientation(LinearLayoutManager.VERTICAL);
                forecastRecyclerView.setLayoutManager(lm);

                hourlyForecastAdapter = new HourlyForecastAdapter(SearchResultActivity.this,forecastList);
                forecastRecyclerView.setAdapter(hourlyForecastAdapter);
                builder.setView(inflateView);
                builder.setPositiveButton("OK",null);
                builder.show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.profile:
                Intent goProfile = new Intent(SearchResultActivity.this,ProfileActivity.class);
                startActivity(goProfile);
            break;

            case android.R.id.home:
                Intent intent = new Intent(SearchResultActivity.this,WeatherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            break;

        }

        return super.onOptionsItemSelected(item);

    }

    private void setUnit(Response<CityWeatherResponse> response, final double getTemp) {

        if (response.isSuccessful()){

            switch_unit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                    if (status == true) {

                        show_unit.setText("F");
                        double fahrenheit = getTemp * 1.8 + 35;
                        temp.setText(String.valueOf(new DecimalFormat("##.##").format(fahrenheit)));

                    } else {

                        show_unit.setText("C");
                        temp.setText(String.valueOf(getTemp));
                    }
                }
            });

        }else {
            Toast.makeText(this, "Sir/Mam did not find the city !", Toast.LENGTH_SHORT).show();
        }

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
}
