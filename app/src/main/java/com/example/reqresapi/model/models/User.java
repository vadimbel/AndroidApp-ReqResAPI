package com.example.reqresapi.model.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents the User entity in the Room database, mapping to the "users" table.
 * This class is used to define the structure of the User table in the database.
 */

@Entity(tableName = "users")
public class User {

    // Represents the User entity in the Room database, mapping to the users table.

    @PrimaryKey
    private int id;

    private String email;
    private String first_name;
    private String last_name;
    private String avatar;

    public User(int id, String email, String first_name, String last_name, String avatar) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.avatar = avatar;
    }

    // Getters and Setters
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
