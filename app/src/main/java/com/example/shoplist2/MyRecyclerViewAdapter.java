package com.example.shoplist2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Data> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private static final String TAG = "myLogs";

    MyRecyclerViewAdapter(Context context, List<Data> data) {
        Log.d(TAG, "MyRecyclerViewAdapter constructor started");
        
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_layout, parent, false);
        return new ViewHolder(view, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder started");

        MainActivity.canUpdate = false;

        setHolderButtonsVisibility(holder, position);
        setStrikeoutText(holder, position);
        setHolderItemsNames(holder, position);

        holder.myCustomEditTextListener.updateID(mData.get(holder.getAdapterPosition()).data_id);

        MainActivity.canUpdate = true;
    }

    private void setHolderItemsNames(ViewHolder holder, int position) {
        Log.d(TAG, "setHolderItemsNames started");

        Float data_qty_float = mData.get(position).data_qty;;
        String data_name = mData.get(position).data_name;
        String data_qty = data_qty_float.toString().replaceAll("\\.?0*$", "");

        holder.myTextView.setText(data_name);
        holder.mCount.setText(data_qty);
        holder.mEditQty.setText(data_qty);

        if (data_qty.equals("0") && position != 0 && MainActivity.editButtonClicked) {
            holder.mCount.setVisibility(View.INVISIBLE);
        } else if (data_qty.equals("0") && position != 0 && position >= (mData.size() - MainActivity.db.departmentDataDao().getDepartmentDataById(mData.get(position).department_id).CrossOutNumber)) {
            holder.mCount.setVisibility(View.INVISIBLE);
        }
    }

    private void setStrikeoutText(ViewHolder holder, int position) {
        Log.d(TAG, "setStrikethroughText started");

        if (position >= (mData.size() - MainActivity.db.departmentDataDao().getDepartmentDataById(mData.get(position).department_id).CrossOutNumber)) {
            holder.myTextView.setPaintFlags(holder.myTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.myTextView.setTextColor(Color.parseColor("#808080"));
            holder.mCount.setPaintFlags(holder.myTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mCount.setTextColor(Color.parseColor("#808080"));
        } else {
            holder.myTextView.setPaintFlags(holder.myTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.myTextView.setTextColor(ContextCompat.getColor(holder.myTextView.getContext(), R.color.image_btn));
            holder.mCount.setPaintFlags(holder.myTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.mCount.setTextColor(ContextCompat.getColor(holder.mCount.getContext(), R.color.image_btn));
        }
    }

    private void setHolderButtonsVisibility(ViewHolder holder, int position) {
        Log.d(TAG, "setHolderButtonsVisibility started");

        if (MainActivity.editButtonClicked && position == 0) {
            holder.myTextView.setVisibility(View.GONE);
            holder.mMoreImage.setVisibility(View.GONE);
            holder.mHighImage.setVisibility(View.GONE);
            holder.mCount.setVisibility(View.GONE);
            holder.mEditQty.setVisibility(View.GONE);
            holder.mLowImage.setVisibility(View.GONE);
        } else if (MainActivity.editButtonClicked) {
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
        } else if (!MainActivity.editButtonClicked
                && position >= (mData.size()
                - MainActivity.db.departmentDataDao().getDepartmentDataById(mData.get(position).department_id).CrossOutNumber)) {
            holder.mMoreImage.setVisibility(View.VISIBLE);
            holder.mLowImage.setVisibility(View.GONE);
            holder.mHighImage.setVisibility(View.GONE);
            holder.myTextView.setVisibility(View.VISIBLE);
            holder.mEditQty.setVisibility(View.GONE);
            holder.mCount.setVisibility(View.VISIBLE);
        } else if (!MainActivity.editButtonClicked) {
            holder.myTextView.setVisibility(View.VISIBLE);
            holder.mMoreImage.setVisibility(View.VISIBLE);
            holder.mLowImage.setVisibility(View.VISIBLE);
            holder.mHighImage.setVisibility(View.VISIBLE);
            holder.mCount.setVisibility(View.GONE);
            holder.mEditQty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        TextView myTextView;
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
                    }
                }
            });

            mMoreImage.setOnClickListener(this);
            mLowImage.setOnClickListener(this);
            mLowImage.setOnTouchListener(this);
            mHighImage.setOnClickListener(this);
            mHighImage.setOnTouchListener(this);
            mCount.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            if (mClickListener != null) {

                //todo зачем нужна эта строка?
                //MainActivity.setAdapterPosition(getLayoutPosition());

                mClickListener.onItemClick(view, getAdapterPosition(), mData.get(getAdapterPosition()).data_id);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mClickListener.onItemTouch(v, event, mData.get(getAdapterPosition()).data_id);
            return true;
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int dataID;

        public void updateID(int id) {
            this.dataID = id;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //todo округлять до третьего знака после запятой
            Log.d(TAG, "afterTextChanged started editable: " + editable.toString());
            if (!editable.toString().equals("") && !editable.toString().equals("-")) {

                if (MainActivity.canUpdate
                        && Float.parseFloat(editable.toString())
                        != MainActivity.db.dataDao()
                        .getChosenDataById(dataID).data_qty) {

                    MainActivity.db.dataDao().updateQty(dataID, Float.parseFloat(editable.toString()));
                }
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).data_name;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int id, int position);

        void onItemTouch(View view, MotionEvent event, int id);
    }
}

