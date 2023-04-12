package com.example.businesscarapp.models;

public class Friend{
    public String name;
    public String email;
    public String profileImageUrl;
    public String uid;

    public Friend() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Friend(String name, String email, String profileImageUrl, String uid) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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


}
