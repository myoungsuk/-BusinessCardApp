package com.example.businesscarapp.activity;

public class ItemModel {
    private int image;
    private String name, age, area, univ, major, skill;

    public ItemModel() {
    }

    public ItemModel(int image, String name, String age, String area, String univ, String major,
                     String skill)
    {
        this.image = image;
        this.name = name;
        this.age = age;
        this.area = area;
        this.univ = univ;
        this.major = major;
        this.skill = skill;
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
    public String getuniv() {
        return univ;
    }

    public String getmajor() {
        return major;
    }

    public String getSkill() {
        return skill;
    }
}
