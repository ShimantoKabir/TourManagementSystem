package com.example.maask.tourmanagementsystem.WeatherFile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.maask.tourmanagementsystem.ForecastFragment;
import com.example.maask.tourmanagementsystem.WeatherFragment;

/**
 * Created by Maask on 1/27/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                WeatherFragment weatherFragment = new WeatherFragment();
                return weatherFragment;
            case 1:
                ForecastFragment forecastFragment = new ForecastFragment();
                return forecastFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}
