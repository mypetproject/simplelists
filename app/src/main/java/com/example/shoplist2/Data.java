package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "data_table",
        foreignKeys = @ForeignKey(entity = DepartmentData.class, parentColumns = "department_id", childColumns = "department_id", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"department_id","data_name"}, unique = false)})
public class Data {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int data_id;

    //@ForeignKey()
    public int department_id;

    public int data_position;

    public String data_name;

    public Data(int department_id, int data_position, String data_name) {
        this.department_id = department_id;
        this.data_position = data_position;
        this.data_name = data_name;
    }

    public Data() {

    }
    public String getAllInString() {
        String s = "department id: " + department_id + " data id: " + data_id + " data position: "
                + data_position + " data name: '" + data_name + "'";
        return s;
    }
}