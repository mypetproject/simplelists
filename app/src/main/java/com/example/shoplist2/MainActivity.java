package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, DepartmentsAdapter.ItemClickListener {

    List<Data> data;

    static MyRecyclerViewAdapter adapter;
    int crossOutNumber;
    static boolean editButtonClicked = true;
    boolean deleteFlagForEdit;
    static int adapterPosition;
    int dataPosition;

    // DepartmentsAdapter // adapterForDepartments;
    List<String> keysForDepartments;
    List<DepartmentData> listOfDepartmentsData;
    List<String> keysForLists;

    private Drawer drawerResult = null;
    //private boolean newListCancelled = false;
    private int selectedListIndex = 2;

    Toolbar toolbar;

    static ListDataDatabase db;
    static ListData chosenListData = new ListData();
    static DepartmentData chosenDepartmentData;
    static Data chosenData = new Data();
    int parentID;

    // ImageButton mShareButton;
    EditText et;
    static boolean canUpdate;

    RecyclerView recyclerViewDepartments;
    static RecyclerView recyclerView;
    static ViewPagerAdapter viewPagerAdapter;
    static ViewPager2 myViewPager2;
    static TabLayout tabLayout;

    //for OnItemTouch
    private static final int MAX_CLICK_DURATION = 250;
    private static long startClickTime;
    private static long clickDuration;

    // static boolean //dontTouchMLowButton = false;

    private static final String TAG = "myLogs";

    private ArrayList<String> arrayList = new ArrayList<>();
    private static List<Data> adapterListData;
    ImageButton addDepartmentButton;
    ImageButton moreMenuButton;

    static MainActivity mn;
    static boolean editFlag;
    static boolean stopClick = false;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mn = MainActivity.this;

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
        listOfDepartmentsData = new ArrayList<>();
      /*  //Single.fromCallable(() example
                Single.fromCallable(() -> db.listDataDao().insert(listData)).subscribeOn(Schedulers.io()).subscribe();
*/

        // set up the RecyclerView
      /*  recyclerView = findViewById(R.id.rvAnimals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        adapter = new MyRecyclerViewAdapter(this, data);

        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);*/

        Log.d(TAG, "recyclerViewDepartments started ");
      /*  recyclerViewDepartments = findViewById(R.id.rvDepartments);
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
        // adapterForDepartments = new DepartmentsAdapter(this, keysForDepartments, data.size());
        // adapterForDepartments.setClickListener(this);
        /*while (recyclerViewDepartments.getItemDecorationCount() > 0) {
            recyclerViewDepartments.removeItemDecorationAt(0);
        }*/
      /*  recyclerViewDepartments.setAdapter(// adapterForDepartments);
        layoutManagerDepartments.setOrientation(LinearLayoutManager.HORIZONTAL);
        Log.d(TAG, "recyclerViewDepartments ended ");
        //recyclerView.setNestedScrollingEnabled(false);
        //recyclerViewDepartments.setNestedScrollingEnabled(false);*/

        if (keysForDepartments.size() > 1) {
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            getListOfDepartmentsData();
            Log.d(TAG, "chosenDepartmentData  ended ");
            getCrossOutNumber();
            Log.d(TAG, "getCrossOutNumber();  ended ");
            //  getData();
            Log.d(TAG, "getData();  ended ");
        }

    /*    arrayList.add("Item 1");
        arrayList.add("Item 2");
        arrayList.add("Item 3");
        arrayList.add("Item 4");
        arrayList.add("Item 5");*/

        myViewPager2 = findViewById(R.id.viewpager);
        // viewPagerAdapter = new ViewPagerAdapter(this, data, listOfDepartmentsData);
        viewPagerAdapter = new ViewPagerAdapter(this);
        Log.d(TAG, "onCreate MyAdapter(this, arrayList);");

        // myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        // Log.d(TAG, " myViewPager2.setOrientation ended");
        myViewPager2.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, myViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.d(TAG, "onConfigureTab");

                DepartmentData currentDepartment = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_tab, null);
                // ImageView tabImageView = view.findViewById(R.id.tabImageView);
                ///tabImageView.setImageResource(R.id.tabImageView);
                TextView textView = (TextView) view.findViewById(R.id.tvDepartmentsName);
                TextView textViewQty = (TextView) view.findViewById(R.id.tvDepartmentsQty);

                int activeItem = db.dataDao().getAllNames(currentDepartment.department_id).size() - currentDepartment.CrossOutNumber - 1;

                textView.setText(db.departmentDataDao().getAll(chosenListData.list_id).get(position).department_name);

                if (activeItem != 0) {
                    textViewQty.setText(String.valueOf(activeItem));
                } else {
                    textViewQty.setVisibility(View.GONE);
                }
                tab.setCustomView(view);
                /*LinearLayout tabsll = (LinearLayout) findViewById(R.id.tabs_linear_layout);
                if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() == 0) {
                    tabsll.setVisibility(View.GONE);
                } else {
                    tabsll.setVisibility(View.VISIBLE);
                }*/

                // tab.setIcon(R.id.tabImageView);
                //View v = LayoutInflater.from(tabLayout.getContext()).inflate(R.layout.custom_tub, null);
                // Log.d(TAG,"LayoutInflater.from");
                //v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                // TextView textView = (TextView) v.findViewById(R.id.tv_tab);
                // textView.setText("Tab " + (position+1));
                // tab.setCustomView(v);
                // Log.d(TAG," tabLayout.getTabAt(position).setCustomView(v)");
                // tab.setText("Tab " + (position+1));
                //   tab.setText(listOfDepartmentsData.get(position).department_name);
                // tab.setText(db.departmentDataDao().getAll(chosenListData.list_id).get(position).department_name);

            }
        }).attach();

        setTabsOnLongClickListener();

        /*tabLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Long click at " + v.getVerticalScrollbarPosition(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/

        Log.d(TAG, "onCreate myViewPager2.setAdapter(viewPagerAdapter);");

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
                //todo возможно поменять
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
                            //recyclerViewDepartments.smoothScrollToPosition(position + 1);
                        }
                        break;

                    case ItemTouchHelper.END:
                        if (position > 1) {
                            chosenDepartmentData = db.departmentDataDao()
                                    .getChosenDepartment(chosenDepartmentData.department_position - 1, chosenListData.list_id);
                            //recyclerViewDepartments.smoothScrollToPosition(position - 1);
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

                // adapterForDepartments.notifyItemMoved(position_dragged, position_target);

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


     /*   mShareButton = (ImageButton) findViewById(R.id.share_button);

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

*/
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
                    // recyclerViewDepartments.smoothScrollToPosition(position - 1);
                }
                getCrossOutNumber();
                getData();

            }

            public void onSwipeLeft() {
                position = keysForDepartments.indexOf(chosenDepartmentData.department_name);
                if (position < (keysForDepartments.size() - 1)) {
                    chosenDepartmentData = db.departmentDataDao()
                            .getChosenDepartment(chosenDepartmentData.department_position + 1, chosenListData.list_id);
                    // recyclerViewDepartments.smoothScrollToPosition(position + 1);
                }
                getCrossOutNumber();
                getData();

            }

            public void onSwipeBottom() {
            }
        });

       /* Log.d(TAG, "OnCreate() R.id.etAnimalCount start");
        EditText et = (EditText) findViewById(R.id.etAnimalCount);
        Log.d(TAG, "OnCreate() R.id.etAnimalCount findViewById(R.id.etAnimalCount)");
                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {

                                db.dataDao().updateQty(dataPosition, chosenDepartmentData.department_id, Float.parseFloat(et.getText().toString()));
                            }
                        }
                    });

       /* EditText et = (EditText) findViewById(R.id.etAnimalCount);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
db.dataDao().updateQty(dataPosition, chosenDepartmentData.department_id, Float.parseFloat(str));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
        addDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo переделать inputTextDialogWindow?
                inputTextDialogWindow(v, 1, 0);
                Log.d(TAG, "addDepartmentButton id" + v.toString());
            }
        });

        setTabsVisibility();

        moreMenuButton = (ImageButton) findViewById(R.id.more_menu_button);
        if (db.listDataDao().getAllNames().size() < 2) {
            moreMenuButton.setVisibility(View.GONE);
        }

    }

    private void setTabsVisibility() {
        LinearLayout tabsll = (LinearLayout) findViewById(R.id.tabs_linear_layout);
        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() == 0
                && editButtonClicked) {
            tabsll.setVisibility(View.GONE);
        } else {
            tabsll.setVisibility(View.VISIBLE);
        }
    }

    private void setTabsOnLongClickListener() {
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {

            // Set LongClick listener to each Tab
            int finalI = i;
            tabStrip.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    View parent = (View) v.getParent().getParent();

                    menuForDepartments(v, finalI);
                    Log.d(TAG, "onLongClick tab view: " + R.id.tabs + " == " + parent.getId());
                    //Toast.makeText(getApplicationContext(), "Tab clicked at " + finalI, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private void menuForDepartments(View view, int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        int id = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id).department_id;
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        //popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.department_popup_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.show();
        //   Log.d(TAG, name + " popup.menu created" + parentID);
        //View parentView = (View) view.getParent();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.department_menu_delete:
                        // deleteSingleItem(position, id);
                        deleteSingleItemInDepartments(position);
                        // Toast.makeText(getBaseContext(), "delete " + finalI, Toast.LENGTH_SHORT).show();
                        return true;
                    //Toast.makeText(getBaseContext(), "delete", Toast.LENGTH_SHORT).show();
                    //todo доделать
                    case R.id.department_menu_edit:
                        //  Toast.makeText(getBaseContext(), "edit " + finalI, Toast.LENGTH_SHORT).show();
                      /*!  deleteFlagForEdit = true;
                        chosenData = db.dataDao().getChosenData(dataPosition, chosenDepartmentData.department_id);
                        Log.d(TAG, name + " data was chosen" + parentID);
                        inputTextDialogWindow(parentView, dataPosition, dataPosition);
                        Log.d(TAG, name + "inputTextDialogWindow(view, dataPosition, dataPosition, parentID) done");*/

                        //inputTextDialogWindowForViewHolderItem(view, position, id);
                        // inputTextDialogWindow(view, position, position);
                        editDepartmentDialogWindow(view, position, position);
                        Log.d(TAG, name + " view name: " + view.getParent().toString());
                        return true;
                    case R.id.department_menu_move:
                        //Toast.makeText(getBaseContext(), "move", Toast.LENGTH_SHORT).show();
                        //createDataMoveSubMenu(parentView);
                        createDepartmentMoveSubMenu(view, id, position);
                        // Toast.makeText(getBaseContext(), "move " + finalI, Toast.LENGTH_SHORT).show();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    private void createDepartmentMoveSubMenu(View view, int id, int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        // popup.setOnMenuItemClickListener(this);
        //popup2.inflate(R.menu.data_popup_menu);
        // popup2.setForceShowIcon(true);
        Log.d(TAG, name + " popup = new PopupMenu");
        // int depID = db.dataDao().getDepartmentIdByDataId(id);
        // int listID = db.departmentDataDao().getDepartmentDataById(depID).list_id;
        int listID = db.departmentDataDao().getDepartmentDataById(id).list_id;

        Log.d(TAG, name + " listID: " + listID);
        if (db.listDataDao().getAll().size() > 2) for (ListData s : db.listDataDao().getAll()) {
            if (s.list_id != chosenListData.list_id) popup.getMenu().add(s.getList_name());
        }
        else {
            Toast.makeText(this, "Слишком мало списков", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, name + " for (DepartmentData s");
        popup.show();
        Log.d(TAG, "createDataMoveSubMenu popup set");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                View parent = (View) view.getParent();
               /* TextView text = parent.findViewById(R.id.tvAnimalName);
                EditText dataQty = parent.findViewById(R.id.etAnimalCount);
                Log.d(TAG, name + " ext.getText().toString() listID: " + text.getText().toString());
                Data newData = new Data(
                        db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(),
                                listID).department_id,
                        1,
                        text.getText().toString(),
                        Float.parseFloat(dataQty.getText().toString()));
                Log.d(TAG, name + " newData = new Data");*/
                //   TextView text = parent.findViewById(R.id.tvDepartmentsName);
                DepartmentData departmentData = db.departmentDataDao().getDepartmentDataById(id);
                ListData toList = db.listDataDao().getChosenListByName(item.getTitle().toString());
                Log.d(TAG, name + " toList " + toList.getList_name());
                departmentData.list_id = toList.list_id;
                int oldPosition = departmentData.department_position;
                departmentData.department_position = 0;
                boolean nameSetStatus = true;
                int copiesCounter = 0;
                String tempDepartmentName = departmentData.department_name;
                do {
                    if (!db.departmentDataDao().getAllNames(toList.list_id).contains(tempDepartmentName)) {
                        if (copiesCounter > 0)
                            departmentData.department_name += " (" + copiesCounter + ")";
                        // setNewList(listName);
                        nameSetStatus = false;
                    } else {
                        copiesCounter++;
                        tempDepartmentName = departmentData.department_name + " (" + copiesCounter + ")";
                    }
                } while (nameSetStatus);
                db.departmentDataDao().incrementAllValues(toList.list_id);
                db.departmentDataDao().update(departmentData);
                db.departmentDataDao().decrementValues(chosenListData.list_id, oldPosition);
                viewPagerAdapter.notifyDataSetChanged();
                setNavigationDrawerData();
               /*deleteSingleItem(position, id);
                db.dataDao().incrementValues(
                        db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(),
                                listID).department_id, 0);
                db.dataDao().insert(newData);*/
                // Single.fromCallable(() -> notifyWithDelay(500)).subscribeOn(Schedulers.io()).subscribe();
                return false;
            }
        });
    }

    static void hideTab(int position) {

        //   tabLayout.removeTab(tabLayout.getTabAt(position));

        ((LinearLayout) tabLayout.getTabAt(position).view).setVisibility(View.GONE);
        //TabLayout.Tab tab = tabLayout.getTabAt(position+1);
        //tab.select();
        //View view =  tabLayout.getTabAt(position).getCustomView();
        //  tabLayout.removeTab(tabLayout.getTabAt(position));
        //view.setVisibility(View.GONE);
        //  view.setLayoutParams(new RecyclerView.LayoutParams(0,0));
//View parent = (View) tabLayout.getParent();
//parent.setVisibility(View.GONE);
        //To hide the first tab
        //((LinearLayout) parent.getTabAt(position).view).setVisibility(View.GONE);
        //Set the next  tab as selected tab
        // TabLayout.Tab tab = tabLayout.getTabAt(0);
        //  tab.select();

    }

    static void showTab(int position) {

        ((LinearLayout) tabLayout.getTabAt(position).view).setVisibility(View.VISIBLE);

        //To hide the first tab
        //((ViewGroup) tabLayout.getChildAt(position)).getChildAt(position).setVisibility(View.VISIBLE);
        //Set the next  tab as selected tab
        // TabLayout.Tab tab = tabLayout.getTabAt(0);
        //  tab.select();

    }

    private void getListOfDepartmentsData() {
        listOfDepartmentsData = db.departmentDataDao().getAll(chosenListData.list_id);
    }

    /*public void setAdapter(MyRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }*/

    public static void ViewPagerItemClicked(View view, int id, MyRecyclerViewAdapter adapter, int position, List<Data> adapterData) {

        Log.d(TAG, "Clicked id: " + id);
        // MyRecyclerViewAdapter myRecyclerViewAdapter = (MyRecyclerViewAdapter) recyclerView.getAdapter();
        setAdapter(adapter);
        //focusedChild = myViewPager2.getCurrentItem();
//Log.d(TAG, "focusedChild = " + myViewPager2.getCurrentItem());
        // setRecyclerView(recyclerView);
        setAdapterData(adapterData);
        if (editButtonClicked) {


            DepartmentData temp = db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id));
            if (position < (db.dataDao().getAllNames(db.dataDao().getDepartmentIdByDataId(id)).size()
                    - db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).CrossOutNumber)) {
                temp.CrossOutNumber++;
                moveItemToBottom(id, position);
                Log.d(TAG, "moveItemToBottom");
            } else {

                db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).CrossOutNumber--;
                temp.CrossOutNumber--;
                moveItemToTop(id, position);
                Log.d(TAG, " moveItemToTop");
            }
            db.departmentDataDao().update(temp);
            //viewPagerAdapter.notifyItemChanged(position);

            //viewPagerAdapter.notifyItemChanged(myViewPager2.getCurrentItem());
            TabLayout.Tab tab = tabLayout.getTabAt(myViewPager2.getCurrentItem());
            View tabView = tab.getCustomView();
            TextView textViewQty = (TextView) tabView.findViewById(R.id.tvDepartmentsQty);
            //TextView textView = (TextView) tabView.findViewById(R.id.tvDepartmentsName);
            Log.d(TAG, "TextView textView ");
            int activeItem = db.dataDao().getAllNames(db.dataDao().getDepartmentIdByDataId(id)).size()
                    - db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).CrossOutNumber - 1;
            Log.d(TAG, "int activeItem");

            // textView.setText(db.departmentDataDao().getAll(chosenListData.list_id).get(myViewPager2.getCurrentItem()).department_name);
            //  textView.setText(db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).department_name);
            Log.d(TAG, " textView.setText");
            if (activeItem != 0) {
                textViewQty.setVisibility(View.VISIBLE);
                textViewQty.setText(String.valueOf(activeItem));
            } else {
                textViewQty.setVisibility(View.GONE);
            }
            Log.d(TAG, " if (activeItem != 0) ");
            tab.setCustomView(tabView);
            Log.d(TAG, "tab.setCustomView(tabView);");
            // viewPagerAdapter.notifyItemChanged(myViewPager2.getCurrentItem());
            //   Single.fromCallable(() -> notifyWithDelay(400, position)).subscribeOn(Schedulers.io()).subscribe();
            //Log.d(TAG, "crossOutNumber: " + db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).CrossOutNumber);
            Log.d(TAG, "viewPagerAdapter.notifyItemChanged(position);: " + position);
        } else {
            switch (view.getId()) {
                case R.id.image_more:
                    dataHolderMenuItemButtonClick(view, position, id);
                    break;
                default:
                    inputTextDialogWindowForViewHolderItem(view, position, id);

            }

        }

    }

    public static void viewPagerOnTouchListener(View view, MotionEvent event, int id, MyRecyclerViewAdapter adapter, List<Data> adapterData) {
        // Log.d(TAG, "item " + db.dataDao().getChosenDataById(id).data_name + " touched");
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started " + event.toString());
        setAdapter(adapter);
        setAdapterData(adapterData);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //  view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                //Log.d(TAG, "parent: " + view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().toString());
                //view.getParent().requestDisallowInterceptTouchEvent(true);
                clickDuration = 0;
                startClickTime = Calendar.getInstance().getTimeInMillis();
                Log.d(TAG, name + " ACTION_DOWN start " + event.getAction() + " clickDuration: " + clickDuration);

                switch (view.getId()) {
                    case R.id.image_to_low:

                        if (!stopClick)
                            Single.fromCallable(() -> itemCount(false, id, view)).subscribeOn(Schedulers.io()).subscribe();
                      /*  if (clickDuration == 0) {
                            mTimer.schedule(mMyTimerTask, 500, 500);
                        }*/
                        break;
                    case R.id.image_to_high:
                        if (!stopClick)
                            Single.fromCallable(() -> itemCount(true, id, view)).subscribeOn(Schedulers.io()).subscribe();
                        break;
                }
                // Single.fromCallable(() -> db.listDataDao().insert(listData)).subscribeOn(Schedulers.io()).subscribe();
                // Single.fromCallable(() -> itemCount(true, position)).subscribeOn(Schedulers.io()).subscribe();

                break;
            }
            case MotionEvent.ACTION_UP:
                // view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                //view.getParent().requestDisallowInterceptTouchEvent(false);
                clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                Log.d(TAG, name + " ACTION_UP start " + event.getAction() + " clickDuration: " + clickDuration);
                //   if (clickDuration < MAX_CLICK_DURATION) {
                //click event has occurred
                //dontTouchMLowButton = false;
                //  Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_SHORT).show();

                //  }
                adapterListData.clear();
                adapterListData.addAll(db.dataDao().getAll(
                        db.dataDao().getDepartmentIdByDataId(id)
                ));
//adapter.notifyItemChanged(db.dataDao().getChosenDataById(id).data_position);
                adapter.notifyDataSetChanged();
                // getData();
                break;
            case MotionEvent.ACTION_CANCEL:
                clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                //dontTouchMLowButton = false;
                //onItemTouch(view, null,position);
                //getData();
                adapterListData.clear();
                adapterListData.addAll(db.dataDao().getAll(
                        db.dataDao().getDepartmentIdByDataId(id)
                ));
//adapter.notifyItemChanged(db.dataDao().getChosenDataById(id).data_position);
                adapter.notifyDataSetChanged();
                break;
        }
        //  return 0;

    }

    private static int itemCount(boolean sign, int id, View view) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        stopClick = true;
        while (clickDuration == 0) {
            Log.d(TAG, name + " sign: " + sign);
            if (sign) {
                db.dataDao().plusQty(id);

            } else {
                db.dataDao().minusQty(id);
            }
            Log.d(TAG, name + " data saved: ");
            mn.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, name + "  runOnUiThread ");
                    //todo popup menu над элементом вмесо toaster?
                    Toast toast = Toast.makeText(view.getContext(), "" + db.dataDao().getChosenDataById(id).data_qty, Toast.LENGTH_SHORT);
                    CountDownTimer toastCountDown;
                    toastCountDown = new CountDownTimer(100, 10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            toast.show();
                        }

                        @Override
                        public void onFinish() {
                            toast.cancel();
                        }
                    };
                    toast.show();
                    toastCountDown.start();
                    Log.d(TAG, name + "  data get ");
                }
            });
            // Log.d(TAG, name + " get data");
            SystemClock.sleep(200);
        }
        stopClick = false;
        return 0;
    }

    private static void moveItemToTop(int id, int fromPosition) {
        List<Data> oldTemp = new ArrayList<>();
        oldTemp.addAll(adapterListData);
        db.dataDao().incrementValuesFromOneToPosition(db.dataDao().getDepartmentIdByDataId(id), fromPosition);
        db.dataDao().updateSingleItemPosition(id, 1);
        adapterListData.clear();
        adapterListData.addAll(db.dataDao().getAll(
                db.dataDao().getDepartmentIdByDataId(id)
        ));
        //adapter.notifyItemMoved(fromPosition, 1);
        ProductDiffUtilCallback productDiffUtilCallback =
                new ProductDiffUtilCallback(oldTemp, adapterListData);
        DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);

        //adapter.setData(productList);
        productDiffResult.dispatchUpdatesTo(adapter);
        adapter.notifyItemChanged(1);
    }

    private static void moveItemToBottom(int id, int position) {
        List<Data> oldTemp = new ArrayList<>();
        oldTemp.addAll(adapterListData);
        int pos = db.dataDao().getAllPositions(db.dataDao().getDepartmentIdByDataId(id)).size();
        db.dataDao().updateSingleItemPosition(db.dataDao().getChosenDataById(id).data_id, pos);

        db.dataDao().decrementValues(db.dataDao().getDepartmentIdByDataId(id), position);

        adapterListData.clear();
        adapterListData.addAll(db.dataDao().getAll(
                db.dataDao().getDepartmentIdByDataId(id)
        ));
        //  adapter.notifyItemMoved(position, pos - 1);
        // adapter.notifyItemChanged(pos - 1);


        //  Collections.swap(temp, position_dragged, position_target);

        // Log.d(TAG, " Collections.swap");
        //adapter.notifyItemMoved(position_target, position_dragged);

        ProductDiffUtilCallback productDiffUtilCallback =
                new ProductDiffUtilCallback(oldTemp, adapterListData);
        DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);

        //adapter.setData(productList);
        productDiffResult.dispatchUpdatesTo(adapter);
        adapter.notifyItemChanged(pos - 1);
    }

    private static void inputTextDialogWindowForViewHolderItem(View view, int position, int id) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        final EditText et = new EditText(view.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        String title;
        editFlag = true;
        if (position != 0) {
            et.setText(db.dataDao().getChosenDataById(id).data_name + " ");
            Log.d(TAG, name + " et.setText(text.getText().toString()");
            et.setSelection(et.length());
            title = "Редактировать";
        } else {
            editFlag = false;
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
                                // deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .show();
        Log.d(TAG, name + "AlertDialog end building");
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Log.d(TAG, name + "positiveButton start setOnClickListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, name + " positiveButton started onClick title: " + editFlag);
                String str = et.getText().toString();
                // if (uniqueTest(str, view)) {
                // InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                //  imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (editFlag) {
                    editOldData(str, id);
                } else {
                    if (position == 0) {
                        setNewData(str, 1, id);
                    } else {
                        setNewData(str, position, id);
                    }
                }
                Log.d(TAG, name + "positiveButton ended setNewData");

                adapterListData.clear();
                adapterListData.addAll(db.dataDao().getAll(
                        db.dataDao().getDepartmentIdByDataId(id)
                ));
                Log.d(TAG, name + " adapterListData.addAll");
                /*if (!editFlag) {

                    adapter.notifyItemInserted(1);
                    viewPagerAdapter.notifyItemChanged(1);
                } else {
                    adapter.notifyItemChanged(position);
                }*/
                viewPagerAdapter.notifyDataSetChanged();
                dialog.dismiss();
              /*  } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название", Toast.LENGTH_SHORT).show();
                }*/
                Log.d(TAG, name + "positiveButton ended onClick");
            }

        });
        Log.d(TAG, name + "positiveButton end setOnClickListener");
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
               /* if (uniqueTest(str, v)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, view);
                    et.getText().clear();
                    et.setHint("Введите сообщение");
                    dialog.setTitle("Добавить");
               /* } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }
*/
                if (!str.isEmpty()) {
                    boolean tempEditFlag = editFlag;
                    if (editFlag) {
                        editOldData(str, id);
                        Log.d(TAG, " editFlag = false; start");
                        editFlag = false;
                        Log.d(TAG, " editFlag = false; end");
                        dialog.setTitle("Добавить");
                    } else {
                        // setNewData(str, 1, id);
                        if (position == 0) {
                            setNewData(str, 1, id);
                        } else {
                            setNewData(str, position, id);
                        }

                    }
                    et.setText("");
                    Log.d(TAG, name + " neutralButton ended setNewData");

                    adapterListData.clear();
                    adapterListData.addAll(db.dataDao().getAll(
                            db.dataDao().getDepartmentIdByDataId(id)
                    ));
                    Log.d(TAG, name + " adapterListData.addAll");
              /*  if (!tempEditFlag) {
                    if (position == 0) {
                        adapter.notifyItemInserted(1);
                        viewPagerAdapter.notifyItemChanged(1);
                      //  viewPagerAdapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemInserted(position);
                        viewPagerAdapter.notifyItemChanged(myViewPager2.getCurrentItem());
                    }

                } else {


                    adapter.notifyItemChanged(position);
                }*/
                    viewPagerAdapter.notifyDataSetChanged();
                }
                Log.d(TAG, name + " neutralButton ended");
            }

        });

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus && et.isEnabled() && et.isFocusable()) {
                    et.post(new Runnable() {
                        @Override
                        public void run() {
                            final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });
        Log.d(TAG, name + "et start et.requestFocus()");
        et.requestFocus();
        Log.d(TAG, name + "et end et.requestFocus()");
        Log.d(TAG, name + " ended");
    }

    private static void editOldData(String str, int id) {
        Data tempData = db.dataDao().getChosenDataById(id);
        tempData.data_name = str;
        db.dataDao().update(tempData);
        Log.d(TAG, "editOldData ended");
    }


    private static void setAdapterData(List<Data> adapterData) {
        adapterListData = adapterData;
    }


    public static void setAdapter(MyRecyclerViewAdapter getAdapter) {
        adapter = getAdapter;
    }

    public static void setRecyclerView(RecyclerView getRecyclerView) {
        recyclerView = getRecyclerView;
    }

    private void newShare(View view, String stringToShare) {


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Поделиться"));


    }

    private int activeQtyForList(int position) {
        List<DepartmentData> listOfDepartmentsData = new ArrayList<DepartmentData>(db.departmentDataDao().getAll(db.listDataDao().getChosenList(position).list_id));
        int sumOfActive = 0;
        for (DepartmentData dd : listOfDepartmentsData) {
            sumOfActive += MainActivity.db.dataDao().getAll(dd.department_id).size() - dd.CrossOutNumber - 1;
        }
        // Log.d(TAG, " activeQtyForList sum: " + sumOfActive);

        return sumOfActive;
    }

    void setNavigationDrawerData() {
        Log.d(TAG, "setNavigationDrawerData() started");
        IDrawerItem[] iDrawerItems = new IDrawerItem[1000];
        for (int i = 0; i < keysForLists.size(); i++) {
            int activeQty = activeQtyForList(i);
            if (activeQty > 0 && i != 0) {
                //todo заменить keysForLists на запрос из базы
                iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
            } else {
                iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
            }
            // Log.d(TAG, "items for drawer added " + keysForLists.get(i - 1));
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
                            inputTextDialogWindow(view, 1, position - 1);
                            // drawerResult.setSelection(selectedListIndex,false);

                        } else {
                            setActiveList(position);
                            selectedListIndex = position;
                            // recyclerViewDepartments.smoothScrollToPosition(1);
                            // drawerResult.setSelection(selectedListIndex,false);
                            myViewPager2.setCurrentItem(0);
                        }
                        Log.d(TAG, "view drawer id: " + view.toString());
                        setNavigationDrawerData();
                        setTabsOnLongClickListener();

                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        //todo как обновить количество активных позиций в открытом меню или когда slide
                        //  setNavigationDrawerData();
                        if (activeQtyForList(drawerResult.getCurrentSelectedPosition() - 1) > 0) {
                            drawerResult.updateBadge(drawerResult.getCurrentSelectedPosition() - 1, new StringHolder(activeQtyForList(drawerResult.getCurrentSelectedPosition() - 1) + ""));
                            drawerResult.setSelection(selectedListIndex - 1, false);
                        } else {
                            drawerResult.updateBadge(drawerResult.getCurrentSelectedPosition() - 1, null);
                            drawerResult.setSelection(selectedListIndex - 1, false);
                        }
                        Log.d(TAG, "drawerView.getVerticalScrollbarPosition() = " + drawerResult.getCurrentSelectedPosition());
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                     /*   if (activeQtyForList(drawerResult.getCurrentSelectedPosition() - 1) > 0) {
                            drawerResult.updateBadge(drawerResult.getCurrentSelectedPosition() - 1, new StringHolder(activeQtyForList(drawerResult.getCurrentSelectedPosition() - 1) + ""));
                            drawerResult.setSelection(selectedListIndex - 1, false);
                        }*/
                    }
                })
                /* .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                     @Override
                     public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                         // Toast.makeText(getBaseContext(), " longclick on position: " + position , Toast.LENGTH_SHORT).show();
                         String name = new Object() {
                         }.getClass().getEnclosingMethod().getName();
                         PopupMenu popup = new PopupMenu(view.getContext(), view);
                         //popup.setOnMenuItemClickListener(this);
                         popup.inflate(R.menu.popup_menu);
                         popup.setForceShowIcon(true);
                         popup.show();

                         if (editButtonClicked) {
                             popup.getMenu().findItem(R.id.menu_edit).setTitle("Редактировать список");
                         } else {
                             popup.getMenu().findItem(R.id.menu_edit).setTitle("Закончить редактирование");
                         }

                         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                             @Override
                             public boolean onMenuItemClick(MenuItem item) {
                                 switch (item.getItemId()) {
                                     case R.id.menu_share:
                                         String stringToSend = listToStringGenerator(position - 1);
                                         newShare(item.getActionView(), stringToSend);
                                         return true;
                                     case R.id.menu_edit:
                                         //addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
                                         if (editButtonClicked) {
                                             editButtonClicked = false;
                                             addDepartmentButton.setVisibility(View.VISIBLE);
                                         } else {
                                             editButtonClicked = true;
                                             addDepartmentButton.setVisibility(View.GONE);
                                         }
                                         viewPagerAdapter.notifyDataSetChanged();
                                         // adapter.notifyDataSetChanged();
                                         // adapterForDepartments.notifyDataSetChanged();
                                         return true;
                                     case R.id.menu_delete:
                                         final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                                                // .setMessage("Удалить список '" + chosenListData.getList_name() + "'?")
                                                 .setMessage("Удалить список '" + db.listDataDao().getChosenList(position-1).getList_name() + "'?")
                                                 .setCancelable(true)
                                                 .setPositiveButton("Да",
                                                         new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 deleteSingleItemInList(position - 1);
                                                                //drawerResult.removeItem(position);


                                                                drawerResult.removeItemByPosition(position);
                                                              //  drawerResult.getAdapter().notifyItemRemoved(position-1);
                                                             //           Log.d(TAG, name + " drawerResult.getOriginalDrawerItems() " + drawerResult.getDrawerItems());
                                                                // drawerResult.getAdapter().notifyAdapterItemRemoved(position);
                                                               //  drawerResult.getAdapter().notifyAdapterDataSetChanged();


                                                                 dialog.cancel();
 //drawerResult.getDrawerLayout().closeDrawer(GravityCompat.START);
                                                               //  setNavigationDrawerData();
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

                                         return false;
                                     default:
                                         return false;
                                 }
                             }
                         });
                         return false;
                     }
                 })*/
                .addDrawerItems(iDrawerItems)

                .build();
        //       Log.d(TAG, "setNavigationDrawerData() drawerBuilderEnded");
//drawerResult.setSelection(1);
        //drawerResult.setSelection(iDrawerItems[1]);

        if (keysForLists.size() > 1) {
            setTitle(chosenListData.getList_name());
            drawerResult.setSelection(selectedListIndex - 1, false);
            Log.d(TAG, " if (keysForLists.size() > 1) pos: " + drawerResult.getCurrentSelectedPosition());

        } else {
            setTitle("<- Нажмите");
        }
        Log.d(TAG, "setNavigationDrawerData() ended");
    }

    void setActiveList(int position) {
        if (keysForLists.size() > 1) {
            chosenListData = db.listDataDao().getChosenList(position - 1);
            Log.d(TAG, "method: 'setNavigationDrawerData()'; " + chosenListData.getAllInString());
            setTitle(chosenListData.getList_name());
            if (selectedListIndex != 2) selectedListIndex--;
            setNavigationDrawerData();
            /*if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 1) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            } else {
                chosenDepartmentData = new DepartmentData(chosenListData.list_id, 0, "", 0);
            }*/
            //recyclerViewDepartments.scrollToPosition(1);
        }
        //setDepartmentsData();
        // setKeysForDepartments();
     /*   if (keysForDepartments.size() < 2) {
            crossOutNumber = 0;
            data.clear();
            adapter.notifyDataSetChanged();
            // adapterForDepartments.notifyDataSetChanged();
        } else {
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            getCrossOutNumber();
            getData();
        }*/
        setTabsVisibility();
        viewPagerAdapter.notifyDataSetChanged();
        Log.d(TAG, "setActiveList(int position) ended");
    }

    @Override
    public void onBackPressed() {

        // Close Navigation Drawer by press button "Back" if it is in open condition

        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();

            // Go to first element of ViewPager by press button "Back" if it is position >0

        } else if (myViewPager2.getCurrentItem() != 0) {
            Log.d(TAG, "onBackPressed() position " + myViewPager2.getCurrentItem());
            myViewPager2.setCurrentItem(0);

        } else {
            super.onBackPressed();
        }

    }

    public static void setAdapterPosition(int pos) {
        adapterPosition = pos;
    }

    public void setKeysForDepartments() {
       /* keysForDepartments.clear();
        // Log.d(TAG, "keysForDepartments clear");
        keysForDepartments.addAll(db.departmentDataDao().getAllNames(chosenListData.list_id));
        Log.d(TAG, "method: 'setKeysForDepartment'; departments keys names: " + keysForDepartments);*/
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
      /*  mShareButton = (ImageButton) findViewById(R.id.share_button);
        if (keysForLists.size() < 2) {
            mShareButton.setVisibility(View.GONE);
        } else {
            mShareButton.setVisibility(View.GONE);
        }*/
        setNavigationDrawerData();
        Log.d(TAG, name + " setNavigationDrawerData() ended");
        setDepartmentsData();
        Log.d(TAG, name + " setDepartmentsData(); ended");
        //   setKeysForDepartments();
        Log.d(TAG, name + " setKeysForDepartments(); ended");
    }


    public void getData() {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        canUpdate = false;
        data.clear();
        Log.d(TAG, name + " data.clear(); ended");
        if (keysForDepartments.size() > 0) {
            //  data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
            data.addAll(db.dataDao().getAll(chosenDepartmentData.department_id));
        }
        if (data.size() == 0) {
            Data dataForInsert = new Data(chosenDepartmentData.department_id, 0, "Добавить", 0);
            db.dataDao().insert(dataForInsert);
            Log.d(TAG, "Added first element to data: " + dataForInsert.getAllInString());
            // data.addAll(db.dataDao().getAllNames(chosenDepartmentData.department_id));
            data.addAll(db.dataDao().getAll(chosenDepartmentData.department_id));
        }

        //adapter.notifyDataSetChanged();
        // adapterForDepartments.notifyDataSetChanged();
        canUpdate = true;
    }

    void setDepartmentsData() {
       /* if (keysForLists.size() > 1) {
            DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 0, "Добавить", 0);
            db.departmentDataDao().insert(departmentData);
            Log.d(TAG, "items departments data added");
        }*/
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
    public void onItemClick(View view, final int position, int id) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        View parent = (View) view.getParent();
        parentID = parent.getId();
        int viewID = view.getId();

        boolean clicker = false;
        Log.d(TAG, name + " viewID: " + viewID + " R.id.tvAnimalCount:" + R.id.tvAnimalCount);
        // Log.d(TAG,name + " view name: " + getResources().getResourceName(view.getId()));
        switch (viewID) {
            //todo по longclick прибавлять или вычитать числа, пока не отпустят
            case R.id.image_to_low:
                //!    db.dataDao().minusQty(position, chosenDepartmentData.department_id);
                getData();
                clicker = true;
                break;
            case R.id.image_to_high:
                //!   db.dataDao().plusQty(position, chosenDepartmentData.department_id);
                getData();
                clicker = true;
                break;
            case R.id.tvAnimalCount:
                clicker = true;
                break;
            case R.id.etAnimalCount:
                Log.d(TAG, name + " R.id.etAnimalCount start");
                clicker = true;
                break;
        }

        if (!clicker) switch (parentID) {
            case R.id.ll:
                dataPosition = position;
                parentID = R.id.rvAnimals;
                //dataHolderMenuItemButtonClick(view);
                break;
            case R.id.depLl:
                TextView text = parent.findViewById(R.id.tvDepartmentsName);
                final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setMessage("Удалить отдел '" + text.getText().toString() + "'?")
                        .setCancelable(true)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSingleItemInDepartments(position);
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

                break;
           /* case R.id.rvDepartments:

                if (position == 0) {
                    crossOutNumber = 0;
                    inputTextDialogWindow(view, 1, position, parentID);
                } else {
                    recyclerViewDepartments.smoothScrollToPosition(position);
                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
                    Log.d(TAG, "Chosen department: " + chosenDepartmentData.getAllInString());
                    getData();
                    getCrossOutNumber();
                }
                break;*/
            case R.id.rvAnimals:
                if (position == 0) {
                    inputTextDialogWindow(view, 1, position);
                } else if (editButtonClicked) {
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
                    inputTextDialogWindow(view, position, position);
                }
                // adapterForDepartments.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onItemTouch(View view, MotionEvent event, int id) {

    }

   /* @Override
    public int onItemTouch(View view, MotionEvent event, int position) {
      //  Toast.makeText(this, "item touched", Toast.LENGTH_SHORT).show();
        return 0;
    }
//todo временно скрыто
 /*   @Override
    public void onItemLongClick(int position, View view) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " Long click started");
       /* final int viewID = view.getId();
        switch (viewID) {
            case R.id.image_to_low:
                db.dataDao().minusQty(position,chosenDepartmentData.department_id);

                getData();
                Log.d(TAG, name + " Long click minus");
                break;
            case R.id.image_to_high:
                db.dataDao().plusQty(position,chosenDepartmentData.department_id);

                getData();
                Log.d(TAG, name + " Long click plus");
                break;
        }*/
    //Toast.makeText(view.getContext(), "Long click", Toast.LENGTH_SHORT).show();
    /*}*/
//todo временно скрыто
    //todo обновлять данные при удержании, action_down прерывается при вызове adapter.notify
    /*
    @Override
    public int onItemTouch(View view, MotionEvent event, int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started " + event.toString());
        //Toast.makeText(view.getContext(), "Touch event: " + event, Toast.LENGTH_SHORT).show();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //  view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                //Log.d(TAG, "parent: " + view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().toString());
                //view.getParent().requestDisallowInterceptTouchEvent(true);
                clickDuration = 0;
                startClickTime = Calendar.getInstance().getTimeInMillis();
                Log.d(TAG, name + " ACTION_DOWN start " + event.getAction() + " clickDuration: " + clickDuration);

                switch (view.getId()) {
                    case R.id.image_to_low:
                        Single.fromCallable(() -> itemCount(false, position, view)).subscribeOn(Schedulers.io()).subscribe();
                      /*  if (clickDuration == 0) {
                            mTimer.schedule(mMyTimerTask, 500, 500);
                        }*/
                 /*       break;
                    case R.id.image_to_high:
                        Single.fromCallable(() -> itemCount(true, position, view)).subscribeOn(Schedulers.io()).subscribe();
                        break;
                }
                // Single.fromCallable(() -> db.listDataDao().insert(listData)).subscribeOn(Schedulers.io()).subscribe();
                // Single.fromCallable(() -> itemCount(true, position)).subscribeOn(Schedulers.io()).subscribe();

           /*     break;
            }
           case MotionEvent.ACTION_UP:
                // view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                //view.getParent().requestDisallowInterceptTouchEvent(false);
                clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                Log.d(TAG, name + " ACTION_UP start " + event.getAction() + " clickDuration: " + clickDuration);
                if (clickDuration < MAX_CLICK_DURATION) {
                    //click event has occurred
                    //dontTouchMLowButton = false;
                    //  Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_SHORT).show();

                }
                getData();
                break;
            case MotionEvent.ACTION_CANCEL:
                clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                //dontTouchMLowButton = false;
                //onItemTouch(view, null,position);
                getData();
                break;
        }
        return 0;
    }*/


    public void inputTextDialogWindow(final View view, final int insertIndex, final int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();

        View parent = (View) view.getParent();
        parentID = parent.getId();
        View parentParent = (View) view.getParent().getParent();
        int parentParentID = parentParent.getId();


        final EditText et = new EditText(view.getContext());
        Log.d(TAG, name + " et start building");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        Log.d(TAG, name + " et end building");

        String title;
        if (position != 0 && parentID == R.id.rvAnimals) {
            TextView text = new TextView(view.getContext());
            Log.d(TAG, name + " TextView(view.getContext())");
            //  switch (parentID) {
            //      case R.id.rvAnimals:
            text = view.findViewById(R.id.tvAnimalName);
            Log.d(TAG, name + " text = view.findViewById(R.id.tvAnimalName)");
            //break;
               /* case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;*/
            //    }
            et.setText(text.getText().toString() + " ");
            Log.d(TAG, name + " et.setText(text.getText().toString()");
            et.setSelection(et.length());

            title = "Редактировать";
        } else if (parentParentID == R.id.tabs) {
            TextView text = new TextView(view.getContext());
            Log.d(TAG, name + " TextView(view.getContext())");
            //  switch (parentID) {
            //      case R.id.rvAnimals:
            text = view.findViewById(R.id.tvDepartmentsName);
            Log.d(TAG, name + " text = view.findViewById(R.id.tvAnimalName)");

               /* case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;*/
            //    }
            et.setText(text.getText().toString() + " ");
            Log.d(TAG, name + " et.setText(text.getText().toString()");
            et.setSelection(et.length());

            title = "Редактировать";
        } else {
            deleteFlagForEdit = false;
            title = "Добавить";
            et.setHint("Введите сообщение");
        }

        Log.d(TAG, name + "AlertDialog start building");
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
        Log.d(TAG, name + "AlertDialog end building");
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Log.d(TAG, name + "positiveButton start setOnClickListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (uniqueTest(str, view)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, view);
                    setTabsOnLongClickListener();

                    dialog.dismiss();
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название", Toast.LENGTH_SHORT).show();
                }

            }

        });
        Log.d(TAG, name + "positiveButton end setOnClickListener");
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (!str.isEmpty()) {
                    if (uniqueTest(str, view)) {
                        // InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        inputButtonClicked(str, insertIndex, view);
                        et.getText().clear();
                        et.setHint("Введите сообщение");
                        dialog.setTitle("Добавить");
                        setTabsOnLongClickListener();

                    } else {
                        Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                    }
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
        Log.d(TAG, name + "et start et.requestFocus()");
        et.requestFocus();
        Log.d(TAG, name + "et end et.requestFocus()");
    }

    public void editDepartmentDialogWindow(final View view, final int insertIndex, final int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();

     /*   View parent = (View) view.getParent();
        parentID = parent.getId();
        View parentParent = (View) view.getParent().getParent();
        int parentParentID = parentParent.getId();*/


        final EditText et = new EditText(view.getContext());
        Log.d(TAG, name + " et start building");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        Log.d(TAG, name + " et end building");

        String title;
       /* if (position != 0 && parentID == R.id.rvAnimals) {
            TextView text = new TextView(view.getContext());
            Log.d(TAG, name + " TextView(view.getContext())");
            //  switch (parentID) {
            //      case R.id.rvAnimals:
            text = view.findViewById(R.id.tvAnimalName);
            Log.d(TAG, name + " text = view.findViewById(R.id.tvAnimalName)");
            //break;
               /* case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;*/
        //    }
        /*    et.setText(text.getText().toString() + " ");
            Log.d(TAG, name + " et.setText(text.getText().toString()");
            et.setSelection(et.length());

            title = "Редактировать";
        } else if (parentParentID == R.id.tabs) {*/
        TextView text = new TextView(view.getContext());
        Log.d(TAG, name + " TextView(view.getContext())");
        //  switch (parentID) {
        //      case R.id.rvAnimals:
        text = view.findViewById(R.id.tvDepartmentsName);
        Log.d(TAG, name + " text = view.findViewById(R.id.tvAnimalName)");

               /* case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;*/
        //    }
        et.setText(text.getText().toString() + " ");
        Log.d(TAG, name + " et.setText(text.getText().toString()");
        et.setSelection(et.length());

        title = "Редактировать";
       /* } else {
            deleteFlagForEdit = false;
            title = "Добавить";
            et.setHint("Введите сообщение");
        }*/

        Log.d(TAG, name + "AlertDialog start building");
        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(title)
                //.setMessage("Write your message here")
                .setCancelable(true)
                .setView(et)
                .setPositiveButton("Ok", null)
                // .setNeutralButton("Следующее", null)
                .setNegativeButton(
                        "Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .show();
        Log.d(TAG, name + "AlertDialog end building");
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Log.d(TAG, name + "positiveButton start setOnClickListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (uniqueTest(str, view)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, view);
                    dialog.dismiss();
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название", Toast.LENGTH_SHORT).show();
                }

            }

        });
        Log.d(TAG, name + "positiveButton end setOnClickListener");
    /*    Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                if (uniqueTest(str, view)) {
                    // InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, view);
                    et.getText().clear();
                    et.setHint("Введите сообщение");
                    dialog.setTitle("Добавить");
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название отдела", Toast.LENGTH_SHORT).show();
                }

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
        Log.d(TAG, name + "et start et.requestFocus()");
        et.requestFocus();
        Log.d(TAG, name + "et end et.requestFocus()");
    }

    private void inputButtonClicked(String str, int insertIndex, View view) {
        View parent = (View) view.getParent();
        parentID = parent.getId();
        if (!str.isEmpty()) {
            if (deleteFlagForEdit) {
                deleteFlagForEdit = false;
                // removeSingleItem(position, parentID);
                //!  db.dataDao().updateSingleItem(chosenData.data_id, str);
                //!  getData();
            } else {
                insertFromPopup(str, insertIndex, view);
            }
            if (parentID == R.id.rvAnimals) {

            } /*else if (parentID == R.id.rvDepartments) {
                chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
                getData();
            }*/

        }
    }

    boolean uniqueTest(String str, View view) {
        View parent = (View) view.getParent();
        parentID = parent.getId();
        View parentParent = (View) view.getParent().getParent();
        int parentParentID = parentParent.getId();
        //return ((!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str) && parentID == R.id.rvDepartments) ||
        return (parentID == R.id.rvAnimals
                || (!db.listDataDao().getAllNamesNotFlowable().contains(str) && parentID == R.id.material_drawer_recycler_view)
                || (view.getId() == R.id.add_department_button && !db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str))
                || (parentParentID == R.id.tabs && !db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str))
                || (view.getId() == R.id.more_menu_button && !db.listDataDao().getAllNames().contains(str))
                || (view.getId() == R.id.add_department_button && !db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str))
        );
    }


    private void setNewDepartment(String s) {

        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 0, s, 0);
        db.departmentDataDao().incrementValues(chosenListData.list_id, 0);
        db.departmentDataDao().insert(departmentData);
        // chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
        Data data = new Data(
                db.departmentDataDao().getChosenDepartment(0, chosenListData.list_id).department_id,
                0, "Добавить", 0.0f);
        db.dataDao().insert(data);
        viewPagerAdapter.notifyDataSetChanged();

        Log.d(TAG, "method: 'setNewDepartment'; departments data after insert new in db: " + db.departmentDataDao().getAllNames(chosenListData.list_id));
    }

    private void setNewDepartmentFromParse(String s, int position) {
        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, position, s, 0);
        db.departmentDataDao().insert(departmentData);
        //chosenDepartmentData = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
        Data data = new Data(
                db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id).department_id,
                0, "Добавить", 0.0f);
        db.dataDao().insert(data);
        Log.d(TAG, "method: 'setNewDepartmentFromParse'; departments data after insert new in db: " + db.departmentDataDao().getAllNames(chosenListData.list_id));
    }

    private static void setNewData(String s, int position, int id) {
        Data newData = new Data(db.dataDao().getChosenDataById(id).department_id, position, s, 0.0f);
        db.dataDao().incrementValues(db.dataDao().getChosenDataById(id).department_id, position - 1);
        db.dataDao().insert(newData);
        //data.add(position, newData);
        //Log.d(TAG, "Test increment data position: " + db.dataDao().getAllPositions(chosenDepartmentData.department_id));
        Log.d(TAG, "new data set ended");
    }

    private static void setNewData(int position, String s, int departmentPosition, Float dataQty) {
        Log.d(TAG, "new data set started");
        Data newData = new Data(db.departmentDataDao().getChosenDepartment(departmentPosition - 1, chosenListData.list_id).department_id, position, s, dataQty);
        //Log.d(TAG, " Data newData = new Data ended");
        db.dataDao().incrementValues(db.departmentDataDao().getChosenDepartment(departmentPosition - 1, chosenListData.list_id).department_id, position - 1);
        db.dataDao().insert(newData);
        //data.add(position, newData);
        //Log.d(TAG, "Test increment data position: " + db.dataDao().getAllPositions(chosenDepartmentData.department_id));
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

    private void insertFromPopup(String s, int insertIndex, View view) {
        View parent = (View) view.getParent();
        parentID = parent.getId();
        View parentParent = (View) view.getParent().getParent();
        int parentParentID = parentParent.getId();

        if (view.getId() == R.id.add_department_button) {
            setNewDepartment(s);
        } else if (parentParentID == R.id.tabs) {
            int id = db.departmentDataDao().getChosenDepartment(insertIndex, chosenListData.list_id).department_id;
            editDepartment(s, id);
        }

        switch (parentID) {
           /* case R.id.rvDepartments:
                setNewDepartment(s);
                setKeysForDepartments();
                // adapterForDepartments.notifyItemInserted(insertIndex);
                Log.d(TAG, "in rvDepartments, adapter notified. Chosen department");
                break;*/
            case R.id.rvAnimals:
                //  data.add(insertIndex, s);
                //  data.add(insertIndex, s);
                //  data_qty.add(insertIndex,0.0f);
                //  setNewData(s, insertIndex);
                setNavigationDrawerData();
                adapter.notifyItemInserted(insertIndex);
                // adapterForDepartments.notifyItemChanged(chosenDepartmentData.department_position);
                break;
            case R.id.material_drawer_recycler_view:
                parser(s);
                setKeysForLists();
                setKeysForDepartments();
                selectedListIndex = 2;
                setNavigationDrawerData();
                // if (keysForDepartments.size() > 1) {
                if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 0) {
                    chosenDepartmentData = db.departmentDataDao().getChosenDepartment(0, chosenListData.list_id);
                    //getData();
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
                                            //getData();
                                            dialog.cancel();
                                            viewPagerAdapter.notifyDataSetChanged();

                                            addDepartmentButton.setVisibility(View.GONE);
                                            editButtonClicked = true;
                                            setNavigationDrawerData();
                                            setTabsVisibility();
                                        }
                                    })
                            .setNegativeButton(
                                    "Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            editButtonClicked = false;
                                            setTabsVisibility();
                                            addDepartmentButton.setVisibility(View.VISIBLE);
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                    Log.d(TAG, "dialog built");
                }
                viewPagerAdapter.notifyDataSetChanged();
                if (moreMenuButton.getVisibility() == View.GONE) {
                    moreMenuButton.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void editDepartment(String s, int id) {
        DepartmentData editedDepartment = db.departmentDataDao().getDepartmentDataById(id);
        editedDepartment.department_name = s;
        db.departmentDataDao().update(editedDepartment);
        viewPagerAdapter.notifyDataSetChanged();

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

        int departmentPosition = 0;
//Log.d(TAG,defaultData+"");
        for (String key : defaultData.keySet()) {
            DepartmentData dpData = new DepartmentData(chosenListData.list_id, departmentPosition, key, 0);
            db.departmentDataDao().insert(dpData);
            chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);

            //getData();
            Data dataForInsert = new Data(chosenDepartmentData.department_id, 0, "Добавить", 0);
            db.dataDao().insert(dataForInsert);
            int dataPosition = 1;
            Log.d(TAG, "all default data: " + defaultData.get(key));
            for (String s : defaultData.get(key)) {
                Data tempData = new Data(chosenDepartmentData.department_id, dataPosition, s, 0);
                db.dataDao().insert(tempData);
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
        //data.addAll(insertIndex, items);
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
        //  data.addAll(newList);

        // notify adapter
        adapter.notifyDataSetChanged();
    }

    private void updateSingleItem() {
        String newValue = "I like sheep.";
        int updateIndex = 3;
        // data.set(updateIndex, newValue);
        adapter.notifyItemChanged(updateIndex);
    }

    private void moveSingleItem(int fromPosition) {
        int toPosition = data.size() - 1;

        // update data array
       /* String item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);*/
        Data item = data.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);

       /* Float item_qty = data_qty.get(fromPosition);
        data_qty.remove(fromPosition);
        data_qty.add(toPosition, item_qty);*/

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
        // String item = data.get(fromPosition);
        Data item = data.get(fromPosition);
        //Float item_qty = data_qty.get(fromPosition);
        data.remove(fromPosition);
        data.add(toPosition, item);
        // data_qty.remove(fromPosition);
        // data_qty.add(toPosition, item_qty);

        Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        db.dataDao().incrementValuesFromOneToPosition(chosenDepartmentData.department_id, fromPosition);
        db.dataDao().updateSingleItemPosition(temp.data_id, 1);
        adapter.notifyItemMoved(fromPosition, toPosition);
        adapter.notifyItemChanged(toPosition);
    }

    private static void deleteSingleItem(int position, int id) {
        if (position >= (db.dataDao().getAllNames(db.dataDao().getDepartmentIdByDataId(id)).size() - db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).CrossOutNumber)) {
            DepartmentData temp = db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id));
            temp.CrossOutNumber--;
            db.departmentDataDao().update(temp);
        }
        //  Log.d(TAG,"deleteSingleItem started");
        if (position > 0) {
            int departmentID = db.dataDao().getDepartmentIdByDataId(id);
            db.dataDao().deleteSingleDataById(id);
            db.dataDao().decrementValues(departmentID, position);

            adapterListData.clear();
            adapterListData.addAll(db.dataDao().getAll(
                    departmentID
            ));

            adapter.notifyItemRemoved(position); // notify the adapter about the removed item
            viewPagerAdapter.notifyItemChanged(0);
            // Single.fromCallable(() -> notifyWithDelay(500)).subscribeOn(Schedulers.io()).subscribe();
        }
    }


    private void deleteSingleItemInDepartments(int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
      /*  if (position > 0 && keysForDepartments.size() > 2) {
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
        }*/
        db.departmentDataDao().deleteSingleData(position, chosenListData.list_id);
        db.departmentDataDao().decrementValues(chosenListData.list_id, position);
        //adapter.notifyDataSetChanged();
        viewPagerAdapter.notifyDataSetChanged();
        //   viewPagerAdapter.notifyItemChanged(position);
        //    viewPagerAdapter.notifyItemChanged(position);
        // adapterForDepartments.notifyDataSetChanged();
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
            // crossOutNumber = 0;
            // data.clear();
            setKeysForLists();
        }
        // adapter.notifyDataSetChanged();
        // adapterForDepartments.notifyDataSetChanged();
        setTabsVisibility();
        if (db.listDataDao().getAllNames().size() < 2) {
            moreMenuButton.setVisibility(View.GONE);
            //addDepartmentButton.setVisibility(View.GONE);
            LinearLayout tabsll = (LinearLayout) findViewById(R.id.tabs_linear_layout);
                tabsll.setVisibility(View.GONE);
        }
        viewPagerAdapter.notifyDataSetChanged();
    }

    private void deleteSingleItemInList(int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        //  int position = chosenListData.list_position;
        ListData listData = db.listDataDao().getChosenList(position);
        if (position > 0 && keysForLists.size() > 2) {
            db.listDataDao().deleteSingleItem(listData.list_id);
            db.listDataDao().decrementValues(listData.list_position);
            setKeysForLists();
            if (position != 1) {
                setActiveList(position);
            } else {
                setActiveList(position + 1);
            }
        } else if (position > 0) {
            db.listDataDao().deleteSingleItem(listData.list_id);
            // crossOutNumber = 0;
            // data.clear();
            setKeysForLists();
        }
        // adapter.notifyDataSetChanged();
        // adapterForDepartments.notifyDataSetChanged();
        //  viewPagerAdapter.notifyDataSetChanged();
    }

    private void parser(String inputText) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        inputText = parserFilter(inputText);
//Log.d(TAG,"\n" + inputText);
        String[] tokens = inputText.split("");
        String listName = "";
        String departmentName = "";
        String dataName = "";
        String dataQty = "";
        int index = 0;
        int position = 1;
        int departmentPosition = 0;
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
//Log.d(TAG," list name set: " + db.listDataDao().getChosenList(1).getList_name());


            if (s.equals("[") && index == 1) {
                if (departmentName.isEmpty()) departmentName = "Enter name";


                if (!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(departmentName)) {
                    setNewDepartmentFromParse(departmentName, departmentPosition);
                } else {
                    boolean nameSetStatus = true;
                    int copiesCounter = 0;
                    String tempDepartmentName = departmentName;
                    do {
                        if (!db.departmentDataDao().getAllNames(chosenListData.list_id).contains(tempDepartmentName)) {
                            departmentName += " (" + copiesCounter + ")";
                            setNewDepartmentFromParse(departmentName, departmentPosition);
                            nameSetStatus = false;
                        } else {
                            copiesCounter++;
                            tempDepartmentName = departmentName + " (" + copiesCounter + ")";
                        }
                    } while (nameSetStatus);
                }
                departmentName = "";
                index++;
                departmentPosition++;
                //getData();
                continue;
            } else if (s.equals("]") && index == 1) {
                index--;
                continue;
            } else if (index == 1) {
                departmentName += s;
            }

           /* if (s.equals("]") && index == 2) {

            } else if (s.equals(";") && index == 2) {

            } else*/
            if (s.equals("~") && index == 2) {
                index++;
                continue;
            } else if (index == 2) {
                dataName += s;
            }

            if (s.equals(";") && index == 3) {
                index = 2;
                if (!dataName.isEmpty())
                    setNewData(dataPosition, dataName, departmentPosition, Float.valueOf(dataQty));
                Log.d(TAG + " parser", "parser: data name: " + dataName + " position: " + dataPosition + " index: " + index);
                dataName = "";
                dataQty = "";
                dataPosition++;
            } else if (s.equals("]") && index == 3) {
                if (!dataName.isEmpty())
                    setNewData(dataPosition, dataName, departmentPosition, Float.valueOf(dataQty));
                Log.d(TAG + " parser", "parser: data name: " + dataName + " position: " + dataPosition);
                dataName = "";
                dataQty = "";
                //index--;
                index = 1;
                dataPosition = 1;
            } else if (index == 3) {
                dataQty += s;
            }
        }

        if (!listName.isEmpty()) setNewList(listName);
        if (!departmentName.isEmpty()) setNewDepartmentFromParse(departmentName, position);
        if (!dataName.isEmpty()) {
            // getData();
            setNewData(dataPosition, dataName, departmentPosition, Float.valueOf(dataQty));
        }
        Log.d(TAG, "Parser ended");
    }

    private String parserFilter(String s) {
        s = s.replace("-=***=-", "[");
        s = s.replace("-*-", "[");
        s = s.replace("---", "]");
        s = s.replace("--=*=--", "]");
        s = s.replace("\n", "");
        s = s.replace("-->", "~");

        return s;
    }


    private String listToStringGenerator() {
        String stringToSend = "";
        Log.d(TAG, "In listToStringGenerator()");
        //stringToSend += chosenListData.getList_name() + "[";
        stringToSend += chosenListData.getList_name() + "\n-=***=-\n";
        Log.d(TAG, "In stringToSend += chosenListData.getList_name()" + db.departmentDataDao().getAllPositions(chosenListData.list_id));

        int departmentPosition = 0;
        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 0)
            for (String s : db.departmentDataDao().getAllNames(chosenListData.list_id)) {
                //chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);
                int chosenDepartmentID = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id).department_id;
                // stringToSend += s + "[";
                stringToSend += s + "\n-*-\n";
                int dataCounter = 0;
                for (Data dataS : db.dataDao().getAllForGenerator(chosenDepartmentID)) {
                    Float data_qty_float = dataS.data_qty;
                    stringToSend += dataS.data_name + "-->" + data_qty_float.toString().replaceAll("\\.?0*$", "") + ";\n";
                    dataCounter++;
                    if (dataCounter == (db.dataDao().getAllForGenerator(chosenDepartmentID).size() - db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id).CrossOutNumber))
                        break;
                }
                if (dataCounter > 0)
                    stringToSend = stringToSend.substring(0, stringToSend.length() - 2);
                //stringToSend += "]";
                stringToSend += "\n---\n";
                departmentPosition++;
            }
        // stringToSend += "]";
        stringToSend += "--=*=--";
        Log.d(TAG, "StringToSend ready " + stringToSend);
        return generatorFilter(stringToSend);
    }

    private String generatorFilter(String s) {
        s = s.replace('[', '(');
        s = s.replace(']', ')');
        //s = s.replace(';',',');
        return s;
    }

    private String listToStringGenerator(int listPosition) {
        String stringToSend = "";
        Log.d(TAG, "In listToStringGenerator()");
        //stringToSend += chosenListData.getList_name() + "[";
        ListData listDataForSend = db.listDataDao().getChosenListByPosition(listPosition);
        stringToSend += listDataForSend.getList_name() + "\n-=***=-\n";
        Log.d(TAG, "In stringToSend += listDataForSend.getList_name()" + db.departmentDataDao().getAllPositions(listDataForSend.list_id));

        int departmentPosition = 0;
        if (db.departmentDataDao().getAllNames(listDataForSend.list_id).size() > 0)
            for (String s : db.departmentDataDao().getAllNames(listDataForSend.list_id)) {
                //chosenDepartmentData = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);
                int chosenDepartmentID = db.departmentDataDao().getChosenDepartment(departmentPosition, listDataForSend.list_id).department_id;
                // stringToSend += s + "[";
                stringToSend += s + "\n-*-\n";
                int dataCounter = 0;
                for (Data dataS : db.dataDao().getAllForGenerator(chosenDepartmentID)) {
                    Float data_qty_float = dataS.data_qty;
                    stringToSend += dataS.data_name + "-->" + data_qty_float.toString().replaceAll("\\.?0*$", "") + ";\n";
                    dataCounter++;
                    if (dataCounter == (db.dataDao().getAllForGenerator(chosenDepartmentID).size() - db.departmentDataDao().getChosenDepartment(departmentPosition, listDataForSend.list_id).CrossOutNumber))
                        break;
                }
                if (dataCounter > 0)
                    stringToSend = stringToSend.substring(0, stringToSend.length() - 2);
                //stringToSend += "]";
                stringToSend += "\n---\n";
                departmentPosition++;
            }
        // stringToSend += "]";
        stringToSend += "--=*=--";
        Log.d(TAG, "StringToSend ready " + stringToSend);
        return generatorFilter(stringToSend);
    }

    //menu in toolbar on right side
    public void onMoreMenuItemButtonClick(View view) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        // PopupMenu popup = new PopupMenu(this, view);
        PopupMenu popup = new PopupMenu(this, view);
        //popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }


        popup.show();

        if (editButtonClicked) {
            popup.getMenu().findItem(R.id.menu_edit).setTitle("Режим редактирования");
        } else {
            popup.getMenu().findItem(R.id.menu_edit).setTitle("Закончить редактирование");
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_share:
                        String stringToSend = listToStringGenerator();
                        newShare(item.getActionView(), stringToSend);
                        return true;
                    case R.id.menu_edit:
                        //addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
                        if (editButtonClicked) {
                            editButtonClicked = false;
                            addDepartmentButton.setVisibility(View.VISIBLE);
                        } else {
                            editButtonClicked = true;
                            addDepartmentButton.setVisibility(View.GONE);
                        }
                        setTabsVisibility();
                        viewPagerAdapter.notifyDataSetChanged();
                        // adapter.notifyDataSetChanged();
                        // adapterForDepartments.notifyDataSetChanged();
                        return true;
                    case R.id.menu_delete:
                        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                                .setMessage("Удалить список '" + chosenListData.getList_name() + "'?")
                                .setCancelable(true)
                                .setPositiveButton("Да",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteSingleItemInList();
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

                        return true;
                    case R.id.menu_edit_name:
                        //addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
                        editListName(view);
                        viewPagerAdapter.notifyDataSetChanged();
                        // adapter.notifyDataSetChanged();
                        // adapterForDepartments.notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    private void editListName(View view) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();

      /*  View parent = (View) view.getParent();
        parentID = parent.getId();
        View parentParent = (View) view.getParent().getParent();
        int parentParentID = parentParent.getId();*/


        final EditText et = new EditText(view.getContext());
        Log.d(TAG, name + " et start building");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et.setText(chosenListData.getList_name());
        Log.d(TAG, name + " et end building");

        String title = "Редактировать";
       /* if (position != 0 && parentID == R.id.rvAnimals) {
            TextView text = new TextView(view.getContext());
            Log.d(TAG, name + " TextView(view.getContext())");
            //  switch (parentID) {
            //      case R.id.rvAnimals:
            text = view.findViewById(R.id.tvAnimalName);
            Log.d(TAG, name + " text = view.findViewById(R.id.tvAnimalName)");
            //break;
               /* case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;*/
        //    }
         /*   et.setText(text.getText().toString() + " ");
            Log.d(TAG, name + " et.setText(text.getText().toString()");
            et.setSelection(et.length());

            title = "Редактировать";
        } else if (parentParentID == R.id.tabs)/* {
            TextView text = new TextView(view.getContext());
            Log.d(TAG, name + " TextView(view.getContext())");
            //  switch (parentID) {
            //      case R.id.rvAnimals:
            text = view.findViewById(R.id.tvDepartmentsName);
            Log.d(TAG, name + " text = view.findViewById(R.id.tvAnimalName)");

               /* case R.id.rvDepartments:
                    text = view.findViewById(R.id.tvDepartmentsName);
                    break;*/
        //    }
         /*   et.setText(text.getText().toString() + " ");
            Log.d(TAG, name + " et.setText(text.getText().toString()");
            et.setSelection(et.length());

            title = "Редактировать";
        } else {
            deleteFlagForEdit = false;
            title = "Добавить";
            et.setHint("Введите сообщение");
        }*/

        Log.d(TAG, name + "AlertDialog start building");
        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(title)
                //.setMessage("Write your message here")
                .setCancelable(true)
                .setView(et)
                .setPositiveButton("Ok", null)
                // .setNeutralButton("Следующее", null)
                .setNegativeButton(
                        "Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .show();
        Log.d(TAG, name + "AlertDialog end building");
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Log.d(TAG, name + "positiveButton start setOnClickListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                Log.d(TAG, "view name: " + view.toString());
                if (uniqueTest(str, view)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    saveEditedListName(str);
                    dialog.dismiss();
                } else {
                    Toast.makeText(view.getContext(), "Введите уникальное название", Toast.LENGTH_SHORT).show();
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
                            final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });
        et.requestFocus();
    }

    private void saveEditedListName(String str) {

        ListData newListData = chosenListData;
        newListData.setList_name(str);
        db.listDataDao().update(newListData);
        setNavigationDrawerData();


    }


    /*@Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                String stringToSend = listToStringGenerator();
                newShare(item.getActionView(), stringToSend);
                return true;
            case R.id.menu_edit:
                if (editButtonClicked) {
                    editButtonClicked = false;
                } else {
                    editButtonClicked = true;
                }
                adapter.notifyDataSetChanged();
                adapterForDepartments.notifyDataSetChanged();
                return true;
            case R.id.menu_delete:
                deleteSingleItemInList();
                return true;
           /* case R.id.data_menu_delete:
                deleteSingleItem(dataPosition);
                return true;
                //Toast.makeText(getBaseContext(), "delete", Toast.LENGTH_SHORT).show();
            case  R.id.data_menu_edit:
                //Toast.makeText(getBaseContext(), "edit", Toast.LENGTH_SHORT).show();
                deleteFlagForEdit = true;
                chosenData = db.dataDao().getChosenData(dataPosition, chosenDepartmentData.department_id);
                inputTextDialogWindow(this., dataPosition, dataPosition, parentID);
                return true;
            case R.id.data_menu_move:
                Toast.makeText(getBaseContext(), "move", Toast.LENGTH_SHORT).show();
                return true;*/
            /*default:
                return false;
        }
    }*/


    private static void dataHolderMenuItemButtonClick(View view, int position, int id) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        //popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.data_popup_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.show();
        //   Log.d(TAG, name + " popup.menu created" + parentID);
        View parentView = (View) view.getParent();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.data_menu_delete:
                        deleteSingleItem(position, id);
                        return true;
                    //Toast.makeText(getBaseContext(), "delete", Toast.LENGTH_SHORT).show();

                    case R.id.data_menu_edit:
                        //Toast.makeText(getBaseContext(), "edit", Toast.LENGTH_SHORT).show();
                      /*!  deleteFlagForEdit = true;
                        chosenData = db.dataDao().getChosenData(dataPosition, chosenDepartmentData.department_id);
                        Log.d(TAG, name + " data was chosen" + parentID);
                        inputTextDialogWindow(parentView, dataPosition, dataPosition);
                        Log.d(TAG, name + "inputTextDialogWindow(view, dataPosition, dataPosition, parentID) done");*/

                        inputTextDialogWindowForViewHolderItem(view, position, id);
                        Log.d(TAG, name + " view name: " + view.toString());
                        return true;
                    case R.id.data_menu_move:
                        //Toast.makeText(getBaseContext(), "move", Toast.LENGTH_SHORT).show();
                        //createDataMoveSubMenu(parentView);
                        createDataMoveSubMenu(view, id, position);
                        return false;
                    default:
                        return false;
                }
            }
        });
        /*if (editButtonClicked) {
            popup.getMenu().findItem(R.id.menu_edit).setTitle("Редактировать список");
        } else {
            popup.getMenu().findItem(R.id.menu_edit).setTitle("Закончить редактирование");
        }*/

    }

    private static void createDataMoveSubMenu(View view, int id, int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        // popup.setOnMenuItemClickListener(this);
        //popup2.inflate(R.menu.data_popup_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        Log.d(TAG, name + " popup = new PopupMenu");
        int depID = db.dataDao().getDepartmentIdByDataId(id);
        int listID = db.departmentDataDao().getDepartmentDataById(depID).list_id;
        Log.d(TAG, name + " listID: " + listID);
        for (DepartmentData s : db.departmentDataDao().getAll(listID)) {
            if (s.department_id != depID) popup.getMenu().add(s.department_name);
        }
        Log.d(TAG, name + " for (DepartmentData s");
        popup.show();
        Log.d(TAG, "createDataMoveSubMenu popup set");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                View parent = (View) view.getParent();
                TextView text = parent.findViewById(R.id.tvAnimalName);
                EditText dataQty = parent.findViewById(R.id.etAnimalCount);
                Log.d(TAG, name + " ext.getText().toString() listID: " + text.getText().toString());
                Data newData = new Data(
                        db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(),
                                listID).department_id,
                        1,
                        text.getText().toString(),
                        Float.parseFloat(dataQty.getText().toString()));
                Log.d(TAG, name + " newData = new Data");
                deleteSingleItem(position, id);
                db.dataDao().incrementValues(
                        db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(),
                                listID).department_id, 0);
                db.dataDao().insert(newData);
                Single.fromCallable(() -> notifyWithDelay(500)).subscribeOn(Schedulers.io()).subscribe();
                return false;
            }
        });
    }

    static int notifyWithDelay(int delay) {
        SystemClock.sleep(delay);
        mn.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPagerAdapter.notifyDataSetChanged();
            }
        });
        return 0;
    }

    static int notifyWithDelay(int delay, int position) {
        SystemClock.sleep(delay);
        mn.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //   viewPagerAdapter.notifyItemChanged(0);
                viewPagerAdapter.notifyDataSetChanged();
            }
        });
        return 0;
    }
}


//todo Добавление списков, отделов, элементов с помощью google assistant
//todo Обучение интерфейсу при первом старте


//todo обойти ограничение массива iDrawerItem[100] или перезаписывать элементы
//todo блокировка списка отпечатком и пинкодом
//todo поиск по списку?

//todo аттач фото к элементу отдела


//todo проверка на hardware клавиатуру при вызове alertdialog для корректировки или добавления элемента (те, где есть edittext)
//TODO в режиме редактирования делать из title в toolbar edittext вместо textview (title = "", edittext - visible, после выхода из режима редактирования забираем с edittext введеный текст, title = et, edittext.gone)

//todo по лонгтапу по элементу отдела появляется чекбокс, где можно выделить элементы и удалить несколько сразу

