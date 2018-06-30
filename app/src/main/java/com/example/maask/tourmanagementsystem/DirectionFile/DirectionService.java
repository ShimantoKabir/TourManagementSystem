package com.example.maask.tourmanagementsystem.DirectionFile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Maask on 1/30/2018.
 */

public interface DirectionService {
    @GET
    Call<DirectionResponse> getDirection(@Url String urlString);
}

