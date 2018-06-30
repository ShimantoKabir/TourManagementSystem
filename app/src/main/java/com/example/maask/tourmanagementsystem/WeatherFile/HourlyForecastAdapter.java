package com.example.maask.tourmanagementsystem.WeatherFile;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maask.tourmanagementsystem.R;

import java.util.List;

/**
 * Created by Maask on 1/28/2018.
 */

public class HourlyForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FragmentActivity activity;
    private List<ForecastResponse.Forecast> forecasts;

    public HourlyForecastAdapter(FragmentActivity activity,List<ForecastResponse.Forecast> forecasts) {
        this.activity = activity;
        this.forecasts = forecasts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        RecyclerView.ViewHolder viewHolder = null;
        View v = inflater.inflate(R.layout.single_weather_forecast,parent,false);
        viewHolder = new HourlyForecastViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        HourlyForecastViewHolder viewHolder = (HourlyForecastViewHolder) holder;
        ForecastResponse.Forecast forecast = forecasts.get(position);

        String getDate = forecast.getDtTxt();
        String[] splitDate = getDate.split(" ");

        viewHolder.date.setText("Date : "+splitDate[0]);
        viewHolder.time.setText("Time : "+splitDate[1]);

        viewHolder.status.setText(forecast.getWeather().get(0).getMain());
        viewHolder.temp.setText("Temp : "+String.valueOf(forecast.getMain().getTemp())+" c");

        setImage(viewHolder,forecast.getWeather().get(0).getIcon());


    }

    private void setImage(HourlyForecastViewHolder viewHolder, String icon) {

        if (icon.equals("50d") || icon.equals("50n") ){
            viewHolder.icon.setImageResource(R.drawable.fog);
        }else if (icon.equals("01d") || icon.equals("01n") ){
            viewHolder.icon.setImageResource(R.drawable.sunny);
        }else if (icon.equals("02d") || icon.equals("02n") ){
            viewHolder.icon.setImageResource(R.drawable.few_cloude);
        }else if (icon.equals("03d") || icon.equals("03n") ){
            viewHolder.icon.setImageResource(R.drawable.scattered_cloude);
        }else if (icon.equals("09d") || icon.equals("09n") ){
            viewHolder.icon.setImageResource(R.drawable.shower_rain);
        }else if (icon.equals("10d") || icon.equals("10n") ){
            viewHolder.icon.setImageResource(R.drawable.rain);
        }else if (icon.equals("13d") || icon.equals("13n") ){
            viewHolder.icon.setImageResource(R.drawable.extreme_rain);
        }else if (icon.equals("11d") || icon.equals("11n") ){
            viewHolder.icon.setImageResource(R.drawable.extreme_rain);
        }
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    private class HourlyForecastViewHolder extends RecyclerView.ViewHolder {

        TextView time,temp,status,date;
        ImageView icon;

        public HourlyForecastViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            temp = itemView.findViewById(R.id.temp);
            status = itemView.findViewById(R.id.status);
            icon = itemView.findViewById(R.id.icon);
            date = itemView.findViewById(R.id.date);

        }
    }
}
