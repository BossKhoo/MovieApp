package com.example.ojtbadaassignment14.models;

public class User {
    public String name;
    public String gender;
    public long dateOfBirth;
    public String photoBase64;

    public User() {
    }

    public User(String name, String gender, long dateOfBirth, String photoBase64) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.photoBase64 = photoBase64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }
}
