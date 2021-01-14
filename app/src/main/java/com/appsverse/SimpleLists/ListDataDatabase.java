package com.appsverse.SimpleLists;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


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
