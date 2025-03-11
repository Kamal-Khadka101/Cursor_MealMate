package com.example.mealmate.models;

public class User {
    private String uid;
    private String email;
    private String name;
    private String phoneNumber;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    public User(String uid, String email, String name, String phoneNumber) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
} 