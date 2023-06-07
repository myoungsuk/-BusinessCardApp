package com.example.businesscarapp.models;

public class Friend{
    public String name;
    public String email;
    public String profileImageUrl;
    public String uid;
    public String studentId;
    public String school;
    public String department;
    public String description;
    public Friend() {
        this("", "", "", "", "", "", "", "");
    }

    public Friend(String name, String email, String profileImageUrl, String uid, String studentId, String school, String department, String description) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.uid = uid;
        this.studentId = studentId;
        this.school = school;
        this.department = department;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}