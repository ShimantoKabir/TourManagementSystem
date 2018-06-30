package com.example.maask.tourmanagementsystem.NearbyFile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Maask on 1/26/2018.
 */

public interface NearbyService{

    @GET
    Call<NearbyResponse> getNearbyPlace(@Url String urlString);

}
