package com.appsverse.SimpleLists;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "data_table",
        foreignKeys = @ForeignKey(entity = DepartmentData.class, parentColumns = "department_id", childColumns = "department_id", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"department_id","data_name"}, unique = false)})
public class Data {

    @PrimaryKey(autoGenerate = true)
    //@NonNull
    public int data_id;

    //@ForeignKey()
    public int department_id;

    public int data_position;

    public String data_name;

    public float data_qty;

    Data(int department_id, int data_position, String data_name, float data_qty) {
        this.department_id = department_id;
        this.data_position = data_position;
        this.data_name = data_name;
        this.data_qty = data_qty;
    }
}
