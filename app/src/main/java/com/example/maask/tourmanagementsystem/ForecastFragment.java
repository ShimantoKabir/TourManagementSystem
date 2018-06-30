package com.example.maask.tourmanagementsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.WeatherFile.ForecastResponse;
import com.example.maask.tourmanagementsystem.WeatherFile.HourlyForecastAdapter;
import com.example.maask.tourmanagementsystem.WeatherFile.WeatherService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastFragment extends Fragment {

    public static final String BASE_URL_WEATHER = "http://api.openweathermap.org/data/2.5/";
    private WeatherService weatherService;

    String urlString;

    private static final String PREFERENCES_KEY  = "store_locally";
    private static final String LAT              = "lat";
    private static final String LON              = "lon";

    SharedPreferences sharedPreferences;

    private String unit = "metric";

    private RecyclerView recyclerView;
    private List<ForecastResponse.Forecast> forecastList = new ArrayList<>();

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        recyclerView = view.findViewById(R.id.rv_for_show_hourly_forecast);

        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        final String api_key = getString(R.string.weather_api_key);

        if (!sharedPreferences.getString(LAT,"").isEmpty() && !sharedPreferences.getString(LON,"").isEmpty()){

            double getLocalLat = Double.parseDouble(sharedPreferences.getString(LAT,""));
            double getLocalLon = Double.parseDouble(sharedPreferences.getString(LON,""));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_WEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            weatherService = retrofit.create(WeatherService.class);

            urlString = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",getLocalLat,getLocalLon,unit,api_key);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Getting data .... ");
            progressDialog.show();

            Call<ForecastResponse> call = weatherService.getForecastResponse(urlString);
            call.enqueue(new Callback<ForecastResponse>() {
                @Override
                public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                    if(response.code() == 200){

                        ForecastResponse forecastResponse = response.body();

                        HourlyForecastAdapter hourlyForecastAdapter = new HourlyForecastAdapter(getActivity(),forecastResponse.getList());

                        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
                        lm.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(lm);
                        recyclerView.setAdapter(hourlyForecastAdapter);
                        progressDialog.dismiss();

                    }
                }

                @Override
                public void onFailure(Call<ForecastResponse> call, Throwable t) {
                    Log.e("res", "onFailure: "+t.getMessage());
                }
            });

        }else {
            Toast.makeText(getActivity(), "ERROR : Sir/Mam did not get location yet ... ! ", Toast.LENGTH_SHORT).show();
        }


        return view;
    }
}
