package com.example.maask.tourmanagementsystem.WeatherFile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Maask on 1/27/2018.
 */

public interface WeatherService {

    @GET()
    Call<CurrentWeatherResponse> getCurrentWeatherResponse(@Url String urlString);

    @GET()
    Call<CityWeatherResponse> getCityWeatherResponse(@Url String urlString);

    @GET()
    Call<ForecastResponse> getForecastResponse(@Url String urlString);

}
