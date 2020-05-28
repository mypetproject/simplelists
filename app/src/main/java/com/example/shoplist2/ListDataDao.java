package com.example.shoplist2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ListDataDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ListData listData);

    @Query("DELETE FROM lists_table")
    int deleteAll();

    @Query("SELECT * FROM lists_table ORDER BY list_position ASC")
    Flowable<List<ListData>> getAll();

    @Query("SELECT list_name FROM lists_table ORDER BY list_position ASC")
    Flowable<List<String>> getAllNames();

    @Query("SELECT list_name FROM lists_table ORDER BY list_position ASC")
    List<String> getAllNamesNotFlowable();

    @Query("SELECT * FROM lists_table WHERE list_position = :position")
    ListData getChosenList(int position);

    @Query("SELECT * from lists_table ORDER BY list_name ASC")
    LiveData<List<ListData>> getAlphabetizedLists();

    @Query("Update lists_table Set list_position = list_position + 1 WHERE list_position > 0")
    int incrementValues();

    @Query("Update lists_table Set list_position = 0 WHERE list_name = 'Добавить'")
    int setDobavitInZero();
}
