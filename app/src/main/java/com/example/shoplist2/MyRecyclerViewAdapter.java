package com.example.shoplist2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private List<Float> mDataQty;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    // ListDataDatabase db = App.getInstance().getDatabase();
    private static final String TAG = "myLogs";


    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> data, List<Float> data_qty) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDataQty = data_qty;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_layout, parent, false);
        return new ViewHolder(view, new MyCustomEditTextListener());
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MainActivity.canUpdate = false;
        Log.d(TAG, "onBindViewHolder mDataQty: " + mDataQty);
        String data_qty = "";
        String animal = mData.get(position);
        //todo сделать нормальное округление
        float remainder = mDataQty.get(position) - mDataQty.get(position).intValue();
        if (remainder == 0f) {
            data_qty = String.format("%.0f", mDataQty.get(position));
        } else {
            data_qty = mDataQty.get(position).toString();
        }

      /*  View parent = (View) itemView.myTextView.getParent();
        if (parent.getId() == R.id.rvDepartments) {
            //Toast.makeText(holder.myTextView.getContext(), "" + parent.getId(), Toast.LENGTH_SHORT).show();
            tvDep.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }*/

        //String animal = mData.get(position);

        //Remove delete button from position 0 and set visible on position > 0
       /* if (position == 0) {
            holder.mMoreImage.setVisibility(View.GONE);
            holder.mLowImage.setVisibility(View.GONE);
            holder.mHighImage.setVisibility(View.GONE);
            holder.mCount.setVisibility(View.GONE);
            holder.mEditQty.setVisibility(View.GONE);
            holder.myTextView.setVisibility(View.GONE);
        } else {
            holder.mMoreImage.setVisibility(View.VISIBLE);
            holder.myTextView.setVisibility(View.VISIBLE);
            holder.mLowImage.setVisibility(View.VISIBLE);
            holder.mHighImage.setVisibility(View.VISIBLE);
            holder.mCount.setVisibility(View.VISIBLE);

        }*/


        if (MainActivity.editButtonClicked && position == 0) {
            holder.myTextView.setVisibility(View.GONE);
            holder.mMoreImage.setVisibility(View.GONE);
            holder.mHighImage.setVisibility(View.GONE);
            holder.mCount.setVisibility(View.GONE);
            holder.mEditQty.setVisibility(View.GONE);
            holder.mLowImage.setVisibility(View.GONE);
        } else if (MainActivity.editButtonClicked || position >= (mData.size() - MainActivity.chosenDepartmentData.CrossOutNumber)) {
            holder.mMoreImage.setVisibility(View.GONE);
            holder.mLowImage.setVisibility(View.GONE);
            holder.mHighImage.setVisibility(View.GONE);
            holder.myTextView.setVisibility(View.VISIBLE);
            holder.mEditQty.setVisibility(View.GONE);
            holder.mCount.setVisibility(View.VISIBLE);
        } else if (position == 0) {
            holder.myTextView.setVisibility(View.VISIBLE);
            holder.mMoreImage.setVisibility(View.GONE);
            holder.mHighImage.setVisibility(View.GONE);
            holder.mCount.setVisibility(View.GONE);
            holder.mEditQty.setVisibility(View.GONE);
            holder.mLowImage.setVisibility(View.GONE);
        } else {
            holder.myTextView.setVisibility(View.VISIBLE);
            holder.mMoreImage.setVisibility(View.VISIBLE);
            holder.mLowImage.setVisibility(View.VISIBLE);
            holder.mHighImage.setVisibility(View.VISIBLE);
            holder.mCount.setVisibility(View.GONE);
            holder.mEditQty.setVisibility(View.VISIBLE);
        }


        if (position >= (mData.size() - MainActivity.chosenDepartmentData.CrossOutNumber)) {
            holder.myTextView.setPaintFlags(holder.myTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.myTextView.setTextColor(Color.parseColor("#808080"));
            holder.mCount.setPaintFlags(holder.myTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mCount.setTextColor(Color.parseColor("#808080"));
        } else {
            holder.myTextView.setPaintFlags(holder.myTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.myTextView.setTextColor(Color.parseColor("#000000"));
            holder.mCount.setPaintFlags(holder.myTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.mCount.setTextColor(Color.parseColor("#000000"));
        }

        holder.myTextView.setText(animal);
        holder.mCount.setText(data_qty);
        holder.mEditQty.setText(data_qty);
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        if (data_qty.equals("0") && position != 0 && MainActivity.editButtonClicked) {
            holder.mCount.setVisibility(View.INVISIBLE);
        } else if (data_qty.equals("0") && position != 0 && position >= (mData.size() - MainActivity.chosenDepartmentData.CrossOutNumber)) {
            holder.mCount.setVisibility(View.INVISIBLE);
        }
        MainActivity.canUpdate = true;

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        //ImageView mDeleteImage;
        ImageView mMoreImage;
        ImageView mLowImage;
        ImageView mHighImage;
        TextView mCount;
        EditText mEditQty;
        public MyCustomEditTextListener myCustomEditTextListener;

        ViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            mMoreImage = itemView.findViewById(R.id.image_more);
            mLowImage = itemView.findViewById(R.id.image_to_low);
            mHighImage = itemView.findViewById(R.id.image_to_high);
            mCount = itemView.findViewById(R.id.tvAnimalCount);

            mEditQty = itemView.findViewById(R.id.etAnimalCount);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.mEditQty.addTextChangedListener(myCustomEditTextListener);
            mEditQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                       // v.clearFocus();
                    }
                }
            });

            mMoreImage.setOnClickListener(this);
            mLowImage.setOnClickListener(this);
            mHighImage.setOnClickListener(this);
            mCount.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            if (mClickListener != null) {

                //todo зачем нужна эта строка?
                MainActivity.setAdapterPosition(getLayoutPosition());

                //Toast.makeText(view.getContext(), "Departments " + view.getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //todo округлять до третьего знака после запятой
            Log.d(TAG, "afterTextChanged started editable: " + editable.toString());
            // if (MainActivity.canUpdate) Log.d(TAG, "afterTextChanged editable: " + Float.parseFloat(editable.toString()) + " mDataQty: " + mDataQty.get(position) + " position: " + position);
            if (!editable.toString().equals("")) {
                Log.d(TAG, "afterTextChanged editable.toString() != \"\"" + editable.toString());
                if (MainActivity.canUpdate
                        && Float.parseFloat(editable.toString())
                        != MainActivity.db.dataDao()
                        .getChosenData(position, MainActivity.chosenDepartmentData.department_id).data_qty) {
                    MainActivity
                            .db.dataDao()
                            .updateQty(position, MainActivity.chosenDepartmentData.department_id, Float.parseFloat(editable.toString()));
                }
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

