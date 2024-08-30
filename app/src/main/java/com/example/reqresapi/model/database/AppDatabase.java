package com.example.reqresapi.model.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.reqresapi.model.models.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // this class extends RoomDatabase and serves as the main access point to your Room database.
    public abstract UserDao userDao();
}
