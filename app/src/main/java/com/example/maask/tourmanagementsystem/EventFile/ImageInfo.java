package com.example.maask.tourmanagementsystem.EventFile;

/**
 * Created by Maask on 2/9/2018.
 */

public class ImageInfo {

    String imageUrl;
    String captureDate;

    public ImageInfo() {}

    public ImageInfo(String imageUrl, String captureDate) {
        this.imageUrl = imageUrl;
        this.captureDate = captureDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }

}
