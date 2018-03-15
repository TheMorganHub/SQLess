package com.sqless.userdata;

public class User {

    private String username;
    private String token;

    public User(String username, String token) {
        this.username = username;
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return username + ": " + token;
    }
}
