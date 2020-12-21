package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "departments_table",
        foreignKeys = @ForeignKey(entity = ListData.class, parentColumns = "list_id", childColumns = "list_id", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"department_name","list_id"}, unique = true)})
public class DepartmentData {

        @PrimaryKey(autoGenerate = true)
        //@NonNull
        public int department_id;

        //@ForeignKey()
        public int list_id;

        public int department_position;

        public String department_name;

        int CrossOutNumber;

        public int visibility = 0;

        DepartmentData(int list_id, int department_position, String department_name, int CrossOutNumber) {
                this.list_id = list_id;
                this.department_position = department_position;
                this.department_name = department_name;
                this.CrossOutNumber = CrossOutNumber;
        }
}
