package com.example.businesscarapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CardModel implements Parcelable {
    private String imageUrl;
    private String description;

    public CardModel(String imageUrl, String description) {
        this.imageUrl = imageUrl;
        this.description = description;
    }

    protected CardModel(Parcel in) {
        imageUrl = in.readString();
        description = in.readString();
    }

    public static final Creator<CardModel> CREATOR = new Creator<CardModel>() {
        @Override
        public CardModel createFromParcel(Parcel in) {
            return new CardModel(in);
        }

        @Override
        public CardModel[] newArray(int size) {
            return new CardModel[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(description);
    }
}