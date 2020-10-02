package com.example.shoplist2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
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

@Database(entities = {ListData.class, DepartmentData.class, Data.class}, version = 3, exportSchema = false)
public abstract class ListDataDatabase extends RoomDatabase {
    public abstract ListDataDao listDataDao();
    public abstract DepartmentDataDao departmentDataDao();
    public abstract DataDao dataDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE data_table ADD COLUMN data_qty FLOAT DEFAULT 0 NOT NULL");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE departments_table ADD COLUMN visibility INTEGER DEFAULT 1 NOT NULL");
        }
    };
}
