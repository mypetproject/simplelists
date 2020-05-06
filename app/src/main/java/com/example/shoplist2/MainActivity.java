package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    List<String> data;
    MyRecyclerViewAdapter adapter;
    static int crossOutNumber;
    static int editButtonClicked = 1;
    boolean deleteFlag;


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
        data.add("Bread");

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
        //adapter.setCrossOutNumber(crossOutNumber);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                if (position_dragged >= (data.size()-crossOutNumber) || position_dragged == 0) {
                    position_target = position_dragged;
                } else if (position_target == 0) {
                    position_target = 1;
                } else if (position_target >= (data.size()-crossOutNumber)) {
                    position_target = data.size()-crossOutNumber-1;
                }
                Collections.swap(data,position_dragged,position_target);

                adapter.notifyItemMoved(position_dragged,position_target);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);
// Do delete buttons invisible and hide holder "dobavit'"
Button mEditButton = (Button) findViewById(R.id.edit_button);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               switch (editButtonClicked) {
                   case 1: editButtonClicked = 0; break;
                   default: editButtonClicked = 1;
               }
                // Toast.makeText(v.getContext(), "You clicked edit button", Toast.LENGTH_SHORT).show();
               adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(View view, final int position) {
        // Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        if (position == 0) {
            onButtonShowPopupWindowClick(view, 1, position);
        } else if (editButtonClicked == 1){
            if (position < (data.size()-crossOutNumber)) {
                crossOutNumber++;
                moveSingleItem(position);
            } else {
                crossOutNumber--;
                moveSingleItemToTop(position);
            }
        } else {
            deleteFlag = true;
            onButtonShowPopupWindowClick(view, position, position);

            //removeSingleItem(position);
        }
     }


    public void onItemDeleteClick(View view, int position) {

    }


    public void onButtonShowPopupWindowClick(View view, final int insertIndex, final int position) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        Button btnOk = popupView.findViewById(R.id.popup_ok_button);
        Button btnCancel = popupView.findViewById(R.id.popup_cancel_button);
        Button btnNext = popupView.findViewById(R.id.popup_next_button);
        final EditText et = popupView.findViewById(R.id.popup_edit);

        if (position != 0) {
            TextView text = view.findViewById(R.id.tvAnimalName);
            et.setText(text.getText().toString() + " ");
            et.setSelection(et.length());

        } else {
            deleteFlag = false;
        }



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (!str.isEmpty()) {
                    if (deleteFlag) {
                        deleteFlag = false;
                        removeSingleItem(position);
                    }
                    insertFromPopup(str, insertIndex);
                    popupWindow.dismiss();
                }
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFlag = false;
                popupWindow.dismiss();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (!str.isEmpty()) {
                    if (deleteFlag) {
                        deleteFlag = false;
                        removeSingleItem(position);
                    }
                    insertFromPopup(str, insertIndex);
                    et.getText().clear();
                }
            }

        });
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus && et.isEnabled() && et.isFocusable()) {
                    et.post(new Runnable() {
                        @Override
                        public void run() {
                            final InputMethodManager imm =(InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et,InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });

        //InputMethodManager imm =  (InputMethodManager) getSystemService(popupView.getContext().INPUT_METHOD_SERVICE);
        //imm.showSoftInput(popupView, InputMethodManager.SHOW_IMPLICIT);

        et.requestFocus();
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String str = et.getText().toString();
                    if (!str.isEmpty()) {
                        if (deleteFlag) {
                            deleteFlag = false;
                            removeSingleItem(position);
                        }
                        insertFromPopup(str, insertIndex);
                        popupWindow.dismiss();
                    }

                    handled = true;
                }
                return handled;

            }
        });

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
        int toPosition = data.size() - 1;

        // update data array
        String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

        // notify adapter
        adapter.notifyItemMoved(fromPosition, toPosition);
        adapter.notifyItemChanged(toPosition);
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
        adapter.notifyItemChanged(toPosition);
    }
   /* public void setCrossOutNumberInActivity(int mCrossOutNumber) {
        this.crossOutNumber = mCrossOutNumber;
    }*/

}

