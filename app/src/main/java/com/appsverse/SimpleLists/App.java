package com.appsverse.SimpleLists;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {

    public static App instance;

    private ListDataDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, ListDataDatabase.class, "lists_database")
                .addMigrations(ListDataDatabase.MIGRATION_1_2, ListDataDatabase.MIGRATION_2_3)
                .allowMainThreadQueries()
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public ListDataDatabase getDatabase() {
        return database;
    }
}
