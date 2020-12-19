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

        @Query("SELECT department_name FROM departments_table WHERE list_id = :list_id AND department_position > 0 ORDER BY department_position ASC")
        List<String> getAllNamesExceptFirst(int list_id);

        @Query("SELECT department_position FROM departments_table WHERE list_id = :list_id ORDER BY department_position ASC")
        List<Integer> getAllPositions(int list_id);

        @Query("Update departments_table Set department_position = department_position + 1 WHERE list_id = :list_id AND department_position >= :position")
        void incrementValues(int list_id, int position);

        @Query("Update departments_table Set department_position = department_position + 1 WHERE list_id = :list_id")
        void incrementAllValues(int list_id);

        @Query("Update departments_table Set department_position = department_position - 1 WHERE list_id = :list_id AND department_position > :position")
        void decrementValues(int list_id, int position);

        @Query("Update departments_table Set department_position = 0 WHERE department_name = 'Добавить' AND list_id = :list_id")
        void setDobavitInZero(int list_id);

        @Query("SELECT * FROM departments_table WHERE department_position = :position AND list_id = :list_id")
        DepartmentData getChosenDepartment(int position, int list_id);

        @Query("SELECT department_id FROM departments_table WHERE visibility == 1 AND list_id = :list_id ORDER BY department_position ASC")
        List<Integer> getAllVisibleDepartmentsID(int list_id);

        @Query("SELECT department_name FROM departments_table WHERE visibility == 1 AND list_id = :list_id ORDER BY department_position ASC")
        List<String> getAllVisibleDepartmentNames(int list_id);

        @Query("SELECT * FROM departments_table WHERE visibility == 1 AND list_id = :list_id ORDER BY department_position ASC")
        List<DepartmentData> getAllVisibleDepartmentData(int list_id);

        @Query("SELECT * FROM departments_table WHERE department_name = :name AND list_id = :list_id")
        DepartmentData getChosenDepartmentByName(String name, int list_id);

        @Query("SELECT * FROM departments_table WHERE department_id = :department_id")
        DepartmentData getDepartmentDataById(int department_id);

        @Query("DELETE FROM departments_table WHERE department_id = :id AND list_id = :list_id ")
        void deleteSingleData(int id, int list_id);

        @Update
        void update(DepartmentData departmentData);

        @Query("Update departments_table Set department_position = department_position + 1 WHERE list_id = :list_id " +
                "AND department_position < :fromPosition AND department_position >= :toPosition AND department_position > 0")
        void incrementValuesFromPositionToPosition(int list_id, int fromPosition, int toPosition);

        @Query("Update departments_table Set department_position = department_position - 1 WHERE list_id = :list_id " +
                "AND department_position > :fromPosition AND department_position <= :toPosition AND department_position > 0")
        void decrementValuesFromPositionToPosition(int list_id, int fromPosition, int toPosition);

}
