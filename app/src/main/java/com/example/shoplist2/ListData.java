package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import io.reactivex.Single;

@Entity(tableName = "lists_table", indices = {@Index(value = {"list_name"}, unique = true)})

public class ListData {

        @PrimaryKey(autoGenerate = true)
        @NonNull

        public int list_id;

        public int list_position;


        private String list_name;

        @NonNull
        public String getList_name() {
                return list_name;
        }

        public void setList_name(@NonNull String list_name) {
                this.list_name = list_name;
        }

        public String getAllInString() {
                String s = "list id: " + list_id + " list position: " + list_position + " list name: " + list_name;
                return s;
        }

}
