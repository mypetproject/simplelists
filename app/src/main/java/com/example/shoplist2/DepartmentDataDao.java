package com.example.shoplist2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DepartmentDataDao {

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(DepartmentData departmentData);

        @Query("SELECT * FROM departments_table WHERE list_id = :list_id ORDER BY department_position ASC ")
        List<DepartmentData> getAll(int list_id);

        @Query("SELECT department_name FROM departments_table WHERE list_id = :list_id ORDER BY department_position ASC")
        List<String> getAllNames(int list_id);

        @Query("SELECT department_position FROM departments_table WHERE list_id = :list_id ORDER BY department_position ASC")
        List<Integer> getAllPositions(int list_id);

        @Query("Update departments_table Set department_position = department_position + 1 WHERE list_id = :list_id AND department_position > :position")
        void incrementValues(int list_id, int position);

        @Query("Update departments_table Set department_position = department_position - 1 WHERE list_id = :list_id AND department_position > :position")
        void decrementValues(int list_id, int position);

        @Query("Update departments_table Set department_position = 0 WHERE department_name = 'Добавить' AND list_id = :list_id")
        void setDobavitInZero(int list_id);

        @Query("SELECT * FROM departments_table WHERE department_position = :position AND list_id = :list_id")
        DepartmentData getChosenDepartment(int position, int list_id);

        @Query("DELETE FROM departments_table WHERE department_position = :position AND list_id = :list_id ")
        void deleteSingleData(int position, int list_id);

        @Update
        void update(DepartmentData departmentData);

        @Query("Update departments_table Set department_position = department_position + 1 WHERE list_id = :list_id " +
                "AND department_position < :fromPosition AND department_position >= :toPosition AND department_position > 0")
        void incrementValuesFromPositionToPosition(int list_id, int fromPosition, int toPosition);

        @Query("Update departments_table Set department_position = department_position - 1 WHERE list_id = :list_id " +
                "AND department_position > :fromPosition AND department_position <= :toPosition AND department_position > 0")
        void decrementValuesFromPositionToPosition(int list_id, int fromPosition, int toPosition);
/*
        @Query("DELETE FROM lists_table")
        int deleteAll();

        @Query("SELECT * FROM lists_table ORDER BY list_position ASC")
        Flowable<List<ListData>> getAll();

        @Query("SELECT * from lists_table ORDER BY list_name ASC")
        LiveData<List<ListData>> getAlphabetizedLists();

        @Query("Update lists_table Set list_position = list_position + 1")
        int incrementValues();

        @Query("Update lists_table Set list_position = 0 WHERE list_name = 'Добавить'")
        int setDobavitInZero();*/


}
