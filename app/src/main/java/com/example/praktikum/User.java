package com.example.praktikum;

public class User {

    private String username;
    private String email;
    private String country;
    private String userUri;
    private String id;

    public User() {
    }

    public User(String username, String email, String country, String userUri, String id) {
        this.username = username;
        this.email = email;
        this.country = country;
        this.userUri = userUri;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getUserUri() {
        return userUri;
    }

    public String getId() {
        return id;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUserUri(String userUri) {
        this.userUri = userUri;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", userUri='" + userUri + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
