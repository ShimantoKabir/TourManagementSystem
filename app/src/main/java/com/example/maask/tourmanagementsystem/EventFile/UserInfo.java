package com.example.maask.tourmanagementsystem.EventFile;

/**
 * Created by Maask on 2/1/2018.
 */

public class UserInfo {

    private String userName;
    private String userPho;
    private String imgUrl;

    public UserInfo() {}

    public UserInfo(String userName, String userPho, String imgUrl) {
        this.userName = userName;
        this.userPho = userPho;
        this.imgUrl = imgUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPho() {
        return userPho;
    }

    public void setUserPho(String userPho) {
        this.userPho = userPho;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
