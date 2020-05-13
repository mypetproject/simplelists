package com.example.shoplist2;

import java.util.ArrayList;


public final class DataWithCrossOutNumber {
    private ArrayList<String> data;
    private int crossOutNumber;
    public DataWithCrossOutNumber(ArrayList<String> data, int crossOutNumber) { this.data = data; this.crossOutNumber = crossOutNumber;}

    public ArrayList<String> getData() {
        return this.data;
    }

    public int getCrossOutNumber() {
        return this.crossOutNumber;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public void setCrossOutNumber(int crossOutNumber) {
        this.crossOutNumber = crossOutNumber;
    }
}
