package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, DepartmentsAdapter.ItemClickListener {

    List<String> data;
    MyRecyclerViewAdapter adapter;
    static int crossOutNumber;
    static int editButtonClicked = 1;
    boolean deleteFlagForEdit;
    static int adapterPosition;
    String chosenDepartment;

   // List<String> departmentsData;
  // List<String, List<String>> departmentsData;
   public Map<String, List<String>> departmentsData;
    DepartmentsAdapter adapterForDepartments;
    List<String> keysForDepartments;
    Map<String, Integer> crossOutNumbersArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data to populate the RecyclerView with

        List<String> dataForMap = new ArrayList<>();
        //dataForMap.add("Добавить");
        dataForMap.add("Cow");
        dataForMap.add("Camel");
        dataForMap.add("Sheep");
        dataForMap.add("Bread");


        departmentsData = new HashMap<String, List<String>>();
        departmentsData.put("Добавить", null);
        departmentsData.put("Butcher's", dataForMap);
        departmentsData.put("Confectioner's", null);
        departmentsData.put("Stationer's", null);
        departmentsData.put("Greengrocer's", null);


        keysForDepartments = new ArrayList<>();
        keysForDepartments.add("Добавить");
        keysForDepartments.add("Butcher's");
        keysForDepartments.add("Confectioner's");
        keysForDepartments.add("Stationer's");
        keysForDepartments.add("Greengrocer's");
        data = new ArrayList<>();
        data.add("Добавить");

        crossOutNumbersArray = new HashMap<String, Integer>();
        for (String key : keysForDepartments) {
            crossOutNumbersArray.put(key,0);
        }

        // set up the RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MyRecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        RecyclerView recyclerViewDepartments = findViewById(R.id.rvDepartments);
        LinearLayoutManager layoutManagerDepartments = new LinearLayoutManager(this);
        recyclerViewDepartments.setLayoutManager(layoutManagerDepartments);

       /* DividerItemDecoration dividerItemDecorationDepartments = new DividerItemDecoration(recyclerViewDepartments.getContext(),
                layoutManagerDepartments.HORIZONTAL);
        DividerItemDecoration dividerItemDecorationDepartments2 = new DividerItemDecoration(recyclerViewDepartments.getContext(),
                layoutManagerDepartments.VERTICAL);
        //RecyclerView.ItemDecoration dividerItemDecorationDepartments =
        //        new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);

        recyclerViewDepartments.addItemDecoration(dividerItemDecorationDepartments);
        recyclerViewDepartments.addItemDecoration(dividerItemDecorationDepartments2);*/
        adapterForDepartments = new DepartmentsAdapter(this, keysForDepartments);
        adapterForDepartments.setClickListener(this);
        recyclerViewDepartments.setAdapter(adapterForDepartments);
        layoutManagerDepartments.setOrientation(LinearLayoutManager.HORIZONTAL);

        //recyclerView.setNestedScrollingEnabled(false);
        //recyclerViewDepartments.setNestedScrollingEnabled(false);


        //Animation for drag & drop for list
        //TODO: Переделать свайп лево-право на переключение списков
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.
                SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.END ) {



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
                //adapterForDepartments.notifyItemMoved(position_dragged,position_target);

                return false;
            }





            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                /*int position = viewHolder.getAdapterPosition();
                if (position != 0)            {
                    deleteSingleItem(position);
                }*/
                chosenDepartment = keysForDepartments.get(keysForDepartments.indexOf(chosenDepartment)+1);
                Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                setData();
                //adapter.notifyItemChanged(position);
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView,@NonNull RecyclerView.ViewHolder viewHolder) {

                if (viewHolder.getAdapterPosition() == 0) {
                    // no swipe for header
                    return 0;
                }
                // default swipe for all other items
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        });
        helper.attachToRecyclerView(recyclerView);

        //Animation for drag & drop for departments list
        ItemTouchHelper helper2 = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerViewDepartments, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

               if (position_dragged == 0) {
                   position_target = position_dragged;
               } else if (position_target == 0) {
                    position_target = 1;
                }
                Collections.swap(keysForDepartments,position_dragged,position_target);

                adapterForDepartments.notifyItemMoved(position_dragged,position_target);


                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper2.attachToRecyclerView(recyclerViewDepartments);

       /* ItemTouchHelper helper3 = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                /*switch (direction) {
                    case ItemTouchHelper.LEFT:
                        chosenDepartment = keysForDepartments.get(keysForDepartments.indexOf(chosenDepartment)-1);
                        Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                        setData();
                        break;

                    case ItemTouchHelper.RIGHT:
                        chosenDepartment = keysForDepartments.get(keysForDepartments.indexOf(chosenDepartment)+1);
                        Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                        setData();
                        break;

                }
                /*if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    //Toast.makeText(getBaseContext(), "swiped " + direction, Toast.LENGTH_SHORT).show();
                }*/
           /* }
        });
        helper3.attachToRecyclerView(recyclerView);*/


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
                adapterForDepartments.notifyDataSetChanged();
            }
        });

    //data.addListener(new List<String>())

    }

    public static void setAdapterPosition(int pos) {
        adapterPosition = pos;
    }

    public void setData() {
        //TextView text = view.findViewById(R.id.tvDepartmentsName);
        List<String> listToAdd = departmentsData.get(chosenDepartment);


        data.clear();
        //Toast.makeText(view.getContext(), "" + listToAdd, Toast.LENGTH_LONG).show();
        data.add("Добавить");

        if (listToAdd != null) {data.addAll(listToAdd);
        }
        adapter.notifyDataSetChanged();
    }

    void getCrossOutNumber(String key) {
        crossOutNumber = crossOutNumbersArray.get(key);
    }

    void setCrossOutNumber(String key) {
        crossOutNumbersArray.put(key,crossOutNumber);
    }


    @Override
    public void onItemClick(View view, final int position) {
         //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        //String resName = (String) view.getResources().getResourceName(position);
        View parent = (View) view.getParent();
        //Toast.makeText(this, "" + departmentsData.keySet(),Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "Departments " + view.getParent().toString(), Toast.LENGTH_SHORT).show();
       //Toast.makeText(this,  "vv: "+ parent.getId(), Toast.LENGTH_SHORT).show();
        /*int viewId = view.getId();*/
        /*switch(parent.getId()) {
            case R.id.rvDepartments:
                //Toast.makeText(this, "Departments", Toast.LENGTH_SHORT).show();
                TextView text = view.findViewById(R.id.tvDepartmentsName);
                //Toast.makeText(view.getContext(), "Dobavit'" + text.getText().toString(), Toast.LENGTH_SHORT).show();
                if (text.getText().toString() == "Добавить" && position == 0) {
                    //onButtonShowPopupWindowClick(view, 1, position);
                    Toast.makeText(view.getContext(), "Dobavit'", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rvAnimals:*/
        int parentID = parent.getId();

        //ImageView mDeleteImage = view.findViewById(R.id.image_delete);

        //Toast.makeText(this, "" + parentID + " " + R.id.image_delete, Toast.LENGTH_SHORT).show();
        switch (parentID) {
            case R.id.ll:
                //Toast.makeText(this, " DELETE " + parentID, Toast.LENGTH_SHORT).show();
                deleteSingleItem(position);
                break;
            case R.id.depLl:
                deleteSingleItemInDepartments(view, position);
                break;
                    case R.id.rvDepartments:
                        if (position == 0) {
                            onButtonShowPopupWindowClick(view, 1, position, parentID);
                        } else /*if (editButtonClicked == 0) */{

                            TextView text = view.findViewById(R.id.tvDepartmentsName);
                            chosenDepartment = text.getText().toString();
                            setData();
                            getCrossOutNumber(chosenDepartment);
                           // deleteFlagForEdit = true;
                           // onButtonShowPopupWindowClick(view, position, position, parentID);
                        }/* else if (editButtonClicked == 1) {
                            setData(view, position);
                            TextView text = view.findViewById(R.id.tvDepartmentsName);
                            chosenDepartment = text.getText().toString();
                            getCrossOutNumber(chosenDepartment);
                        }*/
                        break;
                    case R.id.rvAnimals:
                        if (position == 0) {
                            onButtonShowPopupWindowClick(view, 1, position, parentID);
                        } else if (editButtonClicked == 1) {


                            if (position < (data.size() - crossOutNumber)) {
                                crossOutNumber++;
                                setCrossOutNumber(chosenDepartment);
                                moveSingleItem(position);
                                saveDataWhenItChanged();
                            } else {
                                crossOutNumber--;
                                setCrossOutNumber(chosenDepartment);
                                moveSingleItemToTop(position);
                                saveDataWhenItChanged();
                            }
                        } else {
                            deleteFlagForEdit = true;
                            onButtonShowPopupWindowClick(view, position, position, parentID);

                        }
                        break;

        }

        }

    public void onButtonShowPopupWindowClick(final View view, final int insertIndex, final int position, final int parentID) {

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
        //TextView text;
        if (position != 0) {
            TextView  text = new TextView(view.getContext());
            switch (parentID) {
                case R.id.rvAnimals:
               text = view.findViewById(R.id.tvAnimalName);
                break;
                case  R.id.rvDepartments:
                 text = view.findViewById(R.id.tvDepartmentsName);
                    break;
            }
            et.setText(text.getText().toString() + " ");
            et.setSelection(et.length());

        } else {
            deleteFlagForEdit = false;
        }




        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String str = et.getText().toString();
               // if (!str.isEmpty()) {
                String str = et.getText().toString();
                    if ((!departmentsData.containsKey(str) && parentID == R.id.rvDepartments) || parentID == R.id.rvAnimals) {
                        buttonClicked(str);
                    }else {
                        Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                    }
                }
            void buttonClicked(String str){

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        removeSingleItem(position, parentID);
                    }
                    insertFromPopup(str, insertIndex, parentID);
                    popupWindow.dismiss();
                    if (parentID == R.id.rvAnimals) {
                        saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {
                        setData();
                        chosenDepartment = str;
                    }
                }
            }
                        // }

        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFlagForEdit = false;
                popupWindow.dismiss();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            /*public void onClick(View v) {
                String str = et.getText().toString();
                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        removeSingleItem(position, parentID);
                    }
                    insertFromPopup(str, insertIndex, parentID);
                    et.getText().clear();
                    if (parentID == R.id.rvAnimals) {
                        saveDataWhenItChanged();
                    }
                }
            }*/

            public void onClick(View v) {
                //String str = et.getText().toString();
                // if (!str.isEmpty()) {
                String str = et.getText().toString();
                if ((!departmentsData.containsKey(str) && parentID == R.id.rvDepartments) || parentID == R.id.rvAnimals) {
                    buttonClicked(str);
                }else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }
            }
            void buttonClicked(String str){

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        removeSingleItem(position, parentID);
                    }
                    insertFromPopup(str, insertIndex, parentID);
                    et.getText().clear();
                    if (parentID == R.id.rvAnimals) {
                        saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {
                        setData();
                        chosenDepartment = str;
                    }
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

                    /*String str = et.getText().toString();
                    if (!str.isEmpty()) {
                        if (deleteFlagForEdit) {
                            deleteFlagForEdit = false;
                            removeSingleItem(position, parentID);
                        }
                        insertFromPopup(str, insertIndex, parentID);
                        popupWindow.dismiss();
                        if (parentID == R.id.rvAnimals) {
                            saveDataWhenItChanged();
                        }
                    }*/

                        String str = et.getText().toString();
                        if ((!departmentsData.containsKey(str) && parentID == R.id.rvDepartments) || parentID == R.id.rvAnimals) {
                            buttonClicked(str);

                        }else {
                            Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                        }


                    handled = true;
                }
                return handled;

            }

            void buttonClicked(String str){

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        removeSingleItem(position, parentID);
                    }
                    insertFromPopup(str, insertIndex, parentID);
                    popupWindow.dismiss();
                    if (parentID == R.id.rvAnimals) {
                        saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {
                        setData();
                        chosenDepartment = str;
                    }
                }
            }
        });


    }

   /* boolean doublingCheck(String str) {

        /*for (String s : departmentsData.keySet()){

            if (s == str) {
                Toast.makeText(getBaseContext(), "find" + s, Toast.LENGTH_SHORT).show();
                return false;

            }
        }
    return true;
       return departmentsData.containsKey(str);

    }*/

    public void saveDataWhenItChanged() {
        List<String> forClone = new ArrayList<>(data);
        forClone.remove(0);
        //forClone.clone(data);
        departmentsData.put(chosenDepartment, forClone);
        //Toast.makeText(getBaseContext(), "chosen: " + chosenDepartment + " data: " + departmentsData.get(chosenDepartment), Toast.LENGTH_LONG).show();

    }


    private void insertFromPopup(String s, int insertIndex, int parentId) {
        //int insertIndex = 1;
        switch(parentId) {
            case R.id.rvDepartments:
                keysForDepartments.add(insertIndex, s);
                departmentsData.put(s,null);
                crossOutNumbersArray.put(s,0);
                //Toast.makeText(this, "" + departmentsData.get(s), Toast.LENGTH_LONG).show();
                adapterForDepartments.notifyItemInserted(insertIndex);

                break;
            case R.id.rvAnimals:
                data.add(insertIndex, s);
                adapter.notifyItemInserted(insertIndex);
                break;
        }
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


    private void removeSingleItem(int removeIndex, int parentId) {
        // int removeIndex = 2;
       switch(parentId) {
            case R.id.rvAnimals:
                data.remove(removeIndex);
                adapter.notifyItemRemoved(removeIndex);
                break;
            case R.id.rvDepartments:
                departmentsData.remove(removeIndex);
                adapterForDepartments.notifyItemRemoved(removeIndex);
                break;

        }
       /* if(parentId == R.id.rvAnimals) {
            data.remove(removeIndex);
            adapter.notifyItemRemoved(removeIndex);
        } else {
            departmentsData.remove(removeIndex);
            adapterForDepartments.notifyItemRemoved(removeIndex);
        }*/


       // data.remove(removeIndex);
       // adapter.notifyItemRemoved(removeIndex);
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
//TODO: Use instead deleting and inserting for edit item
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

   private void deleteSingleItem(int position) {
       if (position >= (data.size()-crossOutNumber)) {
           crossOutNumber--;
           setCrossOutNumber(chosenDepartment);
       }
       if (position>0) {

           // remove your item from data base
           data.remove(position);  // remove the item from list

           adapter.notifyItemRemoved(position); // notify the adapter about the removed item
           saveDataWhenItChanged();

       }
   }

    private void deleteSingleItemInDepartments(View view, int position) {
        if (position>0) {

            // remove your item from data base
            departmentsData.remove(chosenDepartment);  // remove the item from list
            keysForDepartments.remove(chosenDepartment);
            adapterForDepartments.notifyItemRemoved(position); // notify the adapter about the removed item
             int index = 0;
            for (String key : keysForDepartments) {
                if (index == 1){
                    chosenDepartment = key;
                    Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                    break;
                }
                index++;

            }
            setData();


        }
    }

}


