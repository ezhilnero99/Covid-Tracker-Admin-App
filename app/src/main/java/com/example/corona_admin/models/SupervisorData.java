package com.example.corona_admin.models;

public class SupervisorData {
    String email;
    String desId;

    public SupervisorData() {
    }

    public SupervisorData(String email, String desId) {
        this.email = email;
        this.desId = desId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesId() {
        return desId;
    }

    public void setDesId(String desId) {
        this.desId = desId;
    }
}
