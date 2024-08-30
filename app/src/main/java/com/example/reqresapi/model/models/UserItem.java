package com.example.reqresapi.model.models;

/**
 * Represents the data model used in the RecyclerView for displaying user information.
 * This model holds user details such as ID, email, first name, last name, and avatar.
 */
public class UserItem {

    // Represents the data model used in the RecyclerView for displaying user information.

    private int id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;  // Add avatar URL as a String

    public UserItem(int id, String email, String first_name, String last_name, String avatar) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.avatar = avatar;  // Initialize avatar
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}