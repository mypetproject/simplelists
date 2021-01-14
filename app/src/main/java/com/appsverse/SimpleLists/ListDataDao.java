package com.appsverse.SimpleLists;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ListDataDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ListData listData);

    @Query("DELETE FROM lists_table")
    int deleteAll();

    @Query("DELETE FROM lists_table WHERE list_id = :list_id")
    int deleteSingleItem(int list_id);

    @Query("DELETE FROM departments_table WHERE department_position = :position AND list_id = :list_id ")
    void deleteSingleData(int position, int list_id);

    @Query("SELECT * FROM lists_table WHERE list_position > 0 ORDER BY list_position ASC")
    List<ListData> getAll();

    @Query("SELECT list_name FROM lists_table ORDER BY list_position ASC")
    List<String> getAllNames();

    @Query("SELECT list_name FROM lists_table ORDER BY list_position ASC")
    List<String> getAllNamesNotFlowable();

    @Query("SELECT list_position FROM lists_table ORDER BY list_position ASC")
    List<Integer> getAllPositions();

    @Query("SELECT * FROM lists_table WHERE list_position = :position")
    ListData getChosenList(int position);

    @Query("SELECT * FROM lists_table WHERE list_name = :name")
    ListData getChosenListByName(String name);

    @Query("SELECT * FROM lists_table WHERE list_position = :position")
    ListData getChosenListByPosition(int position);

    @Query("SELECT * from lists_table ORDER BY list_name ASC")
    LiveData<List<ListData>> getAlphabetizedLists();

    @Query("Update lists_table Set list_position = list_position + 1 WHERE list_position > 0")
    int incrementValues();

        @Query("Update lists_table Set list_position = list_position - 1 WHERE list_position > 0 AND list_position > :list_position")
    int decrementValues(int list_position);

    @Query("Update lists_table Set list_position = 0 WHERE list_name = 'Добавить'")
    int setDobavitInZero();

    @Update
    void update(ListData listData);
}
