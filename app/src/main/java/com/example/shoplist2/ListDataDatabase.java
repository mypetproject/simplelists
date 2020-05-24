package com.example.shoplist2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*@Database(entities = {ListData.class}, version = 1, exportSchema = false)
public abstract class ListDataDatabase extends RoomDatabase {

    public abstract ListDataDao listDataDao();

    private static volatile ListDataDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ListDataDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ListDataDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ListDataDatabase.class, "list_data_database.db")
                            // .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}*/

@Database(entities = {ListData.class, DepartmentData.class, Data.class}, version = 1, exportSchema = false)
public abstract class ListDataDatabase extends RoomDatabase {
    public abstract ListDataDao listDataDao();
    public abstract DepartmentDataDao departmentDataDao();
    public abstract DataDao dataDao();
}
