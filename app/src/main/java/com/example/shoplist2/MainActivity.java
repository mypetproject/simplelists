package com.example.shoplist2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    List<String> data;
    MyRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data to populate the RecyclerView with
        data = new ArrayList<>();
        data.add("Добавить");
        data.add("Cow");
        data.add("Camel");
        data.add("Sheep");
        data.add("Добавить");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MyRecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onItemClick(View view, final int position) {
        // Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        /*ImageView mDeleteImage = (ImageView) view.findViewById(R.id.image_delete);
        mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String theRemovedItem = mData.get(position);
                if (position == 0 || position == (data.size()-1) || data.size() < 3) {
                    /*String theRemovedItem = data.get(position);

                    data.remove(position);
                    adapter.notifyItemRemoved(position);*/
                  /*  removeSingleItem(position);

                }
            }
        });*/
        if (position == 0 || position == (data.size()-1)) {
            onButtonShowPopupWindowClick(view, 1);
        //} else if (position == (data.size()-1)) {
          //  onButtonShowPopupWindowClick(view, data.size()-1);
        } else {

// Как привязать text к arrayList, а не к получаемому view???
            TextView text = (TextView) view.findViewById(R.id.tvAnimalName);
            //TextView text = view.myTextView;
            //text.setTextSize(40.0f);
            if (text.getPaintFlags() != 1299) {
                moveSingleItem(position);
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                text.setTextColor(R.color.colorPrimaryDark);
                Toast.makeText(this, ""+text.getPaintFlags(), Toast.LENGTH_SHORT).show();

            } else {
                moveSingleItemToTop(position);
                text.setPaintFlags(text.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                text.setTextColor(Color.parseColor("#000000"));

            }
//text.setTextSize(30.0f);
            //text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            //MyRecyclerViewAdapter.setTextSizes(30);
            //text.setTypeface(null,4);
            //Toast.makeText(this, "You clicked " + adapter.getItem(position) +
            //        " on row number " + position + ". Size of ArrayList: " + data.size(), Toast.LENGTH_SHORT).show();
        }
        /*final int lastViewPosition = data.size()-1;
        switch (position) {
            case 0: onButtonShowPopupWindowClick(view, 1);
            break;
            case lastViewPosition: onButtonShowPopupWindowClick(view, lastViewPosition);
            break;
            default: Toast.makeText(this, "You clicked " + adapter.getItem(position) +
                    " on row number " + position, Toast.LENGTH_SHORT).show();
        }*/

    }


    public void onItemDeleteClick(View view, int position) {

    }


    public void onButtonShowPopupWindowClick(View view, final int insertIndex) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        Button btnOk = (Button) popupView.findViewById(R.id.popup_ok_button);
        Button btnCancel = (Button) popupView.findViewById(R.id.popup_cancel_button);
        Button btnNext = (Button) popupView.findViewById(R.id.popup_next_button);
        final EditText et = (EditText) popupView.findViewById(R.id.popup_edit);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (!str.isEmpty()) {
                    insertFromPopup(str, insertIndex);
                    popupWindow.dismiss();
                }
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                          popupWindow.dismiss();
                }
            });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (!str.isEmpty()) {
                    insertFromPopup(str, insertIndex);
                    et.getText().clear();
                }
            }

        });
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        // dismiss the popup window when touched
      /*  popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });*/


    }

    public void onButtonClick(View view) {
        insertSingleItem();
    }

    private void insertFromPopup(String s, int insertIndex) {
        //int insertIndex = 1;
        data.add(insertIndex, s);
        adapter.notifyItemInserted(insertIndex);
    }

    private void insertSingleItem() {
        String item = "Pig";
        int insertIndex = 2;
        data.add(insertIndex, item);
        adapter.notifyItemInserted(insertIndex);
    }

    private void insertMultipleItems() {
        ArrayList<String> items = new ArrayList<>();
        items.add("Pig");
        items.add("Chicken");
        items.add("Dog");
        int insertIndex = 2;
        data.addAll(insertIndex, items);
        adapter.notifyItemRangeInserted(insertIndex, items.size());
    }



    private void removeSingleItem(int removeIndex) {
        // int removeIndex = 2;
        data.remove(removeIndex);
        adapter.notifyItemRemoved(removeIndex);
    }

    private void removeMultipleItems() {
        int startIndex = 2; // inclusive
        int endIndex = 4;   // exclusive
        int count = endIndex - startIndex; // 2 items will be removed
        data.subList(startIndex, endIndex).clear();
        adapter.notifyItemRangeRemoved(startIndex, count);
    }

    private void removeAllItems() {
        data.clear();
        adapter.notifyDataSetChanged();
    }

    private void replaceOldListWithNewList() {
        // clear old list
        data.clear();

        // add new list
        ArrayList<String> newList = new ArrayList<>();
        newList.add("Lion");
        newList.add("Wolf");
        newList.add("Bear");
        data.addAll(newList);

        // notify adapter
        adapter.notifyDataSetChanged();
    }

    private void updateSingleItem() {
        String newValue = "I like sheep.";
        int updateIndex = 3;
        data.set(updateIndex, newValue);
        adapter.notifyItemChanged(updateIndex);
    }

    private void moveSingleItem(int fromPosition) {
       // int fromPosition = 3;
        int toPosition = data.size()-2;

        // update data array
        String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

        // notify adapter
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    private void moveSingleItemToTop(int fromPosition) {
        // int fromPosition = 3;
        int toPosition = 1;

        // update data array
        String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

        // notify adapter
        adapter.notifyItemMoved(fromPosition, toPosition);
    }
}

