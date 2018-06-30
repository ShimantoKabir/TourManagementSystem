package com.example.maask.tourmanagementsystem.LikelihoodFile;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Maask on 1/30/2018.
 */

public class StoreLikelihoodPlaces {


    private String placeName;
    private String placeAddress;
    private String placePhone;
    private LatLng placeLatLng;
    private float rating;

    public StoreLikelihoodPlaces(String placeName, String placeAddress, String placePhone, LatLng placeLatLng, float rating) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placePhone = placePhone;
        this.placeLatLng = placeLatLng;
        this.rating = rating;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlacePhone() {
        return placePhone;
    }

    public void setPlacePhone(String placePhone) {
        this.placePhone = placePhone;
    }

    public LatLng getPlaceLatLng() {
        return placeLatLng;
    }

    public void setPlaceLatLng(LatLng placeLatLng) {
        this.placeLatLng = placeLatLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}