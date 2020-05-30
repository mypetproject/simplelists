package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, DepartmentsAdapter.ItemClickListener, PopupMenu.OnMenuItemClickListener {

    List<String> data;
    MyRecyclerViewAdapter adapter;
    int crossOutNumber;
    static int editButtonClicked = 1;
    boolean deleteFlagForEdit;
    static int adapterPosition;

    DepartmentsAdapter adapterForDepartments;
    List<String> keysForDepartments;
    List<String> keysForLists;

    private Drawer drawerResult = null;

    Toolbar toolbar;

    ListDataDatabase db;
    static ListData chosenListData = new ListData();
    static DepartmentData chosenDepartmentData;
    static Data chosenData = new Data();

    ImageButton mShareButton;
    EditText et;

    private static final String TAG = "myLogs";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // data to populate the RecyclerView with
        db = App.getInstance().getDatabase();
        keysForLists = new ArrayList<>();
        keysForDepartments = new ArrayList<>();

       /* //Flowable example
       db.listDataDao().getAllNames()
                // .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> names) {
                        // ...
                       if (names != null) keysForLists.addAll(names);
                    }

                });*/
        ListData firstElementOfList = new ListData();
        firstElementOfList.setList_name("Добавить список");
        firstElementOfList.list_position = 0;
        db.listDataDao().insert(firstElementOfList);
        Log.d(TAG, "First element to list added " + db.listDataDao().getAllNamesNotFlowable());
        setKeysForLists();
        data = new ArrayList<>();
      /*  //Single.fromCallable(() example
                Single.fromCallable(() -> db.listDataDao().insert(listData)).subscribeOn(Schedulers.io()).subscribe();
*/

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


        final RecyclerView recyclerViewDepartments = findViewById(R.id.rvDepartments);
        LinearLayoutManager layoutManagerDepartments = new LinearLayoutManager(this);
        recyclerViewDepartments.setLayoutManager(layoutManagerDepartments);

       /* //Разделители
       DividerItemDecoration dividerItemDecorationDepartments = new DividerItemDecoration(recyclerViewDepartments.getContext(),
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

        if (keysForDepartments.size() > 1) {
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            getCrossOutNumber();
            getData();
        }

        //Animation for drag & drop for list

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.
                SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.END | ItemTouchHelper.START) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                if (position_dragged >= (data.size() - crossOutNumber) || position_dragged == 0) {
                    position_target = position_dragged;
                } else if (position_target == 0) {
                    position_target = 1;
                } else if (position_target >= (data.size() - crossOutNumber)) {
                    position_target = data.size() - crossOutNumber - 1;
                }
                Collections.swap(data, position_dragged, position_target);

                adapter.notifyItemMoved(position_dragged, position_target);

                Data temp = db.dataDao().getChosenData(position_dragged, chosenDepartmentData.department_id);
                temp.data_position = position_target;
                if (position_dragged > position_target) {
                    db.dataDao().incrementValuesFromPositionToPosition(chosenDepartmentData.department_id, position_dragged, position_target);
                } else {
                    db.dataDao().decrementValuesFromPositionToPosition(chosenDepartmentData.department_id, position_dragged, position_target);
                }
                db.dataDao().update(temp);
                Log.d(TAG, "data keys after swipe: " + db.dataDao().getAllNames(chosenDepartmentData.department_id));
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //setKeysForDepartments();
                //int position = keysForDepartments.indexOf(chosenDepartment);
                int position = chosenDepartmentData.department_position;
                Log.d(TAG, "onSwiped position:" + position);
                switch (direction) {
                    case ItemTouchHelper.START:
                        if (position < (keysForDepartments.size() - 1)) {
                            chosenDepartmentData = db.departmentDataDao()
                                    .getChosenDepartment(chosenDepartmentData.department_position + 1, chosenListData.list_id);
                            recyclerViewDepartments.smoothScrollToPosition(position + 1);
                        }
                        break;

                    case ItemTouchHelper.END:
                        if (position > 1) {
                            chosenDepartmentData = db.departmentDataDao()
                                    .getChosenDepartment(chosenDepartmentData.department_position - 1, chosenListData.list_id);
                            recyclerViewDepartments.smoothScrollToPosition(position - 1);
                        }
                        break;

                }
                getCrossOutNumber();
                getData();
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

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
        ItemTouchHelper helper2 = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerViewDepartments, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                if (position_dragged == 0) {
                    position_target = position_dragged;
                } else if (position_target == 0) {
                    position_target = 1;
                }
                Collections.swap(keysForDepartments, position_dragged, position_target);

                adapterForDepartments.notifyItemMoved(position_dragged, position_target);

                DepartmentData temp = db.departmentDataDao().getChosenDepartment(position_dragged, chosenListData.list_id);
                temp.department_position = position_target;
                if (position_dragged > position_target) {
                    db.departmentDataDao().incrementValuesFromPositionToPosition(chosenListData.list_id, position_dragged, position_target);
                } else {
                    db.departmentDataDao().decrementValuesFromPositionToPosition(chosenListData.list_id, position_dragged, position_target);
                }
                db.departmentDataDao().update(temp);
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position_target, chosenListData.list_id);
                setKeysForDepartments();
                Log.d(TAG, "dep keys after swipe: " + db.departmentDataDao().getAllNames(chosenListData.list_id)
                        + " positions" + db.departmentDataDao().getAllPositions(chosenListData.list_id));
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper2.attachToRecyclerView(recyclerViewDepartments);


// Do delete buttons invisible and hide holder "dobavit'"
        Button mEditButton = (Button) findViewById(R.id.edit_button);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (editButtonClicked) {
                    case 1:
                        editButtonClicked = 0;
                        break;
                    default:
                        editButtonClicked = 1;
                }
                adapter.notifyDataSetChanged();
                adapterForDepartments.notifyDataSetChanged();
            }
        });

        mShareButton = (ImageButton) findViewById(R.id.share_button);

        if (keysForLists.size() < 2) {
            mShareButton.setVisibility(View.INVISIBLE);
        } else {
            mShareButton.setVisibility(View.VISIBLE);
        }

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "share button clicked");
                String stringToSend = listToStringGenerator();
                Log.d(TAG, "string to send generated" + stringToSend);
                newShare(v, stringToSend);
                Log.d(TAG, "new share intent");
            }
        });


        //Get swipes from background
        findViewById(R.id.backgroundLL).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            int position;

            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                position = keysForDepartments.indexOf(chosenDepartmentData.department_name);
                if (position > 1) {
                    chosenDepartmentData = db.departmentDataDao()
                            .getChosenDepartment(chosenDepartmentData.department_position - 1, chosenListData.list_id);
                    recyclerViewDepartments.smoothScrollToPosition(position - 1);
                }
                getCrossOutNumber();
                getData();

            }

            public void onSwipeLeft() {
                position = keysForDepartments.indexOf(chosenDepartmentData.department_name);
                if (position < (keysForDepartments.size() - 1)) {
                    chosenDepartmentData = db.departmentDataDao()
                            .getChosenDepartment(chosenDepartmentData.department_position + 1, chosenListData.list_id);
                    recyclerViewDepartments.smoothScrollToPosition(position + 1);
                }
                getCrossOutNumber();
                getData();

            }

            public void onSwipeBottom() {
            }
        });
    }

    private void newShare(View view, String stringToShare) {


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Поделиться"));


    }

    void setNavigationDrawerData() {
        IDrawerItem[] iDrawerItems = new IDrawerItem[1000];
        for (int i = 0; i < keysForLists.size(); i++) {
            iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i));
            Log.d(TAG, "items for drawer added " + keysForLists.get(i));
        }
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 1) {

                            View parent = (View) view.getParent();
                            int parentID = parent.getId();
                            inputTextDialogWindow(view, 1, position - 1, parentID);
                        } else {
                            setActiveList(position);
                        }
                        return false;
                    }
                })
                .addDrawerItems(iDrawerItems)
                .build();
        //       Log.d(TAG, "setNavigationDrawerData() drawerBuilderEnded");

        if (keysForLists.size() > 1) {
            setTitle(chosenListData.getList_name());
        } else {
            setTitle("<- Нажмите");
        }
//Log.d(TAG, "setNavigationDrawerData() ended");
    }

    void setActiveList(int position) {
        if (keysForLists.size() > 1) {
            chosenListData = db.listDataDao().getChosenList(position - 1);
            Log.d(TAG, "method: 'setNavigationDrawerData()'; " + chosenListData.getAllInString());
            setTitle(chosenListData.getList_name());
            if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 1) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            } else {
                chosenDepartmentData = new DepartmentData(chosenListData.list_id, 0, "", 0);
            }
        }
        setDepartmentsData();
        setKeysForDepartments();
        if (keysForDepartments.size() < 2) {
            crossOutNumber = 0;
            data.clear();
            adapter.notifyDataSetChanged();
            adapterForDepartments.notifyDataSetChanged();
        } else {
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            getCrossOutNumber();
            getData();
        }
        Log.d(TAG, "setActiveList(int position) ended");
    }

    @Override
    public void onBackPressed() {
        //todo translate
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public static void setAdapterPosition(int pos) {
        adapterPosition = pos;
    }

    public void setKeysForDepartments() {
        keysForDepartments.clear();
        // Log.d(TAG, "keysForDepartments clear");
        keysForDepartments.addAll(db.departmentDataDao().getAllNames(chosenListData.list_id));
        Log.d(TAG, "method: 'setKeysForDepartment'; departments keys names: " + keysForDepartments);
    }


    public void setKeysForLists() {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        keysForLists.clear();
        Log.d(TAG, "keysforlists cleaned");
        keysForLists.addAll(db.listDataDao().getAllNamesNotFlowable());
        Log.d(TAG, "keysforlists added information from base " + keysForLists);
        Log.d(TAG, name + " setKeysForLists() added information from base " + db.listDataDao().getAllPositions());
        if (keysForLists.size() > 1) {
            chosenListData = db.listDataDao().getChosenList(1);


        }
        mShareButton = (ImageButton) findViewById(R.id.share_button);
        if (keysForLists.size() < 2) {
            mShareButton.setVisibility(View.INVISIBLE);
        } else {
            mShareButton.setVisibility(View.VISIBLE);
        }
        setNavigationDrawerData();
        setDepartmentsData();
        setKeysForDepartments();
    }


    public void getData() {
        data.clear();
        if (keysForDepartments.size() > 0)
            data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
        if (data.size() == 0) {
            Data dataForInsert = new Data(chosenDepartmentData.department_id, 0, "Добавить");
            db.dataDao().insert(dataForInsert);
            Log.d(TAG, "Added first element to data: " + dataForInsert.getAllInString());
            data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
        }

        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
    }

    void setDepartmentsData() {
        if (keysForLists.size() > 1) {
            DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 0, "Добавить", 0);
            db.departmentDataDao().insert(departmentData);
            Log.d(TAG, "items departments data added");
        }
        Log.d(TAG, "setDepartmentsData() ended");
    }

    void getCrossOutNumber() {
        if (keysForLists.size() > 1) {
            crossOutNumber = chosenDepartmentData.CrossOutNumber;
        }
    }

    void setCrossOutNumber() {
        if (keysForLists.size() > 1) {
            chosenDepartmentData.CrossOutNumber = crossOutNumber;
            db.departmentDataDao().update(chosenDepartmentData);
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        View parent = (View) view.getParent();
        int parentID = parent.getId();
        switch (parentID) {
            case R.id.ll:
                deleteSingleItem(position);
                break;
            case R.id.depLl:
                deleteSingleItemInDepartments(position);
                break;
            case R.id.rvDepartments:
                if (position == 0) {
                    crossOutNumber = 0;
                    inputTextDialogWindow(view, 1, position, parentID);
                } else {
                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
                    Log.d(TAG, "Chosen department: " + chosenDepartmentData.getAllInString());
                    getData();
                    getCrossOutNumber();
                }
                break;
            case R.id.rvAnimals:
                if (position == 0) {
                    inputTextDialogWindow(view, 1, position, parentID);
                } else if (editButtonClicked == 1) {


                    if (position < (data.size() - crossOutNumber)) {
                        crossOutNumber++;
                        setCrossOutNumber();
                        moveSingleItem(position);
                    } else {
                        crossOutNumber--;
                        setCrossOutNumber();
                        moveSingleItemToTop(position);
                    }
                } else {
                    deleteFlagForEdit = true;
                    chosenData = db.dataDao().getChosenData(position, chosenDepartmentData.department_id);
                    inputTextDialogWindow(view, position, position, parentID);
                }
                break;
        }
    }

    public void inputTextDialogWindow(final View view, final int insertIndex, final int position, final int parentID) {

        final EditText et = new EditText(view.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        String title;
        if (position != 0) {
            TextView text = new TextView(view.getContext());
            switch (parentID) {
                case R.id.rvAnimals:
                    text = view.findViewById(R.id.tvAnimalName);
                    break;
                case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;
            }
            et.setText(text.getText().toString() + " ");
            et.setSelection(et.length());

            title = "Редактировать";
        } else {
            deleteFlagForEdit = false;
            title = "Добавить";
            et.setHint("Введите сообщение");
        }


        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(title)
                //.setMessage("Write your message here")
                .setCancelable(true)
                .setView(et)
                .setPositiveButton("Ok", null)
                .setNeutralButton("Следующее", null)
                .setNegativeButton(
                        "Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (uniqueTest(str, parentID)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, parentID, view);
                    dialog.dismiss();
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }

            }

        });

        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (uniqueTest(str, parentID)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, parentID, view);
                    et.getText().clear();
                    et.setHint("Введите сообщение");
                    dialog.setTitle("Добавить");
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }

            }

        });

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus && et.isEnabled() && et.isFocusable()) {
                    et.post(new Runnable() {
                        @Override
                        public void run() {
                            final InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });
        et.requestFocus();
    }

    private void inputButtonClicked(String str, int insertIndex, int parentID, View view) {
        if (!str.isEmpty()) {
            if (deleteFlagForEdit) {
                deleteFlagForEdit = false;
                // removeSingleItem(position, parentID);
                db.dataDao().updateSingleItem(chosenData.data_id, str);
                getData();
            } else {
                insertFromPopup(str, insertIndex, parentID, view);
            }
            if (parentID == R.id.rvAnimals) {

            } else if (parentID == R.id.rvDepartments) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                getData();
            }
        }
    }

    boolean uniqueTest(String str, int parentID) {
        return ((!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str) && parentID == R.id.rvDepartments)
                || parentID == R.id.rvAnimals
                || (!db.listDataDao().getAllNamesNotFlowable().contains(str) && parentID == R.id.material_drawer_recycler_view));
    }

    private void setNewDepartment(String s) {

        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 1, s, 0);
        db.departmentDataDao().incrementValues(chosenListData.list_id, 0);
        db.departmentDataDao().insert(departmentData);
        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
        Log.d(TAG, "method: 'setNewDepartment'; departments data after insert new in db: " + db.departmentDataDao().getAllNames(chosenListData.list_id));
    }

    private void setNewDepartmentFromParse(String s, int position) {
        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, position, s, 0);
        db.departmentDataDao().insert(departmentData);
        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
        Log.d(TAG, "method: 'setNewDepartmentFromParse'; departments data after insert new in db: " + db.departmentDataDao().getAllNames(chosenListData.list_id));
    }

    private void setNewData(String s, int position) {
        Data newData = new Data(chosenDepartmentData.department_id, position, s);
        db.dataDao().incrementValues(chosenDepartmentData.department_id, position - 1);
        db.dataDao().insert(newData);
        Log.d(TAG, "Test increment data position: " + db.dataDao().getAllPositions(chosenDepartmentData.department_id));
        Log.d(TAG, "new data set ended");
    }

    private void setNewList(String s) {
        ListData listData = new ListData();
        listData.setList_name(s);
        listData.list_position = 1;
        db.listDataDao().incrementValues();
        db.listDataDao().insert(listData);
        chosenListData = db.listDataDao().getChosenList(1);
    }

    /* //was used for rxjava
    private int InsertListInAnotherThread(ListData listData) {
        db.listDataDao().incrementValues();
        db.listDataDao().setDobavitInZero();
        db.listDataDao().insert(listData);

        chosenListData = db.listDataDao().getChosenList(1);

        return 0;
    }*/

    private void insertFromPopup(String s, int insertIndex, int parentId, View view) {
        switch (parentId) {
            case R.id.rvDepartments:
                setNewDepartment(s);
                setKeysForDepartments();
                adapterForDepartments.notifyItemInserted(insertIndex);
                Log.d(TAG, "in rvDepartments, adapter notified. Chosen department");
                break;
            case R.id.rvAnimals:
                data.add(insertIndex, s);
                setNewData(s, insertIndex);
                adapter.notifyItemInserted(insertIndex);
                break;
            case R.id.material_drawer_recycler_view:
                parser(s);
                setKeysForLists();
                setKeysForDepartments();
                if (keysForDepartments.size() > 1) {
                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                    getData();
                } else {
                    data.clear();
                    Log.d(TAG, "data clear");
                    final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                            .setMessage("Заполнить списком по умолчанию?")
                            .setCancelable(true)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setDefaultList();
                                            setKeysForDepartments();
                                            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                                            getData();
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton(
                                    "Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                    Log.d(TAG, "dialog built");
                }
                break;
        }
    }

    private void setDefaultList() {
        LinkedHashMap<String, List<String>> defaultData = new LinkedHashMap<>();
        List<String> departmentData = new ArrayList<>();

        departmentData.add("Зубная паста");
        departmentData.add("Средство для мытья посуды");
        departmentData.add("Мешки для мусора");
        departmentData.add("Пищевая пленка");
        departmentData.add("Губки для посуды");
        departmentData.add("Стиральный порошок");
        defaultData.put("Хозтовары", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Влажные салфетки");
        departmentData.add("Шампунь");
        departmentData.add("Мыло");
        defaultData.put("Косметика", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Свинина");
        departmentData.add("Говядина");
        departmentData.add("Грудка куриная");
        departmentData.add("Фарш");
        defaultData.put("Мясная продукция", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Рыба");
        departmentData.add("Креветки");
        defaultData.put("Рыба и морепродукты", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Яблоки");
        departmentData.add("Бананы");
        departmentData.add("Апельсины");
        departmentData.add("Груши");
        departmentData.add("Капуста");
        departmentData.add("Лук");
        departmentData.add("Морковь");
        departmentData.add("Картофель");
        departmentData.add("Зелень");
        departmentData.add("Помидоры");
        departmentData.add("Огурцы");
        departmentData.add("Чеснок");
        departmentData.add("Грибы");
        defaultData.put("Овощи и фрукты", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Молоко");
        departmentData.add("Сметана");
        departmentData.add("Творог");
        departmentData.add("Масло сливочное");
        departmentData.add("Кефир");
        departmentData.add("Сливки");
        departmentData.add("Сыр");
        departmentData.add("Яйца куриные");
        defaultData.put("Молочные продукты", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Оливки");
        departmentData.add("Горошек");
        departmentData.add("Кукуруза");
        departmentData.add("Фасоль в томатном соусе");
        defaultData.put("Консервы, соленья", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Макароны");
        departmentData.add("Крупа гречневая");
        departmentData.add("Рис");
        departmentData.add("Сахар");
        departmentData.add("Овсянка");
        departmentData.add("Чай");
        departmentData.add("Кофе");
        departmentData.add("Растительное масло");
        defaultData.put("Бакалея", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Хлеб");
        departmentData.add("Булочки");
        departmentData.add("Кексы с изюмом");
        departmentData.add("Шоколад");
        defaultData.put("Хлебобулочные, сладости", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Майонез");
        departmentData.add("Кетчуп");
        defaultData.put("Соусы, приправы", departmentData);
        departmentData = new ArrayList<>();

        departmentData.add("Вода бутилированная");
        departmentData.add("Сок");
        defaultData.put("Вода, напитки", departmentData);

        int departmentPosition = 1;

        for (String key : defaultData.keySet()) {
            DepartmentData dpData = new DepartmentData(chosenListData.list_id, departmentPosition, key, 0);
            db.departmentDataDao().insert(dpData);
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);
            getData();
            int dataPosition = 1;
            Log.d(TAG, "all default data: " + defaultData.get(key));
            for (String s : defaultData.get(key)) {
                Data data = new Data(chosenDepartmentData.department_id, dataPosition, s);
                db.dataDao().insert(data);
                dataPosition++;
            }
            departmentPosition++;
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
        int toPosition = data.size() - 1;

        // update data array
        String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

        Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        int pos = db.dataDao().getAllPositions(chosenDepartmentData.department_id).size();
        db.dataDao().updateSingleItemPosition(temp.data_id, pos);
        db.dataDao().decrementValues(chosenDepartmentData.department_id, fromPosition);
        adapter.notifyItemMoved(fromPosition, pos - 1);
        adapter.notifyItemChanged(pos - 1);
    }

    private void moveSingleItemToTop(int fromPosition) {
        int toPosition = 1;

        // update data array
        String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

        Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        db.dataDao().incrementValuesFromOneToPosition(chosenDepartmentData.department_id, fromPosition);
        db.dataDao().updateSingleItemPosition(temp.data_id, 1);
        adapter.notifyItemMoved(fromPosition, toPosition);
        adapter.notifyItemChanged(toPosition);
    }

    private void deleteSingleItem(int position) {
        if (position >= (data.size() - crossOutNumber)) {
            crossOutNumber--;
            setCrossOutNumber();
        }
        if (position > 0) {
            // remove your item from data base
            data.remove(position);  // remove the item from list
            db.dataDao().deleteSingleData(position, chosenDepartmentData.department_id);
            db.dataDao().decrementValues(chosenDepartmentData.department_id, position);
            adapter.notifyItemRemoved(position); // notify the adapter about the removed item
        }
    }


    private void deleteSingleItemInDepartments(int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        if (position > 0 && keysForDepartments.size() > 2) {
            int tempDepartmentId = chosenDepartmentData.department_id;
            Log.d(TAG, name + "tempDepartmentId before setKeysForDepartments: " + tempDepartmentId);
            db.departmentDataDao().deleteSingleData(position, chosenListData.list_id);
            db.departmentDataDao().decrementValues(chosenListData.list_id, position);
            setKeysForDepartments();
            Log.d(TAG, name + "tempDepartmentId after setKeysForDepartments: " + tempDepartmentId);
            Log.d(TAG, name + "chosenDepId after setKeysForDepartments: " + chosenDepartmentData.department_id);
            if (position == 1) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
                getCrossOutNumber();
            } else if (chosenDepartmentData.department_position == position) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position - 1, chosenListData.list_id);
                getCrossOutNumber();
            }
            getData();
        } else if (position > 0) {
            db.departmentDataDao().deleteSingleData(position, chosenListData.list_id);
            crossOutNumber = 0;
            data.clear();
            setKeysForDepartments();
        }
        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
    }

    private void deleteSingleItemInList() {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        int position = chosenListData.list_position;
        if (position > 0 && keysForLists.size() > 2) {
            db.listDataDao().deleteSingleItem(chosenListData.list_id);
            db.listDataDao().decrementValues(chosenListData.list_position);
            setKeysForLists();
            if (position != 1) {
                setActiveList(position);
            } else {
                setActiveList(position + 1);
            }
       } else if (position > 0) {
            db.listDataDao().deleteSingleItem(chosenListData.list_id);
            crossOutNumber = 0;
            data.clear();
            setKeysForLists();
        }
        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
    }

    private void parser(String inputText) {
        String[] tokens = inputText.split("");
        String listName = "";
        String departmentName = "";
        String dataName = "";
        int index = 0;
        int position = 1;
        int dataPosition = 1;
        for (String s : tokens) {
            if (s.equals("[") && index == 0) {
                if (listName.isEmpty()) listName = "Enter list name";

                if (!db.listDataDao().getAllNamesNotFlowable().contains(listName)) {
                    setNewList(listName);
                } else {
                    boolean nameSetStatus = true;
                    int copiesCounter = 0;
                    String tempListName = listName;
                    do {
                        if (!db.listDataDao().getAllNamesNotFlowable().contains(tempListName)) {
                            listName += " (" + copiesCounter + ")";
                            setNewList(listName);
                            nameSetStatus = false;
                        } else {
                            copiesCounter++;
                            tempListName = listName + " (" + copiesCounter + ")";
                        }
                    } while (nameSetStatus);
                }
                listName = "";
                index++;
                continue;
            } else if (index == 0 && !s.equals("]")) {
                listName += s;
            }

            if (s.equals("[") && index == 1) {
                if (departmentName.isEmpty()) departmentName = "Enter name";


                if (!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(departmentName)) {
                    setNewDepartmentFromParse(departmentName, position);
                } else {
                    boolean nameSetStatus = true;
                    int copiesCounter = 0;
                    String tempDepartmentName = departmentName;
                    do {
                        if (!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(tempDepartmentName)) {
                            departmentName += " (" + copiesCounter + ")";
                            setNewDepartmentFromParse(departmentName, position);
                            nameSetStatus = false;
                        } else {
                            copiesCounter++;
                            tempDepartmentName = departmentName + " (" + copiesCounter + ")";
                        }
                    } while (nameSetStatus);
                }
                departmentName = "";
                index++;
                position++;
                getData();
                continue;
            } else if (s.equals("]") && index == 1) {
                index--;
                continue;
            } else if (index == 1) {
                departmentName += s;
            }

            if (s.equals("]") && index == 2) {
                if (!dataName.isEmpty()) setNewData(dataName, dataPosition);
                Log.d(TAG + " parser", "parser: data name: " + dataName + " position: " + dataPosition);
                dataName = "";
                index--;
                dataPosition = 1;
            } else if (s.equals(";") && index == 2) {
                if (!dataName.isEmpty()) setNewData(dataName, dataPosition);
                Log.d(TAG + " parser", "parser: data name: " + dataName + " position: " + dataPosition + " index: " + index);
                dataName = "";
                dataPosition++;
            } else if (index == 2) {
                dataName += s;
            }
        }

        if (!listName.isEmpty()) setNewList(listName);
        if (!departmentName.isEmpty()) setNewDepartmentFromParse(departmentName, position);
        if (!dataName.isEmpty()) {
            getData();
            setNewData(dataName, dataPosition);
        }
    }

    private String listToStringGenerator() {
        String stringToSend = "";
        Log.d(TAG, "In listToStringGenerator()");
        stringToSend += chosenListData.getList_name() + "[";
        Log.d(TAG, "In stringToSend += chosenListData.getList_name()" + db.departmentDataDao().getAllPositions(chosenListData.list_id));

        int departmentPosition = 1;
        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 1)
            for (String s : db.departmentDataDao().getAllNamesExceptFirst(chosenListData.list_id)) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);
                stringToSend += s + "[";
                int dataCounter = 0;
                for (String dataS : db.dataDao().getAllNamesForGenerator(chosenDepartmentData.department_id)) {
                    stringToSend += dataS + ";";
                    dataCounter++;
                    if (dataCounter == (db.dataDao().getAllNamesForGenerator(chosenDepartmentData.department_id).size() - chosenDepartmentData.CrossOutNumber))
                        break;
                }
                if (dataCounter > 0)
                    stringToSend = stringToSend.substring(0, stringToSend.length() - 1);
                stringToSend += "]";
                departmentPosition++;
            }
        stringToSend += "]";
        Log.d(TAG, "StringToSend ready " + stringToSend);
        return stringToSend;
    }

    public void onMoreMenuItemButtonClick(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.setForceShowIcon(true);
        popup.show();

        if (editButtonClicked == 1) {
            popup.getMenu().findItem(R.id.menu_edit).setTitle("Редактировать список");
        } else {
            popup.getMenu().findItem(R.id.menu_edit).setTitle("Закончить редактирование");
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                String stringToSend = listToStringGenerator();
                newShare(item.getActionView(), stringToSend);
                return true;
            case R.id.menu_edit:
                switch (editButtonClicked) {
                    case 1:
                        editButtonClicked = 0;
                        break;
                    default:
                        editButtonClicked = 1;
                }
                adapter.notifyDataSetChanged();
                adapterForDepartments.notifyDataSetChanged();
                return true;
            case R.id.menu_delete:
                deleteSingleItemInList();
                return true;
            default:
                return false;
        }
    }
}


//todo Перенос элемента в другое меню по долгому тапу (например с помощью контекстного меню или перетаскивания)
//todo Добавление списков, отделов, элементов с помощью google assistant
//todo Обучение интерфейсу при первом старте
//todo При share списка сделать передаваемый string читаемым
//todo Баг при добавлении нового листа, выделяется всегда первый пункт "Добавить"
//todo alertDialog для подтверждения удаления отдела или списка
//todo popupmenu по долгому тапу на лист, отдел или элемент
//todo обойти ограничение массива iDrawerItem[100] или перезаписывать элементы
//todo блокировка списка отпечатком и пинкодом
//todo поиск по списку?
//todo убрать edit_button
//todo аттач фото к элементу отдела
//todo счетчик невычеркнутых элементов к списку в боковое меню
//todo установка количества к элементу
//todo проверка на hardware клавиатуру при вызове alertdialog для корректировки или добавления элемента (те, где есть edittext)
//TODO в режиме редактирования делать из title в toolbar edittext вместо textview (title = "", edittext - visible, после выхода из режима редактирования забираем с edittext введеный текст, title = et, edittext.gone)
//todo привязать свайп влево-вправо к recyclerview, а не к холдерам (для смены отдела)
//todo по лонгтапу по элементу отдела появляется чекбокс, где можно выделить элементы и удалить несколько сразу
//todo центрировать отдел по тапу