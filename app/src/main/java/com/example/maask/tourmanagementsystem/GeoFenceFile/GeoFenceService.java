package com.example.maask.tourmanagementsystem.GeoFenceFile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Maask on 2/13/2018.
 */

public interface GeoFenceService {

    @GET
    Call<GeoFenceResponse> getAddressFromLatLng(@Url String urlString);

}
