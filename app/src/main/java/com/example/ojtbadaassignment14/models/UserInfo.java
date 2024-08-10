package com.example.ojtbadaassignment14.models;

public class UserInfo {
    private String uid;
    private String email;
    private String yourName;
    private String photo;
    private String token;

    public UserInfo() {
    }

    public UserInfo(String uid, String email, String yourName, String photo, String token) {
        this.uid = uid;
        this.email = email;
        this.yourName = yourName;
        this.photo = photo;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYourName() {
        return yourName;
    }

    public void setYourName(String yourName) {
        this.yourName = yourName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
