package com.appsverse.SimpleLists;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Data data);

    @Query("SELECT * FROM data_table WHERE department_id = :department_id ORDER BY data_position ASC ")
    List<Data> getAll(int department_id);

    @Query("SELECT * FROM data_table WHERE data_position == 0")
    List<Data> getAllZerosElements();

    @Query("SELECT data_name FROM data_table WHERE department_id = :department_id ORDER BY data_position ASC")
    List<String> getAllNames(int department_id);

    @Query("SELECT data_qty FROM data_table WHERE department_id = :department_id ORDER BY data_position ASC")
    List<Float> getAllQty(int department_id);

    @Query("SELECT * FROM data_table WHERE department_id = :department_id AND data_position > 0 ORDER BY data_position ASC")
    List<Data> getAllForGenerator(int department_id);

    @Query("SELECT data_position FROM data_table WHERE department_id = :department_id ORDER BY data_position ASC")
    List<Integer> getAllPositions(int department_id);

    @Query("Update data_table Set data_position = data_position + 1 WHERE department_id = :department_id AND data_position > :position")
    void incrementValues(int department_id, int position);

    @Query("Update data_table Set data_position = data_position + 1 WHERE department_id = :department_id AND data_position < :position AND data_position > 0")
    void incrementValuesFromOneToPosition(int department_id, int position);

    @Query("Update data_table Set data_position = data_position + 1 WHERE department_id = :department_id " +
            "AND data_position < :fromPosition AND data_position >= :toPosition AND data_position > 0")
    void incrementValuesFromPositionToPosition(int department_id, int fromPosition, int toPosition);

    @Query("Update data_table Set data_position = data_position - 1 WHERE department_id = :department_id " +
            "AND data_position > :fromPosition AND data_position <= :toPosition AND data_position > 0")
    void decrementValuesFromPositionToPosition(int department_id, int fromPosition, int toPosition);

    @Query("Update data_table Set data_position = data_position - 1 WHERE department_id = :department_id AND data_position > :position")
    void decrementValues(int department_id, int position);

    @Update
    void update(Data data);

    @Query("Update data_table Set data_position = 0 WHERE data_name = 'Добавить' AND department_id = :department_id ")
    void setDobavitInZero(int department_id );

    @Query("Update data_table Set data_name = :name WHERE data_id = :data_id ")
    void updateSingleItem(int data_id, String name );

    @Query("Update data_table Set data_position = :position WHERE data_id = :data_id ")
    void updateSingleItemPosition(int data_id, int position);

    @Query("SELECT * FROM data_table WHERE data_position = :position AND department_id = :department_id")
    Data getChosenData(int position, int department_id);

    @Query("SELECT * FROM data_table WHERE data_id = :id")
    Data getChosenDataById(int id);

    @Query("SELECT department_id FROM data_table WHERE data_id = :id")
    Integer getDepartmentIdByDataId(int id);

    @Query("DELETE FROM data_table WHERE data_position = :position AND department_id = :department_id ")
    void deleteSingleData(int position, int department_id);

    @Query("DELETE FROM data_table WHERE data_id = :id")
    void deleteSingleDataById(int id);

    @Query("DELETE FROM data_table WHERE department_id = :id AND data_position > 0")
    void deleteAllDataByDepartmentID(int id);

    @Query("UPDATE data_table SET data_qty = data_qty - 1 WHERE data_id = :id")
    void minusQty(int id);

    @Query("UPDATE data_table SET data_qty = data_qty + 1 WHERE data_id = :id")
    void plusQty(int id);

    @Query("UPDATE data_table SET data_qty = :qty WHERE data_id = :id")
    void updateQty(int id, Float qty);

}