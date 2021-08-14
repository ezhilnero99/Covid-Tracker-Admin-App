package com.example.corona_admin.models;

public class UserLocationDetails {
    String name;
    String age;
    String gender;
    String blood;
    String phone;
    String asvEmail;
    String date;
    String time;
    String location;
    String temperature;

    public UserLocationDetails(String name, String age, String gender, String blood, String phone, String asvEmail, String date, String time, String location, String temperature) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.blood = blood;
        this.phone = phone;
        this.asvEmail = asvEmail;
        this.date = date;
        this.time = time;
        this.location = location;
        this.temperature = temperature;
    }

    public UserLocationDetails() {
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

    public String getAsvEmail() {
        return asvEmail;
    }

    public void setAsvEmail(String asvEmail) {
        this.asvEmail = asvEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
