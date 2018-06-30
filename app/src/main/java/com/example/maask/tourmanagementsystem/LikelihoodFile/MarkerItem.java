package com.example.maask.tourmanagementsystem.LikelihoodFile;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Maask on 1/30/2018.
 */

public class MarkerItem implements ClusterItem {
    private LatLng mLatLng;
    private String title;
    private String snippet;

    public MarkerItem(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    public MarkerItem(LatLng mLatLng, String title, String snippet) {
        this.mLatLng = mLatLng;
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}