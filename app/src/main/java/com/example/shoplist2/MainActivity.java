package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MotionEventCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;


//import io.reactivex.android.schedulers.AndroidSchedulers;
import javax.security.auth.callback.Callback;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


//import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, DepartmentsAdapter.ItemClickListener {

    List<String> data;
    MyRecyclerViewAdapter adapter;
    static int crossOutNumber;
    static int editButtonClicked = 1;
    boolean deleteFlagForEdit;
    static int adapterPosition;
    static String chosenDepartment;
    //String chosenList;
    //int previewPositionOfDepartment;

    // List<String> departmentsData;
    // List<String, List<String>> departmentsData;
    public LinkedHashMap<String, DataWithCrossOutNumber> departmentsData;
    public LinkedHashMap<String, LinkedHashMap<String, DataWithCrossOutNumber>> listData;
    //public Map<String, LinkedHashMap<String, List<Integer>>> listOfCrossOutNumbers;
    DepartmentsAdapter adapterForDepartments;
    List<String> keysForDepartments;
    Map<String, Integer> crossOutNumbersArray;
    List<String> keysForLists;

    private Drawer drawerResult = null;
    IDrawerItem[] iDrawerItems = new IDrawerItem[1000];
    Toolbar toolbar;

    List<String> ls;
    List<Integer> lInt;
    List<Integer> lPos;
    ListDataDatabase db;
    ListData chosenListData = new ListData();
    DepartmentData chosenDepartmentData;
    Data chosenData = new Data();

    ImageButton mShareButton;
    EditText et;
    private ClipboardManager myClipboard;
    private ClipData myClip;

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
       /* db.listDataDao().getAllNames()
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
       // List<String> fromDB = db.listDataDao().getAllNamesNotFlowable();
       // if (fromDB.size() > 0) {
           // keysForLists.clear();
           // keysForLists.addAll(fromDB);
        Log.d(TAG, "First element to list added " + db.listDataDao().getAllNamesNotFlowable());
            setKeysForLists();

       // Toast.makeText(getBaseContext(), "" + keysForLists, Toast.LENGTH_SHORT).show();

        //ArrayList<String> dataForMap = new ArrayList<String>();
        //dataForMap.add("Добавить");
       /* dataForMap.add("Cow");
        dataForMap.add("Camel");
        dataForMap.add("Sheep");
        dataForMap.add("Bread");*/
        //DataWithCrossOutNumber dataForMap2 = new DataWithCrossOutNumber(dataForMap, 0);
        departmentsData = new LinkedHashMap<String, DataWithCrossOutNumber>();
       /* departmentsData.put("Добавить", null);
        departmentsData.put("Butcher's", dataForMap);
        departmentsData.put("Confectioner's", null);
        departmentsData.put("Stationer's", null);
        departmentsData.put("Greengrocer's", null);*/

       // listData = new LinkedHashMap<String, LinkedHashMap<String, DataWithCrossOutNumber>>();
       // listData.put("Добавить", null);
       // listData.put("First", new LinkedHashMap<String, DataWithCrossOutNumber>());
       // listData.put("Second", new LinkedHashMap<String, DataWithCrossOutNumber>());

       // listData.get("First").put("Добавить", null);
       // listData.get("First").put("Butcher's", dataForMap2);
    /*    listData.get("First").put("Confectioner's", new DataWithCrossOutNumber(null,0));
        listData.get("First").put("Stationer's", new DataWithCrossOutNumber(null,0));
        listData.get("First").put("Greengrocer's", new DataWithCrossOutNumber(null,0));*/
       /*LinkedHashMap<String, ArrayList<DataWithCrossOutNumber>> bufDepData = new LinkedHashMap<String, ArrayList<DataWithCrossOutNumber>>();
        bufDepData.putAll(listData.get("First"));
        for (Map.Entry entry: bufDepData.entrySet()) {
            ArrayList<String> arrList = new ArrayList<String>();
            if (entry.getValue() != null) {
                for (DataWithCrossOutNumber d : (DataWithCrossOutNumber[]) entry.getValue()) {
                    arrList.add(d.getData());
                }
            }
            departmentsData.put((String) entry.getKey(), arrList);
        }*/
        //departmentsData = listData.get("First");


       // Toast.makeText(getBaseContext(), "" + listData.get(chosenList).keySet(), Toast.LENGTH_LONG).show();
      //  keysForDepartments = new ArrayList<>(listData.get(chosenList).keySet());




        //Toast.makeText(getBaseContext(), "" + keysForDepartments, Toast.LENGTH_LONG).show();
        data = new ArrayList<>();
//        data.add("Добавить");
      /*  if (keysForLists.size() == 0) {
            keysForLists = new ArrayList<>(listData.keySet());
            //int counter = 0;
            //for (String s : keysForLists) {
                ListData listData = new ListData();
                listData.setList_name(keysForLists.get(0));

                listData.list_position = 0;
              //  counter++;
                // Toast.makeText(getBaseContext(), "" + listData.list_position, Toast.LENGTH_SHORT).show();
                //listDataDao.insert(listData);
                Single.fromCallable(() -> db.listDataDao().insert(listData)).subscribeOn(Schedulers.io()).subscribe();

        }*/
       /* if (keysForLists.size() > 1) {
            chosenList = keysForLists.get(1);
        }*/
        //  keysForLists = new ArrayList<>();
       // setKeysForLists();
       // Toast.makeText(getBaseContext(), "" + keysForLists, Toast.LENGTH_LONG).show();
        /*keysForLists.add("Добавить");
        keysForLists.add("First");
        keysForLists.add("Second");*/


        //ListDataDao listDataDao = db.listDataDao();
        //Single.fromCallable(() -> db.listDataDao().deleteAll()).subscribeOn(Schedulers.io()).subscribe();


        ls = new ArrayList<>();
        lInt = new ArrayList<>();
        lPos = new ArrayList<>();
        //List<ListData> ld = new ArrayList<ListData>();
       /*db.listDataDao().getAll()
              // .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ListData>>() {
                    @Override
                    public void accept(List<ListData> lstdt) {
                        // ...
                      for (ListData s : lstdt) {
                          //Toast.makeText(getBaseContext(), "" + lstdt.size(), Toast.LENGTH_SHORT).show();
                          ls.add(s.getList_name());
                          lInt.add(s.list_id);
                          lPos.add(s.list_position);
                      }
                    }

                });
                //Toast.makeText(getBaseContext(), "" +  lPos.size(), Toast.LENGTH_SHORT).show();

        /*for (ListData s : ld) {
            String x = s.getList_name();
            ls.add(x);
        }*/

       /* crossOutNumbersArray = new LinkedHashMap<String, Integer>();
        for (String key : departmentsData.keySet()) {
            crossOutNumbersArray.put(key, 0);
            //keysForDepartments.add(key);
        }*/


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

        if (keysForDepartments.size() > 1) {
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1,chosenListData.list_id);

           // chosenDepartment = keysForDepartments.get(1);
            chosenDepartment = chosenDepartmentData.department_name;
            getData();
        }
       // setDepartmentsData();
      //  setKeysForDepartments();
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

                Data temp = db.dataDao().getChosenData(position_dragged,chosenDepartmentData.department_id);
                temp.data_position = position_target;
                if (position_dragged > position_target) {
                    db.dataDao().incrementValuesFromPositionToPosition(chosenDepartmentData.department_id, position_dragged,position_target);
                } else {
                    db.dataDao().decrementValuesFromPositionToPosition(chosenDepartmentData.department_id,position_dragged,position_target);
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
                                    .getChosenDepartment(chosenDepartmentData.department_position +1, chosenListData.list_id);
                            //chosenDepartment = keysForDepartments.get(position + 1);
                            chosenDepartment = chosenDepartmentData.department_name;
                            // Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                            recyclerViewDepartments.smoothScrollToPosition(position + 1);
                        }
                        break;

                    case ItemTouchHelper.END:
                        if (position > 1) {
                            chosenDepartmentData = db.departmentDataDao()
                                    .getChosenDepartment(chosenDepartmentData.department_position -1, chosenListData.list_id);
                            //chosenDepartment = keysForDepartments.get(position - 1);
                            chosenDepartment = chosenDepartmentData.department_name;
                            //Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                            recyclerViewDepartments.smoothScrollToPosition(position - 1);
                        }
                        break;

                }
                getCrossOutNumber();
                getData();
                //getCrossOutNumber(chosenDepartment);


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

                DepartmentData temp = db.departmentDataDao().getChosenDepartment(position_dragged,chosenListData.list_id);
                temp.department_position = position_target;
                if (position_dragged > position_target) {
                    db.departmentDataDao().incrementValuesFromPositionToPosition(chosenListData.list_id, position_dragged,position_target);
                } else {
                    db.departmentDataDao().decrementValuesFromPositionToPosition(chosenListData.list_id,position_dragged,position_target);
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
                // Toast.makeText(v.getContext(), "You clicked edit button", Toast.LENGTH_SHORT).show();
                /*setDepartmentsData();
                setKeysForDepartments();
                getData();*/
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
                newShare(v,stringToSend);
                Log.d(TAG, "new share intent");
            }
        });



        //Get swipes from background
        findViewById(R.id.backgroundLL).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            int position;

            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                position = keysForDepartments.indexOf(chosenDepartment);
                if (position > 1) {
                    chosenDepartmentData = db.departmentDataDao()
                            .getChosenDepartment(chosenDepartmentData.department_position - 1, chosenListData.list_id);
                   // chosenDepartment = keysForDepartments.get(position - 1);
                    chosenDepartment = chosenDepartmentData.department_name;
                    recyclerViewDepartments.smoothScrollToPosition(position - 1);
                }
                getCrossOutNumber();
                getData();
                //!!getCrossOutNumber(chosenDepartment);

            }

            public void onSwipeLeft() {
                position = keysForDepartments.indexOf(chosenDepartment);
                if (position < (keysForDepartments.size() - 1)) {
                    chosenDepartmentData = db.departmentDataDao()
                            .getChosenDepartment(chosenDepartmentData.department_position +1, chosenListData.list_id);
                   // chosenDepartment = keysForDepartments.get(position + 1);
                    chosenDepartment = chosenDepartmentData.department_name;
                    recyclerViewDepartments.smoothScrollToPosition(position + 1);
                }
                getCrossOutNumber();
                getData();
                //!!getCrossOutNumber(chosenDepartment);

            }

            public void onSwipeBottom() {
            }
        });

        //SnapHelper snapHelper = new LinearSnapHelper();
        // snapHelper.attachToRecyclerView(recyclerViewDepartments);

        //previewPositionOfDepartment = 1;

        // Handle Toolbar

       /* if (keysForLists.size()>1) {
            chosenList = keysForLists.get(1);
        }*/
        //setNavigationDrawerData();
        // PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Ololo");
        // PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Olola");


       /* drawerResult = new DrawerBuilder()
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
                            Toast.makeText(getBaseContext(), "ParentID: " + view.getParent(), Toast.LENGTH_LONG).show();
                            onButtonShowPopupWindowClick(view, 1, position - 1, parentID);
                        } else {
                            //TextView  text = new TextView(view.getContext());

                            chosenList = keysForLists.get(position - 1);
                            //Toast.makeText(MainActivity.this, "Selected list: " + chosenList, Toast.LENGTH_SHORT).show();
                            setTitle(chosenList);
                            setDepartmentsData();
                            //setSupportActionBar(toolbar);

                        }
                        return false;
                    }
                })

                .addDrawerItems(
                        iDrawerItems
                        /*new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1),*/


             /*   )
                .build();*/



    }

    private void newShare(View view, String stringToShare) {


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,"Поделиться"));


    }
    void setNavigationDrawerData() {
        //setKeysForLists();
        for (int i = 0; i < keysForLists.size(); i++) {
            iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i));
            Log.d(TAG, "items for drawer added ");
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
                           // Toast.makeText(getBaseContext(), "ParentID: " + view.getParent(), Toast.LENGTH_LONG).show();
                            //onButtonShowPopupWindowClick(view, 1, position - 1, parentID);
                            inputTextDialogWindow(view, 1, position - 1, parentID);
                          /*  setDepartmentsData();
                            setKeysForDepartments();
                            data.clear();*/
                            //adapterForDepartments.notifyDataSetChanged();
                        } else {
                            //TextView  text = new TextView(view.getContext());
                            //db = App.getInstance().getDatabase();

                            db.listDataDao().getAll()
                                    // .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<List<ListData>>() {
                                        @Override
                                        public void accept(List<ListData> lstdt) {
                                            // ...
                                            ls.clear();
                                            lInt.clear();
                                            lPos.clear();
                                            for (ListData s : lstdt) {
                                                //Toast.makeText(getBaseContext(), "" + lstdt.size(), Toast.LENGTH_SHORT).show();
                                                ls.add(s.getList_name());
                                                lInt.add(s.list_id);
                                                lPos.add(s.list_position);
                                            }
                                        }

                                    });
                            //Toast.makeText(getBaseContext(), "" + lInt + ls + lPos, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "method: 'setNavigationDrawerData()'; list data = " + lInt + ls + lPos);
                            //Toast.makeText(MainActivity.this, "Selected list: " + chosenList, Toast.LENGTH_SHORT).show();
                            if (keysForLists.size() > 1) {

                                chosenListData = db.listDataDao().getChosenList(position-1);
                                Log.d(TAG, "method: 'setNavigationDrawerData()'; " + chosenListData.getAllInString());
                                //chosenList = keysForLists.get(position - 1);
                               // chosenList = chosenListData.getList_name();
                                setTitle(chosenListData.getList_name());
                                if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 1) {
                                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1,chosenListData.list_id);
                                }
                            }
                            setDepartmentsData();
                            setKeysForDepartments();


                           // getData();

                            if (keysForDepartments.size()<2) {
                                crossOutNumber = 0;
                                data.clear();
                                adapter.notifyDataSetChanged();
                                adapterForDepartments.notifyDataSetChanged();
                            } else {
                                //String text = keysForDepartments.get(1);
                               // chosenDepartment = text;
                                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1,chosenListData.list_id);
                                chosenDepartment = chosenDepartmentData.department_name;
                                getCrossOutNumber();
                                getData();

                                //!!getCrossOutNumber(chosenDepartment);


                            }

                            //setData();
                            //Toast.makeText(getBaseContext(), "" + departmentsData.keySet(), Toast.LENGTH_SHORT).show();

                            //setSupportActionBar(toolbar);
                            //notifyAdapters();
                        }
                        return false;
                    }
                })
                .addDrawerItems(iDrawerItems)
                .build();


        if (keysForLists.size() > 1) {
            setTitle(chosenListData.getList_name());
        } else {
            setTitle("<- Нажмите");
        }

    }

    void notifyAdapters() {
        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
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
        /*if (listData.size() > 1) {
            if (listData.get(chosenList) != null) keysForDepartments.addAll(listData.get(chosenList).keySet());
        }*/
        Log.d(TAG, "keysForDepartments clear");
       // if (keysForLists.size() > 1) {
            keysForDepartments.addAll(db.departmentDataDao().getAllNames(chosenListData.list_id));

            Log.d(TAG, "method: 'setKeysForDepartment'; departments keys names: " + keysForDepartments);
           /* for (DepartmentData d : db.departmentDataDao().getAll(chosenListData.list_id)) {
                Log.d(TAG, "method: 'setKeysForDepartment'; departments keys data: " + d.getAllInString());
            }*/
        //}
        //if (chosenList != null)
        //Toast.makeText(getBaseContext(), "" + new ArrayList<String>(listData.get(chosenList).keySet()), Toast.LENGTH_LONG).show();
  //      adapter.notifyDataSetChanged();
//adapterForDepartments.notifyDataSetChanged();
    }


    public void setKeysForLists() {

      // if (listData != null) keysForLists.addAll(listData.keySet());
        //if (listData != null) keysForLists.addAll(db.listDataDao().getAllNames());
       /* db.listDataDao().getAllNames()
                // .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> keysList) {
                        // ...
                        keysForLists.clear();
                        if (keysList != null) keysForLists.addAll(keysList);
                       //Toast.makeText(getBaseContext(), "" + keysForLists, Toast.LENGTH_LONG).show();
                        if (keysForLists.size()>1) {
                            chosenListData = db.listDataDao().getChosenList(1);
                            chosenList = chosenListData.getList_name();
                            Log.d(TAG, "Chosen list: " + chosenList);
                        }
                        setNavigationDrawerData();
                        setDepartmentsData();
                        setKeysForDepartments();
                    }

                });*/
        //Toast.makeText(getBaseContext(), "xxx:" + keysForLists, Toast.LENGTH_LONG).show();

        keysForLists.clear();
        Log.d(TAG, "keysforlists cleaned");
       // if (db.listDataDao().getAllNamesNotFlowable().size() > 0) {
            keysForLists.addAll(db.listDataDao().getAllNamesNotFlowable());
            Log.d(TAG, "keysforlists added information from base " + keysForLists);
       // }
        //Toast.makeText(getBaseContext(), "" + keysForLists, Toast.LENGTH_LONG).show();
        if (keysForLists.size()>1) {
            chosenListData = db.listDataDao().getChosenList(1);
            //chosenList = chosenListData.getList_name();


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



    public void setData() {
        //TextView text = view.findViewById(R.id.tvDepartmentsName);
        List<String> listToAdd = departmentsData.get(chosenDepartment).getData();


        data.clear();
        //Toast.makeText(view.getContext(), "" + listToAdd, Toast.LENGTH_LONG).show();
        data.add("Добавить");

        if (listToAdd != null) {
            data.addAll(listToAdd);
        }

        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
    }

    public void getData() {
       /* List<String> listToAdd = listData.get(chosenList).get(chosenDepartment).getData();
        data.clear();
        data.add("Добавить");
        if (listToAdd != null)  data.addAll(listToAdd);

        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();*/
        Log.d(TAG, "In getData" + data);
        data.clear();
        Log.d(TAG, "Data clear" );
       // List<String> temp = new ArrayList<>(db.dataDao().getAllNames(chosenDepartmentData.department_id));
      //  if (db.dataDao().getAllNames(chosenDepartmentData.department_id) != null) data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
        if (keysForDepartments.size() > 0) data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
        Log.d(TAG, "Added elements to data array: " + data);
        if (data.size() == 0) {
            Data dataForInsert = new Data(chosenDepartmentData.department_id, 0, "Добавить");
            db.dataDao().insert(dataForInsert);
            Log.d(TAG, "Added first element to data: " + dataForInsert.getAllInString());
            data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
        }

        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
        Log.d(TAG, "adapters notified");
    }

    void setDepartmentsData() {

      /*  if (listData.size() > 1) {
            departmentsData.clear();
            listData.get(chosenList).put("Добавить", null);
            departmentsData.put("Добавить", null);
            if (listData.get(chosenList) != null) departmentsData.putAll(listData.get(chosenList));
            adapter.notifyDataSetChanged();
            adapterForDepartments.notifyDataSetChanged();
        }*/

        if (keysForLists.size() > 1) {
            //departmentsData.clear();
            DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 0,  "Добавить", 0);
           /* departmentData.list_id = chosenListData.list_id;
            departmentData.department_position = 0;
            departmentData.department_name = "Добавить";
            departmentData.CrossOutNumber = 0;*/
            db.departmentDataDao().insert(departmentData);
            //departmentsData.putAll(db.departmentDataDao().getAllNames(chosenListData.list_id));
            Log.d(TAG, "items departments data added");
        }
        Log.d(TAG, "in departments data");
    }

    //!!!void getCrossOutNumber(String key) {
    void getCrossOutNumber() {
        //!! crossOutNumber = crossOutNumbersArray.get(key);
        if (keysForLists.size() > 1) {
            //crossOutNumber = listData.get(chosenList).get(chosenDepartment).getCrossOutNumber();
            crossOutNumber = chosenDepartmentData.CrossOutNumber;
        }
    }

    //!!!void setCrossOutNumber(String key) {
    void setCrossOutNumber() {
        //!!crossOutNumbersArray.put(key,crossOutNumber);
        if (keysForLists.size() > 1) {
            //listData.get(chosenList).get(chosenDepartment).setCrossOutNumber(crossOutNumber);
            chosenDepartmentData.CrossOutNumber = crossOutNumber;
            db.departmentDataDao().update(chosenDepartmentData);
        }
        }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(),0, "Copy");
        menu.setHeaderTitle("Copy text"); //setting header title for menu
//        TextView textView = (TextView) v; // calling our textView
        EditText editText = (EditText) v;
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", editText.getText());
        manager.setPrimaryClip(clipData);
    }*/

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.id.et_menu, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("title");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.et_menu:
                break;
        }
        return true;
    }*/

    @Override
    public void onItemClick(View view, final int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        //String resName = (String) view.getResources().getResourceName(position);
        // adapterForDepartments.notifyDataSetChanged();
        View parent = (View) view.getParent();
        int parentID = parent.getId();

        //ImageView mDeleteImage = view.findViewById(R.id.image_delete);

        //Toast.makeText(this, "" + parentID + " " + R.id.image_delete, Toast.LENGTH_SHORT).show();
        switch (parentID) {
            case R.id.ll:
                //Toast.makeText(this, " DELETE " + parentID, Toast.LENGTH_SHORT).show();
                deleteSingleItem(position);
                break;
            case R.id.depLl:
                deleteSingleItemInDepartments(position);
                break;
            case R.id.rvDepartments:
                //view.setBackgroundColor(Color.parseColor("#00ff00"));
                if (position == 0) {
                    crossOutNumber = 0;
                    inputTextDialogWindow(view, 1, position, parentID);
                } else /*if (editButtonClicked == 0) */ {

                    //TextView text = view.findViewById(R.id.tvDepartmentsName);
                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position,chosenListData.list_id);
                    Log.d(TAG, "Chosen department: " + chosenDepartmentData.getAllInString());
                    chosenDepartment = chosenDepartmentData.department_name;
                   // Log.d(TAG, "Chosen department name: " + chosenDepartment);
                    //chosenDepartment = text.getText().toString();
                      //  Log.d(TAG, "Chosen department name: " + chosenDepartmentData.department_name);
                    getData();
                    //!!getCrossOutNumber(chosenDepartment);
                    getCrossOutNumber();

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
                    inputTextDialogWindow(view, 1, position, parentID);
                } else if (editButtonClicked == 1) {


                    if (position < (data.size() - crossOutNumber)) {
                        crossOutNumber++;
                        //!!  setCrossOutNumber(chosenDepartment);
                        setCrossOutNumber();
                        moveSingleItem(position);
                        //saveDataWhenItChanged();
                    } else {
                        crossOutNumber--;
                        //setCrossOutNumber(chosenDepartment);
                        setCrossOutNumber();
                        moveSingleItemToTop(position);
                        //saveDataWhenItChanged();
                    }
                } else {
                    deleteFlagForEdit = true;
                    chosenData = db.dataDao().getChosenData(position,chosenDepartmentData.department_id);
                    inputTextDialogWindow(view, position, position, parentID);

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
        et = popupView.findViewById(R.id.popup_edit);



       // registerForContextMenu(et);


        //et.setFocusableInTouchMode(true);
       /* et.setTextIsSelectable(false);
        et.measure(-1,-1);
        et.setTextIsSelectable(true);*/
        //registerForContextMenu(et);




        /*et.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(et.getText());
                Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/


       //Log.d(TAG, "chosen data clicked id: " + chosenData.data_id + " name: " + chosenData.data_name);
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

        } else {
            deleteFlagForEdit = false;
        }


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String str = et.getText().toString();
                // if (!str.isEmpty()) {
                String str = et.getText().toString();
                if (uniqueTest(str,parentID))  {
                    buttonClicked(str);
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }
            }

            void buttonClicked(String str) {

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                       // removeSingleItem(position, parentID);
                       db.dataDao().updateSingleItem(chosenData.data_id, str);
                       getData();
                       //adapter.notifyDataSetChanged();
                    } else {
                        insertFromPopup(str, insertIndex, parentID, view);
                    }
                    popupWindow.dismiss();
                    if (parentID == R.id.rvAnimals) {
                        //saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {

                        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                        chosenDepartment = chosenDepartmentData.department_name;
                        getData();
                      //  chosenDepartment = str;
                      //  setNavigationDrawerData();
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
            public void onClick(View v) {
                //String str = et.getText().toString();
                // if (!str.isEmpty()) {
                String str = et.getText().toString();
                if (uniqueTest(str,parentID))  {
                    buttonClicked(str);
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }
            }

            void buttonClicked(String str) {

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        db.dataDao().updateSingleItem(chosenData.data_id, str);
                        getData();
                        //adapter.notifyDataSetChanged();
                    } else {
                        insertFromPopup(str, insertIndex, parentID, view);
                    }
                    et.getText().clear();
                    if (parentID == R.id.rvAnimals) {
                     //  saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {
                        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                        chosenDepartment = chosenDepartmentData.department_name;
                        getData();
                       // chosenDepartment = str;
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
                            final InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });

        //InputMethodManager imm =  (InputMethodManager) getSystemService(popupView.getContext().INPUT_METHOD_SERVICE);
        //imm.showSoftInput(popupView, InputMethodManager.SHOW_IMPLICIT);

        et.requestFocus();
       /* et.setImeOptions(et.getImeOptions() | EditorInfo.IME_ACTION_DONE);
        //et.setTextIsSelectable(true);
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

                 /*   String str = et.getText().toString();
                    if (uniqueTest(str,parentID)) {
                        buttonClicked(str);

                    } else {
                        Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                    }


                    handled = true;
                }
                return handled;

            }

            void buttonClicked(String str) {

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        removeSingleItem(position, parentID);
                    }
                    insertFromPopup(str, insertIndex, parentID);
                    popupWindow.dismiss();
                    if (parentID == R.id.rvAnimals) {
                        //saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {
                        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                        chosenDepartment = chosenDepartmentData.department_name;
                        getData();
                    }
                }
            }
        });*/


    }

    public void  inputTextDialogWindow(final View view, final int insertIndex, final int position, final int parentID) {

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
               // .create();
                .show();

        /*if (position != 0) {
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

        } else {
            deleteFlagForEdit = false;
        }*/

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (uniqueTest(str,parentID))  {
                     InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str,insertIndex,parentID, view);
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
                if (uniqueTest(str,parentID))  {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str,insertIndex,parentID, view);
                    et.getText().clear();
                    et.setHint("Введите сообщение");
                    dialog.setTitle("Добавить");
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }

            }

        });

       /* builder1.setNeutralButton(
                "Next",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String str = et.getText().toString();
                        if (uniqueTest(str,parentID))  {
                            //buttonClicked(str);
                            inputButtonClicked(str,insertIndex,parentID);
                        } else {
                            Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    void buttonClicked(String str) {

                        if (!str.isEmpty()) {
                            if (deleteFlagForEdit) {
                                deleteFlagForEdit = false;
                                db.dataDao().updateSingleItem(chosenData.data_id, str);
                                getData();
                                //adapter.notifyDataSetChanged();
                            } else {
                                insertFromPopup(str, insertIndex, parentID);
                            }
                            et.getText().clear();
                            if (parentID == R.id.rvAnimals) {
                                //  saveDataWhenItChanged();
                            } else if (parentID == R.id.rvDepartments) {
                                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                                chosenDepartment = chosenDepartmentData.department_name;
                                getData();
                                // chosenDepartment = str;
                            }
                        }
                    }

                });

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String str = et.getText().toString();
                        if (uniqueTest(str,parentID))  {
                            //buttonClicked(str);
                            inputButtonClicked(str,insertIndex,parentID);
                        } else {
                            Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }




                });

        /*builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteFlagForEdit = false;
                        dialog.cancel();
                    }
                });*/
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

        //InputMethodManager imm =  (InputMethodManager) getSystemService(popupView.getContext().INPUT_METHOD_SERVICE);
        //imm.showSoftInput(popupView, InputMethodManager.SHOW_IMPLICIT);

        et.requestFocus();
        //et.setTextIsSelectable(true);
       /* et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

                /*    String str = et.getText().toString();
                    if (uniqueTest(str,parentID))  {
                        inputButtonClicked(str,insertIndex,parentID, view);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                    }

                    handled = true;
                }
                return handled;
            }

            /*void buttonClicked(String str) {

                if (!str.isEmpty()) {
                    if (deleteFlagForEdit) {
                        deleteFlagForEdit = false;
                        removeSingleItem(position, parentID);
                    }
                    insertFromPopup(str, insertIndex, parentID);
                    //popupWindow.dismiss();
                    if (parentID == R.id.rvAnimals) {
                        //saveDataWhenItChanged();
                    } else if (parentID == R.id.rvDepartments) {
                        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                        chosenDepartment = chosenDepartmentData.department_name;
                        getData();
                    }
                }
            }*/
        //});

    }

    private void inputButtonClicked(String str, int insertIndex, int parentID, View view){
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
                chosenDepartment = chosenDepartmentData.department_name;
                getData();
            }
        }
    }

    boolean uniqueTest(String str, int parentID){
        return ((!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str) && parentID == R.id.rvDepartments)
                || parentID == R.id.rvAnimals
                || (!db.listDataDao().getAllNamesNotFlowable().contains(str) && parentID == R.id.material_drawer_recycler_view));
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

    /*public void saveDataWhenItChanged() {
        ArrayList<String> forClone = new ArrayList<String>(data);
        forClone.remove(0);
        DataWithCrossOutNumber forCloneWithCrossOut = new DataWithCrossOutNumber(forClone, crossOutNumber);
        //forClone.clone(data);
        //!!!departmentsData.put(chosenDepartment, forClone);
        //Toast.makeText(getBaseContext(), "chosen: " + chosenDepartment + " data: " + departmentsData.get(chosenDepartment), Toast.LENGTH_LONG).show();
        listData.get(chosenList).put(chosenDepartment, forCloneWithCrossOut);
    }*/


    private void setNewDepartment(String s){
       /* LinkedHashMap<String, DataWithCrossOutNumber> newmap = (LinkedHashMap<String, DataWithCrossOutNumber>) listData.get(chosenList).clone();
        listData.get(chosenList).clear();
        listData.get(chosenList).put("Добавить", null);
        listData.get(chosenList).put(s, new DataWithCrossOutNumber(null,0));
        listData.get(chosenList).putAll(newmap);*/

        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 1, s, 0);
        /*departmentData.CrossOutNumber = 0;
        departmentData.department_name = s;
        departmentData.department_position = 1;
        departmentData.list_id = chosenListData.list_id;*/
        db.departmentDataDao().incrementValues(chosenListData.list_id,0);
        //db.departmentDataDao().setDobavitInZero(chosenListData.list_id);
        db.departmentDataDao().insert(departmentData);
        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1,chosenListData.list_id);
        Log.d(TAG, "method: 'setNewDepartment'; departments data after insert new in db: " + db.departmentDataDao().getAllNames(chosenListData.list_id));
    }

    private void setNewDepartmentFromParse(String s, int position){
       /* LinkedHashMap<String, DataWithCrossOutNumber> newmap = (LinkedHashMap<String, DataWithCrossOutNumber>) listData.get(chosenList).clone();
        listData.get(chosenList).clear();
        listData.get(chosenList).put("Добавить", null);
        listData.get(chosenList).put(s, new DataWithCrossOutNumber(null,0));
        listData.get(chosenList).putAll(newmap);*/

        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, position, s, 0);
        /*departmentData.CrossOutNumber = 0;
        departmentData.department_name = s;
        departmentData.department_position = 1;
        departmentData.list_id = chosenListData.list_id;*/
        //db.departmentDataDao().incrementValues(chosenListData.list_id,0);
        //db.departmentDataDao().setDobavitInZero(chosenListData.list_id);
        db.departmentDataDao().insert(departmentData);
        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position,chosenListData.list_id);
        Log.d(TAG, "method: 'setNewDepartmentFromParse'; departments data after insert new in db: " + db.departmentDataDao().getAllNames(chosenListData.list_id));
    }

    private void setNewData(String s, int position) {
        Data newData = new Data(chosenDepartmentData.department_id, position,s);
        db.dataDao().incrementValues(chosenDepartmentData.department_id,position-1);

       // db.dataDao().setDobavitInZero(chosenDepartmentData.department_id);
        db.dataDao().insert(newData);
        Log.d(TAG, "Test increment data position: " + db.dataDao().getAllPositions(chosenDepartmentData.department_id));
        Log.d(TAG, "new data set");
    }

    private void setNewList(String s){
      /* LinkedHashMap<String, LinkedHashMap<String, DataWithCrossOutNumber>> newmap =
               (LinkedHashMap<String, LinkedHashMap<String, DataWithCrossOutNumber>>) listData.clone();
        listData.clear();
        listData.put("Добавить", null);
        listData.put(s, new LinkedHashMap<String, DataWithCrossOutNumber>());
        listData.putAll(newmap);*/

       // ListDataDatabase db = App.getInstance().getDatabase();
            ListData listData = new ListData();
            listData.setList_name(s);
            listData.list_position = 1;
        db.listDataDao().incrementValues();
       // db.listDataDao().setDobavitInZero();
        db.listDataDao().insert(listData);

        chosenListData = db.listDataDao().getChosenList(1);
       // Single.fromCallable(() -> InsertListInAnotherThread(listData)).subscribeOn(Schedulers.io()).subscribe();
    }

    private int InsertListInAnotherThread(ListData listData) {
        db.listDataDao().incrementValues();
        db.listDataDao().setDobavitInZero();
        db.listDataDao().insert(listData);

        chosenListData = db.listDataDao().getChosenList(1);

        return 0;
    }

    private void insertFromPopup(String s, int insertIndex, int parentId, View view) {
        //int insertIndex = 1;
        switch (parentId) {
            case R.id.rvDepartments:
               //!! keysForDepartments.add(insertIndex, s);
                //listData.get(chosenList).put(s, new DataWithCrossOutNumber(null,0));
                setNewDepartment(s);
                setKeysForDepartments();
                //chosenDepartment = s;
                //chosenDepartmentData = db.departmentDataDao().getChosenDepartment()
//                setDepartmentsData();
                // departmentsData.put(s, null);
                //crossOutNumbersArray.put(s, 0);
                //Toast.makeText(this, "" + departmentsData.get(s), Toast.LENGTH_LONG).show();
                adapterForDepartments.notifyItemInserted(insertIndex);
                Log.d(TAG,"in rvDepartments, adapter notified. Chosen department");
                break;
            case R.id.rvAnimals:
                data.add(insertIndex, s);
                setNewData(s, insertIndex);
               // listData.get(chosenList).get(chosenDepartment).getData().add(insertIndex,s);
                adapter.notifyItemInserted(insertIndex);
                break;
            case R.id.material_drawer_recycler_view:
                //keysForLists.add(insertIndex, s);
                parser(s);
               // setNewList(s);
                setKeysForLists();
                setKeysForDepartments();
                if (keysForDepartments.size()>1) {
                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1,chosenListData.list_id);
                    chosenDepartment = chosenDepartmentData.department_name;
                    getData();
                } else {


                    //chosenList = s;
                    //chosenList = chosenListData.getList_name();
                    //setNavigationDrawerData();
                    //!!!!! setDepartmentsData();
                    //!!!!! setKeysForDepartments();
                    data.clear();
Log.d(TAG, "data clear");

                    final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                            //.setTitle(title)
                            .setMessage("Заполнить списком по умолчанию?")
                            .setCancelable(true)
                            //.setView(et)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setDefaultList();
                                            setKeysForDepartments();
                                            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1,chosenListData.list_id);
                                            chosenDepartment = chosenDepartmentData.department_name;

                                            getData();
                                            dialog.cancel();
                                        }
                                    })
                            //.setNeutralButton("Следующее", null)
                            .setNegativeButton(
                                    "Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            // .create();
                            .show();
                   // InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                   // imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
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
           // Log.d(TAG, "default data keys: " + key);
        DepartmentData dpData = new DepartmentData(chosenListData.list_id,departmentPosition,key,0);
        db.departmentDataDao().insert(dpData);
        chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition,chosenListData.list_id);
        getData();
        int dataPosition = 1;
            Log.d(TAG, "all default data: " + defaultData.get(key));
            for (String s : defaultData.get(key)) {
                //Log.d(TAG, "default data: " + s);
                Data data = new Data(chosenDepartmentData.department_id,dataPosition,s);
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


    private void removeSingleItem(int removeIndex, int parentId) {
        // int removeIndex = 2;
        switch (parentId) {
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

        Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        int pos = db.dataDao().getAllPositions(chosenDepartmentData.department_id).size();
        db.dataDao().updateSingleItemPosition(temp.data_id,pos);
        db.dataDao().decrementValues(chosenDepartmentData.department_id,fromPosition);
        //getData();
        // notify adapter
        adapter.notifyItemMoved(fromPosition, pos-1);
        adapter.notifyItemChanged(pos-1);
    }

    private void moveSingleItemToTop(int fromPosition) {
        // int fromPosition = 3;
        int toPosition = 1;

        // update data array
        String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

        Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        db.dataDao().incrementValuesFromOneToPosition(chosenDepartmentData.department_id,fromPosition);
        //int pos = db.dataDao().getAllPositions(chosenDepartmentData.department_id).size();
        db.dataDao().updateSingleItemPosition(temp.data_id,1);
        //db.dataDao().decrementAllValuesExceptFirst(chosenDepartmentData.department_id);
        //getData();


        // notify adapter
        adapter.notifyItemMoved(fromPosition, toPosition);
        adapter.notifyItemChanged(toPosition);
    }
   /* public void setCrossOutNumberInActivity(int mCrossOutNumber) {
        this.crossOutNumber = mCrossOutNumber;
    }*/


    private void deleteSingleItem(int position) {
        if (position >= (data.size() - crossOutNumber)) {
            crossOutNumber--;
            //!!setCrossOutNumber(chosenDepartment);
            setCrossOutNumber();
        }
        if (position > 0) {

            // remove your item from data base
            data.remove(position);  // remove the item from list
            db.dataDao().deleteSingleData(position,chosenDepartmentData.department_id);
            db.dataDao().decrementValues(chosenDepartmentData.department_id,position);
            //Log.d(TAG, "data after deleting single item: " + db.dataDao().getAllNames(chosenDepartmentData.department_id) + db.dataDao().getAllPositions(chosenDepartmentData.department_id));
            adapter.notifyItemRemoved(position); // notify the adapter about the removed item
            //saveDataWhenItChanged();

        }
    }


    private void deleteSingleItemInDepartments(int position) {
        if (position > 0 && keysForDepartments.size()>2) {
            //TextView text = view.findViewById(R.id.tvDepartmentsName);
            //chosenDepartment = text.getText().toString();
          //  String text = keysForDepartments.get(position);
           // Toast.makeText(getBaseContext(), "" + textStr, Toast.LENGTH_LONG).show();
            // remove your item from data base
            //!!departmentsData.remove(chosenDepartment);  // remove the item from list
          //  listData.get(chosenList).remove(text);
           // adapterForDepartments.notifyItemRemoved(position); // notify the adapter about the removed item
            db.departmentDataDao().deleteSingleData(position,chosenListData.list_id);
            db.departmentDataDao().decrementValues(chosenListData.list_id,position);
           // setDepartmentsData();
            setKeysForDepartments();
           //! keysForDepartments.remove(chosenDepartment);


           /* if (position == 1 && text == chosenDepartment) {
                chosenDepartment = keysForDepartments.get(1);
            } else if (text == chosenDepartment) {
                chosenDepartment = keysForDepartments.get(position-1);
            }*/
           //if (position == 1) {
               chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position,chosenListData.list_id);
               //crossOutNumber = chosenDepartmentData.CrossOutNumber;
            getCrossOutNumber();
            chosenDepartment = chosenDepartmentData.department_name;
           //} e

            /*int index = 0;
            for (String key : keysForDepartments) {
                if (index == 1) {
                    chosenDepartment = key;
                    //Toast.makeText(getBaseContext(), "" + chosenDepartment, Toast.LENGTH_SHORT).show();
                    break;
                }
                index++;

            }*/

            getData();


        } else if (position > 0) {
           // String text = keysForDepartments.get(position);
           // listData.get(chosenList).remove(text);
            db.departmentDataDao().deleteSingleData(position,chosenListData.list_id);
            //db.departmentDataDao().decrementValues(chosenListData.list_id,position);
crossOutNumber = 0;
            data.clear();
           // setDepartmentsData();
            setKeysForDepartments();

        }
        adapter.notifyDataSetChanged();
        adapterForDepartments.notifyDataSetChanged();
    }

    private void parser(String inputText){
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
                  //  Log.d(TAG, "parser set if list name: " + listName);
                } else {
                    boolean nameSetStatus = true;
                    int copiesCounter = 0;
                    String tempListName = listName;
                    do {
                        if (!db.listDataDao().getAllNamesNotFlowable().contains(tempListName)) {
                            listName += " (" + copiesCounter + ")";
                            setNewList(listName);
                           // Log.d(TAG, "parser set else list name: " + listName);
                            nameSetStatus = false;
                        } else {
                            copiesCounter++;
                            tempListName = listName + " (" + copiesCounter + ")";
                        }
                    } while (nameSetStatus);
                }
               //Log.d(TAG+ " parser", "parser: list name: " + listName);
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
                  //  Log.d(TAG, "parser set if department name: " + departmentName);
                } else {
                    boolean nameSetStatus = true;
                    int copiesCounter = 0;
                    String tempDepartmentName = departmentName;
                    do {
                        if (!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(tempDepartmentName)) {
                            departmentName += " (" + copiesCounter + ")";
                            setNewDepartmentFromParse(departmentName, position);
                           // Log.d(TAG, "parser set else department name: " + departmentName);
                            nameSetStatus = false;
                        } else {
                            copiesCounter++;
                            tempDepartmentName = departmentName + " (" + copiesCounter + ")";
                           // Log.d(TAG, "parser set else temporary department name: " + departmentName + " copies counter: " + copiesCounter);
                        }
                    } while (nameSetStatus);
                }

                //Log.d(TAG+ " parser", "parser: department name: " + departmentName);
                departmentName = "";
                index++;
                position++;
                getData();
                continue;
            } else if (s.equals("]") && index == 1) {
                //Log.d(TAG, "parser: data name: " + dataName);
                index--;
                continue;
            }else if (index == 1){
                departmentName += s;
            }

            if (s.equals("]") && index == 2) {
                if (!dataName.isEmpty()) setNewData(dataName,dataPosition);

                Log.d(TAG+ " parser", "parser: data name: " + dataName + " position: " + dataPosition);
                dataName = "";
                index--;
                dataPosition = 1;
            } else if (s.equals(";") && index == 2){
                if (!dataName.isEmpty()) setNewData(dataName,dataPosition);
                Log.d(TAG + " parser", "parser: data name: " + dataName + " position: " + dataPosition + " index: " + index);
                dataName = "";
                dataPosition++;
            } else if (index == 2){
                dataName += s;
            }
        }

        if (!listName.isEmpty()) setNewList(listName);
        if (!departmentName.isEmpty()) setNewDepartmentFromParse(departmentName, position);
        if (!dataName.isEmpty()) {
            getData();
            setNewData(dataName, dataPosition);
        }

    //Log.d(TAG, "parser answer: list name '" + listName + "' department name '" + departmentName + "' data name '" + dataName + "'");
    //listToStringGenerator();
    }

    private String listToStringGenerator() {
        String stringToSend = "";
        Log.d(TAG, "In listToStringGenerator()");
        stringToSend += chosenListData.getList_name() + "[";
        Log.d(TAG, "In stringToSend += chosenListData.getList_name()");
        int departmentPosition = 1;
        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size()>1) for (String s : db.departmentDataDao().getAllNames(chosenListData.list_id)) {
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);
            stringToSend += s + "[";
            int dataCounter = 0;
            for (String dataS : db.dataDao().getAllNamesForGenerator(chosenDepartmentData.department_id)) {
                stringToSend += dataS + ";";
                dataCounter++;
            }
            if (dataCounter>0) stringToSend = stringToSend.substring(0,stringToSend.length()-1);
            stringToSend += "]";

        }
        stringToSend += "]";
        Log.d(TAG, "StringToSend ready " + stringToSend);
        //Log.d(TAG, "generate list to string result: " + stringToSend);
        return stringToSend;
    }

}


