package com.example.shoplist2;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ProductDiffUtilCallback extends DiffUtil.Callback {

    private final List<Data> oldList;
    private final List<Data> newList;

    public ProductDiffUtilCallback(List<Data> oldList, List<Data> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Data oldProduct = oldList.get(oldItemPosition);
        Data newProduct = newList.get(newItemPosition);
        return oldProduct.data_id == newProduct.data_id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Data oldProduct = oldList.get(oldItemPosition);
        Data newProduct = newList.get(newItemPosition);
        return oldProduct.data_id == newProduct.data_id;
             //   && oldProduct.getPrice() == newProduct.getPrice();
    }
}
