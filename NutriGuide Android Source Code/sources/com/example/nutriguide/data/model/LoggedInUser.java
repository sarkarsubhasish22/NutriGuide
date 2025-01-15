package com.example.nutriguide.data.model;

public class LoggedInUser {
    private String displayName;
    private String userId;

    public LoggedInUser(String userId2, String displayName2) {
        this.userId = userId2;
        this.displayName = displayName2;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
