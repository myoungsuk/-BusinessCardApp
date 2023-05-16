package com.example.businesscarapp.activity;

public class ItemModel {
    private int image;
    private String name, age, area;

    public ItemModel() {
    }

    public ItemModel(int image, String name, String age, String area) {
        this.image = image;
        this.name = name;
        this.age = age;
        this.area = area;
    }

    public int getImage() {
        return image;
    }

    public String getNama() {
        return name;
    }

    public String getUsia() {
        return age;
    }

    public String getKota() {
        return area;
    }
}
