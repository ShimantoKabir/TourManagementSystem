package com.example.maask.tourmanagementsystem.WeatherFile;

/**
 * Created by Maask on 1/28/2018.
 */

import android.content.SearchRecentSuggestionsProvider;

public class CitySuggestions extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.example.maask.tourmanagementsystem.WeatherFile.CitySuggestions";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public CitySuggestions() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
