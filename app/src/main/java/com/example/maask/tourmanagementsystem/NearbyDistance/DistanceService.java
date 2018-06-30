package com.example.maask.tourmanagementsystem.NearbyDistance;

import com.example.maask.tourmanagementsystem.NearbyFile.NearbyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Maask on 1/27/2018.
 */

public interface DistanceService {
    @GET
    Call<DistanceResponse> getDistancData(@Url String urlString);
}
