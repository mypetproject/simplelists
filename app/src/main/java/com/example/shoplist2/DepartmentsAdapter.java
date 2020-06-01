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

public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // data is passed into the constructor
    DepartmentsAdapter(Context context, List<String> data) {
   // DepartmentsAdapter(Context context, HashMap<String, ArrayList<>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

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
      /*  View parent = (View) itemView.myTextView.getParent();
        if (parent.getId() == R.id.rvDepartments) {
            //Toast.makeText(holder.myTextView.getContext(), "" + parent.getId(), Toast.LENGTH_SHORT).show();
            tvDep.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }*/

        //String animal = mData.get(position);
        //Log.d("myLogs", "chosenDepartment: " + MainActivity.chosenDepartment + "X");
        //Remove delete button from position 0 and set visible on position > 0


        if (mData.size() > 1) if (animal.equals(MainActivity.chosenDepartmentData.department_name)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#00ff00"));
            Log.d("myLogs", "Set color for " + MainActivity.chosenDepartmentData.department_name);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#79bfea"));
        }
        Log.d("myLogs", "onBindViewHolder color chosen");
        if (position == 0) {
            holder.mDeleteImage.setVisibility(View.GONE);
            holder.mAddImage.setVisibility(View.VISIBLE);
        } else {
            holder.mDeleteImage.setVisibility(View.VISIBLE);
            holder.mAddImage.setVisibility(View.GONE);
        }


        if (MainActivity.editButtonClicked) {
            holder.mDeleteImage.setVisibility(View.INVISIBLE);
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
        //LinearLayout mLinearLayout;
        // String parent1;

        ViewHolder(View itemView) {
            super(itemView);
            //mLinearLayout = itemView.findViewById(R.id.ll);

            // mLinearLayout = itemView.findViewById(R.id.ll);

            myTextView = itemView.findViewById(R.id.tvDepartmentsName);
            mDeleteImage = itemView.findViewById(R.id.image_delete2);
            mAddImage = itemView.findViewById(R.id.addImage);

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
                mClickListener.onItemClick(view, getAdapterPosition());
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
        void onItemClick(View view, int position);
    }


}

