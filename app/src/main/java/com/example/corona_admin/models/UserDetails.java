package com.example.corona_admin.models;

public class UserDetails {
    String name;
    String age;
    String gender;
    String blood;
    String phone;

    public UserDetails() {
    }

    public UserDetails(String name, String age, String gender, String blood, String phone) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.blood = blood;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
