package com.example.shoplist2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

//TODO вроде бы не используется
public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int mDataSize;


    // data is passed into the constructor
    DepartmentsAdapter(Context context, List<String> data, int dataSize) {
   // DepartmentsAdapter(Context context, HashMap<String, ArrayList<>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
this.mDataSize = dataSize;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.departments_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
     String animal = mData.get(position);
        Log.d("myLogs", "holder name in department adapter: " + animal);
        //if (mData.size() > 1) if (animal.equals(MainActivity.chosenDepartmentData.department_name)) {
           holder.itemView.setPressed(true);
           // Log.d("myLogs", "Set color for " + MainActivity.chosenDepartmentData.department_name);
        //} else {
            holder.itemView.setPressed(false);
       // }
        Log.d("myLogs", "onBindViewHolder color chosen");

        if (position == 0) {
            holder.mDeleteImage.setVisibility(View.GONE);
            holder.mAddImage.setVisibility(View.VISIBLE);
            holder.mQty.setVisibility(View.GONE);
        } else {
            holder.mDeleteImage.setVisibility(View.VISIBLE);
            holder.mAddImage.setVisibility(View.GONE);
            holder.mQty.setVisibility(View.VISIBLE);
        }


        if (MainActivity.editButtonClicked) {
            holder.mDeleteImage.setVisibility(View.GONE);
            holder.mAddImage.setVisibility(View.GONE);
        }

        if (MainActivity.editButtonClicked && position == 0) {
            holder.myTextView.setVisibility(View.GONE);
            holder.mDeleteImage.setVisibility(View.GONE);
            //holder.mLinearLayout.setVisibility(View.GONE);
        } else {
            holder.myTextView.setVisibility(View.VISIBLE);

        }

        holder.myTextView.setText(animal);
        int activeQty = MainActivity.db.dataDao().getAllNames(
                MainActivity.db.departmentDataDao().getChosenDepartmentByName(animal,MainActivity.chosenListData.list_id).department_id)
                .size() -  MainActivity.db.departmentDataDao().getChosenDepartmentByName(animal,MainActivity.chosenListData.list_id).CrossOutNumber - 1;
        if (activeQty > 0) {
            holder.mQty.setText(activeQty + "");
        } else {
            holder.mQty.setVisibility(View.GONE);
            //LinearLayout.LayoutParams layoutParams =  holder.itemView.getLayoutParams();
        }

Log.d("myLogs","onBindViewHolder ended");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView mDeleteImage;
        ImageView mAddImage;
        TextView mQty;
        //LinearLayout mLinearLayout;
        // String parent1;

        ViewHolder(View itemView) {
            super(itemView);
            //mLinearLayout = itemView.findViewById(R.id.ll);

            // mLinearLayout = itemView.findViewById(R.id.ll);

            myTextView = itemView.findViewById(R.id.tvDepartmentsName);
            mDeleteImage = itemView.findViewById(R.id.image_delete2);
            mAddImage = itemView.findViewById(R.id.addImage);
            mQty = itemView.findViewById(R.id.tvDepartmentsQty);

            // parent1 = mLinearLayout.getParent().toString();

           /* mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                        // remove your item from data base
                        mData.remove(position);  // remove the item from list
                        notifyItemRemoved(position); // notify the adapter about the removed item

                }
            });*/

            mDeleteImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) {

                //зачем нужна эта строка?
                MainActivity.setAdapterPosition(getLayoutPosition());

                //Toast.makeText(view.getContext(), "Departments " + view.getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
                mClickListener.onItemClick(view, getAdapterPosition(), 0);
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, int id);
    }


}

