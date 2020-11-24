package com.example.shoplist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    List<Data> data;

    static MyRecyclerViewAdapter adapter;
    int crossOutNumber;
    static boolean editButtonClicked = true;
    boolean deleteFlagForEdit;
    static int adapterPosition;
    int dataPosition;

    List<String> keysForDepartments;
    List<DepartmentData> listOfDepartmentsData;
    List<String> keysForLists;

    private Drawer drawerResult = null;
    private int selectedListIndex = 2;

    Toolbar toolbar;

    static ListDataDatabase db;
    static ListData chosenListData = new ListData();
    //static DepartmentData chosenDepartmentData;
    //static Data chosenData = new Data();
    int parentID;

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

    private static final String TAG = "myLogs";

    private ArrayList<String> arrayList = new ArrayList<>();
    private static List<Data> adapterListData;
    ImageButton addDepartmentButton;
    ImageButton addDepartmentEndButton;
    TextView holySpiritTV;
    ImageButton moreMenuButton;
    ImageButton editButton;

    static MainActivity mn;
    static boolean editFlag;
    static boolean stopClick = false;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREF_DARK_THEME = "enable_dark_theme";
    public static final String PREF_HIDE_EMPTY_DEPARTMENT = "hide_empty_department";

    public static boolean hideEmptyDepartmentPreference;

    public static Context context;

//todo! killerfeature: block list by fingerprint and pin

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mn = MainActivity.this;
        db = App.getInstance().getDatabase();
        keysForLists = new ArrayList<>();
        keysForDepartments = new ArrayList<>();

        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setPreferences();
        setContentView(R.layout.activity_main);

        setToolbar();
        setFirstElementOfList();
        setFirstElementInAllSections();

        setKeysForLists();

        //todo убрать блок ниже?
        data = new ArrayList<>();
        listOfDepartmentsData = new ArrayList<>();

        //todo убрать блок ниже?
        if (keysForDepartments.size() > 1) {
            //chosenDepartmentData = db.departmentDataDao().getChosenDepartment(1, chosenListData.list_id);
            getListOfDepartmentsData();

            getCrossOutNumber();
        }

        setViewPager(this);
        setTabs();
        setEditButton();


//todo! блок ниже в комментарии возможно не нужен
        //Get swipes from background
       /* findViewById(R.id.backgroundLL).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
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
        });*/

        holySpiritTV = (TextView) findViewById(R.id.holy_spirit_tv);

        setStartAndEndAddDepartmentButtons();
        setMoreMenuButton();

        setTabsVisibility();

        changeTotalActiveItemsCountInTab();

        //for bug with tab text color when app started
        viewPagerAdapter.notifyDataSetChanged();
    }

    private void setMoreMenuButton() {
        moreMenuButton = (ImageButton) findViewById(R.id.more_menu_button);
        if (db.listDataDao().getAllNames().size() < 2) {
            moreMenuButton.setVisibility(View.GONE);
        }
    }

    private void setStartAndEndAddDepartmentButtons() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
        addDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setStartAndEndAddDepartmentButtons() onClick addDepartmentButton started");
                inputTextDialogWindow(v, 1, 0);
            }
        });

        addDepartmentEndButton = (ImageButton) findViewById(R.id.add_department_button_in_the_end);
        addDepartmentEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setStartAndEndAddDepartmentButtons() onClick addDepartmentEndButton started");
                inputTextDialogWindow(v, 1, 0);
            }
        });
    }

    private void setEditButton() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        editButton = (ImageButton) findViewById(R.id.edit_button);
        setEditButtonVisibility();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editButtonClicked) {
                    editButtonClicked = false;
                } else {
                    editButtonClicked = true;
                }
                setEditModeButtonsVisibility();
                setTabsVisibility();

                viewPagerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setEditModeButtonsVisibility() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        if (!editButtonClicked) {
            addDepartmentButton.setVisibility(View.VISIBLE);
            addDepartmentEndButton.setVisibility(View.VISIBLE);
            holySpiritTV.setVisibility(View.VISIBLE);
        } else {
            addDepartmentButton.setVisibility(View.GONE);
            addDepartmentEndButton.setVisibility(View.GONE);
            holySpiritTV.setVisibility(View.GONE);
        }
    }

    private void setTabs() {
        Log.d(TAG, "setTabs() started");

        tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, myViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.d(TAG, "setTabs() onConfigureTab started");
                DepartmentData currentDepartment = getCurrentDepartment(position);

                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_tab, null);
                TextView textView = (TextView) view.findViewById(R.id.tvDepartmentsName);
                TextView textViewQty = (TextView) view.findViewById(R.id.tvDepartmentsQty);

                textView.setText(currentDepartment.department_name);

                setDepartmentsItemQtyInTabs(textViewQty, currentDepartment);

                tab.setCustomView(view);
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView selectedText = (TextView) view.findViewById(R.id.tvDepartmentsName);
                selectedText.setTextColor(Color.parseColor(getString(R.color.selected_tab_text)));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView selectedText = (TextView) view.findViewById(R.id.tvDepartmentsName);
                selectedText.setTextColor(Color.parseColor(getString(R.color.image_btn)));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setTabsOnLongClickListener();
    }

    private void setViewPager(Context context) {
        myViewPager2 = findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(context);
        myViewPager2.setAdapter(viewPagerAdapter);
    }

    private void setFirstElementInAllSections() {
        if (!Locale.getDefault().getLanguage().equals(loadLanguage()) && Locale.getDefault().getLanguage().equals("ru")) {
            Single.fromCallable(() -> changeLanguage("ru")).subscribeOn(Schedulers.io()).subscribe();
        } else if (!Locale.getDefault().getLanguage().equals(loadLanguage())) {
            Single.fromCallable(() -> changeLanguage("en")).subscribeOn(Schedulers.io()).subscribe();
        }
        saveLanguage(Locale.getDefault().getLanguage());
    }

    private void setFirstElementOfList() {
        ListData firstElementOfList = new ListData();

        if (db.listDataDao().getChosenList(0) == null) {
            firstElementOfList.setList_name(getString(R.string.add_list));
            firstElementOfList.list_position = 0;
            db.listDataDao().insert(firstElementOfList);
        } else {
            firstElementOfList = db.listDataDao().getChosenList(0);
            firstElementOfList.setList_name(getString(R.string.add_list));
            db.listDataDao().update(firstElementOfList);
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    private void setPreferences() {
        loadTheme();
        getHideEmptyDepartmentPreference();
    }

    public static void getHideEmptyDepartmentPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        hideEmptyDepartmentPreference = sharedPreferences.getBoolean(PREF_HIDE_EMPTY_DEPARTMENT, false);
        Log.d(TAG, "getHideEmptyDepartmentPreference() " + hideEmptyDepartmentPreference);
    }

    private DepartmentData getCurrentDepartment(int position) {
        DepartmentData currentDepartment;
        if (!editButtonClicked) {
            currentDepartment = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
        } else {
            List<Integer> allVisibleDepartmentsID = db.departmentDataDao().getAllVisibleDepartmentsID(chosenListData.list_id);
            currentDepartment = db.departmentDataDao().getDepartmentDataById(allVisibleDepartmentsID.get(position));
        }
        return currentDepartment;
    }

    private void setDepartmentsItemQtyInTabs(TextView textViewQty, DepartmentData currentDepartment) {
        int activeItem = db.dataDao().getAllNames(currentDepartment.department_id).size() - currentDepartment.CrossOutNumber - 1;
        if (activeItem != 0) {
            textViewQty.setText(String.valueOf(activeItem));
        } else {
            textViewQty.setVisibility(View.GONE);
        }
    }

    private static int getChosenDepartmentID(int position) {
        int departmentID;
        if (!editButtonClicked) {
            departmentID = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id).department_id;
        } else {
            List<Integer> allVisibleDepartmentsID = db.departmentDataDao().getAllVisibleDepartmentsID(chosenListData.list_id);
            departmentID = db.departmentDataDao().getDepartmentDataById(allVisibleDepartmentsID.get(position)).department_id;
        }
        return departmentID;
    }

    public static DepartmentData getChosenDepartmentData(int position) {
        DepartmentData departmentData;
        if (!editButtonClicked) {
            departmentData = db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id);
        } else {
            List<Integer> allVisibleDepartmentsID = db.departmentDataDao().getAllVisibleDepartmentsID(chosenListData.list_id);
            departmentData = db.departmentDataDao().getDepartmentDataById(allVisibleDepartmentsID.get(position));
        }
        return departmentData;
    }

    private void setEditButtonVisibility() {
        if (keysForLists.size() < 2) {
            editButton.setVisibility(View.INVISIBLE);
        } else {
            editButton.setVisibility(View.VISIBLE);
        }
    }

    private int changeLanguage(String lang) {
        String str;
        if (lang.equals("ru")) {
            str = "Добавить";
        } else {
            str = "Add";
        }

        for (Data d : db.dataDao().getAllZerosElements()) {
            d.data_name = str;
            db.dataDao().update(d);
        }
        return 0;
    }

    private void setTabsVisibility() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        LinearLayout tabsll = (LinearLayout) findViewById(R.id.tabs_linear_layout);

        if (!haveVisibleDepartmentInList() && editButtonClicked) {
            tabsll.setVisibility(View.GONE);
        } else {
            tabsll.setVisibility(View.VISIBLE);
        }

        Single.fromCallable(() -> setTabsOnLongClickListenerWithDelay(100)).subscribeOn(Schedulers.io()).subscribe();

    }

    int setTabsOnLongClickListenerWithDelay(int delay) {
        SystemClock.sleep(delay);
        mn.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTabsOnLongClickListener();
            }
        });
        return 0;
    }

    private void setTabsOnLongClickListener() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            int finalI = i;
            tabStrip.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    menuForDepartments(v, finalI);
                    return true;
                }
            });
        }
    }

    private void menuForDepartments(View view, int position) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        DepartmentData departmentData = db.departmentDataDao().getDepartmentDataById(getChosenDepartmentID(position));
        PopupMenu popup = setPopupMenuForDepartment(view, departmentData);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, name + " onMenuItemClick started");

                switch (item.getItemId()) {
                    case R.id.department_menu_delete:
                        deleteDepartmentAlertDialog(view, departmentData);
                        return true;
                    case R.id.department_menu_edit:
                        editDepartmentDialogWindow(view, departmentData);
                        return true;
                    case R.id.department_menu_move:
                        createDepartmentMoveSubMenu(view, departmentData);
                        return true;
                    case R.id.department_menu_hide_or_show:
                        hideOrShowDepartmentFromPopupMenu(departmentData);
                        return true;
                    case R.id.department_clean:
                        cleanDepartmentAlertDialog(view, departmentData);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private PopupMenu setPopupMenuForDepartment(View view, DepartmentData departmentData) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        PopupMenu popup = new PopupMenu(view.getContext(), view);

        popup.inflate(R.menu.department_popup_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.show();

        setShowOrHideItemInPopupMenu(popup, departmentData);

        return popup;
    }

    private void deleteDepartmentAlertDialog(View view, DepartmentData departmentData) {
        TextView text = view.findViewById(R.id.tvDepartmentsName);
        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setMessage(getString(R.string.delete_department) + text.getText().toString() + "'?")
                .setCancelable(true)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSingleItemInDepartments(departmentData);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create();
        setAlertDialogButtonsColor(view, dialog);
        dialog.show();
    }

    //todo!!! BUG если очистить раздел, где были вычернуты пунты - то общее количество активных станет отрицательным
    private void cleanDepartmentAlertDialog(View view, DepartmentData departmentData) {

        TextView text = view.findViewById(R.id.tvDepartmentsName);

        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setMessage(getString(R.string.clean_department) + " '" + text.getText().toString() + "'?")
                .setCancelable(true)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAllItemInDepartment(departmentData);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create();
        setAlertDialogButtonsColor(view, dialog);
        dialog.show();
    }

    private void hideOrShowDepartmentFromPopupMenu(DepartmentData departmentData) {

        if (departmentData.visibility == 1) {
            setDepartmentInvisible(departmentData.department_id);
        } else {
            setDepartmentVisible(departmentData.department_id);
        }

        viewPagerAdapter.notifyDataSetChanged();
    }

    //TODO!!! bug with department popupmenu in edit mode, didn't create popup if last department is invisible
    private void setShowOrHideItemInPopupMenu(PopupMenu popup, DepartmentData departmentData) {
        if (departmentData.visibility == 1) {
            popup.getMenu().findItem(R.id.department_menu_hide_or_show).setTitle(R.string.hide_department);
            popup.getMenu().findItem(R.id.department_menu_hide_or_show).setIcon(R.drawable.ic_visibility_off_black);
        } else {
            popup.getMenu().findItem(R.id.department_menu_hide_or_show).setTitle(R.string.show_department);
            popup.getMenu().findItem(R.id.department_menu_hide_or_show).setIcon(R.drawable.ic_visibility_on_black);
        }
    }

    private void createDepartmentMoveSubMenu(View view, DepartmentData departmentData) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        PopupMenu popup = setPopupMenuForDepartmentMoving(view);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, name + " onMenuItemClick started");

                moveDepartmentToNewList(item, departmentData);
                setTabsVisibility();
                return false;
            }
        });
    }

    private PopupMenu setPopupMenuForDepartmentMoving(View view) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        PopupMenu popup = new PopupMenu(view.getContext(), view);

        if (db.listDataDao().getAll().size() > 1) {
            for (ListData s : db.listDataDao().getAll()) {
                if (s.list_id != chosenListData.list_id) popup.getMenu().add(s.getList_name());
            }
            popup.show();
        } else {
            Toast.makeText(this, R.string.too_few_lists, Toast.LENGTH_SHORT).show();
        }
        return popup;
    }


    private void moveDepartmentToNewList(MenuItem item, DepartmentData departmentData) {

        //DepartmentData departmentData = db.departmentDataDao().getDepartmentDataById(id);
        ListData toList = db.listDataDao().getChosenListByName(item.getTitle().toString());
        int oldPosition = departmentData.department_position;
        boolean nameSetStatus = true;
        int copiesCounter = 0;
        String tempDepartmentName = departmentData.department_name;

        departmentData.list_id = toList.list_id;
        departmentData.department_position = 0;

        do {
            if (!db.departmentDataDao().getAllNames(toList.list_id).contains(tempDepartmentName)) {
                if (copiesCounter > 0)
                    departmentData.department_name += " (" + copiesCounter + ")";
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
    }

    private void getListOfDepartmentsData() {
        listOfDepartmentsData = db.departmentDataDao().getAll(chosenListData.list_id);
    }

    /*public void setAdapter(MyRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }*/

    //TODO!!! оптимизировать метод ниже
    public static void ViewPagerItemClicked(View view, int dataID, MyRecyclerViewAdapter adapter, int position, List<Data> adapterData) {

        setAdapter(adapter);
        setAdapterData(adapterData);

        if (editButtonClicked) {
            DepartmentData temp = db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(dataID));
            if (position < (db.dataDao().getAllNames(db.dataDao().getDepartmentIdByDataId(dataID)).size()
                    - db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(dataID)).CrossOutNumber)) {
                temp.CrossOutNumber++;
                moveItemToBottom(dataID, position);
                Log.d(TAG, "moveItemToBottom");
            } else {
                db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(dataID)).CrossOutNumber--;
                temp.CrossOutNumber--;
                moveItemToTop(dataID, position);
                Log.d(TAG, " moveItemToTop");
            }
            db.departmentDataDao().update(temp);
            TabLayout.Tab tab = tabLayout.getTabAt(myViewPager2.getCurrentItem());
            View tabView = tab.getCustomView();
            TextView textViewQty = (TextView) tabView.findViewById(R.id.tvDepartmentsQty);
            Log.d(TAG, "TextView textView ");
            int activeItem = db.dataDao().getAllNames(db.dataDao().getDepartmentIdByDataId(dataID)).size()
                    - db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(dataID)).CrossOutNumber - 1;
            Log.d(TAG, "int activeItem");

            if (activeItem != 0) {
                textViewQty.setVisibility(View.VISIBLE);
                textViewQty.setText(String.valueOf(activeItem));
                tab.setCustomView(tabView);
            } else if (hideEmptyDepartmentPreference) {
                setDepartmentInvisible(getChosenDepartmentID(myViewPager2.getCurrentItem()));
                viewPagerAdapter.notifyDataSetChanged();
            } else {
                textViewQty.setVisibility(View.VISIBLE);
                textViewQty.setVisibility(View.GONE);
                tab.setCustomView(tabView);
            }
            Log.d(TAG, " if (activeItem != 0) ");

            changeTotalActiveItemsCountInTab();

        } else {
            switch (view.getId()) {
                case R.id.image_more:
                    dataHolderMenuItemButtonClick(view, position, dataID);
                    break;
                default:
                    inputTextDialogWindowForViewHolderItem(view, position, dataID);
            }
        }
    }

    private static void changeTotalActiveItemsCountInTab() {
        View view = tabLayout.getRootView();
        TextView textViewQtyForList = (TextView) view.findViewById(R.id.tvListQty);
        int activeItemsQtyForList = getTotalActiveItemsCountForChosenList();

        if (activeItemsQtyForList > 0) {
            textViewQtyForList.setText(String.valueOf(activeItemsQtyForList));
            textViewQtyForList.setVisibility(View.VISIBLE);
        } else {
            textViewQtyForList.setVisibility(View.GONE);
            setStaticTabsVisibility();
        }
    }

    private static void setStaticTabsVisibility() {

        View view = tabLayout.getRootView();
        LinearLayout tabsll = (LinearLayout) view.findViewById(R.id.tabs_linear_layout);

        if (!haveVisibleDepartmentInListStatic() && editButtonClicked) {
            tabsll.setVisibility(View.GONE);
        } else {
            tabsll.setVisibility(View.VISIBLE);
        }
    }

    private static boolean haveVisibleDepartmentInListStatic() {
        if (db.departmentDataDao().getAllVisibleDepartmentNames(chosenListData.list_id).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean haveVisibleDepartmentInList() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        if (db.departmentDataDao().getAllVisibleDepartmentNames(chosenListData.list_id).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private static int getTotalActiveItemsCountForChosenList() {
        List<DepartmentData> listOfDepartmentsData = new ArrayList<DepartmentData>(db.departmentDataDao().getAll(chosenListData.list_id));
        int sumOfActive = 0;

        for (DepartmentData dd : listOfDepartmentsData) {
            sumOfActive += db.dataDao().getAll(dd.department_id).size() - dd.CrossOutNumber - 1;
        }

        return sumOfActive;
    }

    private static void setHideDepartmentAlertDialog(View view) {
        AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.hide_section)
                .setCancelable(true)
                .setPositiveButton(
                        view.getContext().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setDepartmentInvisible(getChosenDepartmentID(myViewPager2.getCurrentItem()));
                                viewPagerAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(
                        view.getContext().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .create();

        setAlertDialogButtonsColor(view, dialog);

        dialog.show();
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
            title = view.getContext().getString(R.string.to_edit);
        } else {
            editFlag = false;
            title = view.getContext().getString(R.string.add);
            et.setHint(view.getContext().getString(R.string.enter_text));
        }

        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(title)
                .setCancelable(true)
                .setView(et)
                .setPositiveButton(view.getContext().getString(R.string.ok), null)
                .setNeutralButton(view.getContext().getString(R.string.next), null)
                .setNegativeButton(
                        view.getContext().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .create();

        setAlertDialogButtonsColor(view, dialog);

        dialog.show();
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
                str = deleteLeftRightSpacesInItem(str);
                if (!str.isEmpty()) {
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
                }
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
                str = deleteLeftRightSpacesInItem(str);
                if (!str.isEmpty()) {
                    boolean tempEditFlag = editFlag;
                    if (editFlag) {
                        editOldData(str, id);
                        Log.d(TAG, " editFlag = false; start");
                        editFlag = false;
                        Log.d(TAG, " editFlag = false; end");
                        dialog.setTitle(view.getContext().getString(R.string.add));
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

    private static void setAlertDialogButtonsColor(View view, AlertDialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
            }
        });
    }

    private static void editOldData(String str, int id) {
        Data tempData = db.dataDao().getChosenDataById(id);
        tempData.data_name = str;
        db.dataDao().update(tempData);
        Log.d(TAG, "editOldData ended");
    }


    public static void setAdapterData(List<Data> adapterData) {
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
        Log.d(TAG, " activeQtyForList started");
        List<DepartmentData> listOfDepartmentsData = new ArrayList<DepartmentData>(db.departmentDataDao().getAll(db.listDataDao().getChosenList(position).list_id));
        Log.d(TAG, "  List<DepartmentData> listOfDepartmentsData");
        int sumOfActive = 0;
        for (DepartmentData dd : listOfDepartmentsData) {
            Log.d(TAG, "for (DepartmentData dd : listOfDepartmentsData)");
            sumOfActive += MainActivity.db.dataDao().getAll(dd.department_id).size() - dd.CrossOutNumber - 1;
        }
        Log.d(TAG, " activeQtyForList sum: " + sumOfActive);

        return sumOfActive;
    }

    void setNavigationDrawerData() {
        Log.d(TAG, "setNavigationDrawerData() started");
//todo при добавлении нового списка проверять на максимальное количество списков, если больше 1000, предлагать удалить лишние, возможно предлагать удалить первые 100 созданных

        IDrawerItem[] iDrawerItems = setListsNamesForDrawer();

        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.d(TAG, "setNavigationDrawerData() onItemClick started");
                        if (position == 1) {
                            //todo! оптимизировать метод ниже
                            inputTextDialogWindow(view, 1, position - 1);
                        } else {
                            setActiveList(position);
                            selectedListIndex = position;
                            myViewPager2.setCurrentItem(0);
                        }

                        setNavigationDrawerData();
                        setTabsOnLongClickListener();
                        changeTotalActiveItemsCountInTab();
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        Log.d(TAG, "setNavigationDrawerData() onDrawerOpened started");

                        setOrUpdateBadge(drawerResult);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .addDrawerItems(iDrawerItems)
                .build();

        if (keysForLists.size() > 1) {
            setTitle(chosenListData.getList_name());

            if (db.listDataDao().getAllPositions().size() > 1)
                drawerResult.setSelection(selectedListIndex - 1, false);

        } else {
            setTitle(getString(R.string.press_here));
        }
    }

    private void setOrUpdateBadge(Drawer drawerResult) {
        if (activeQtyForList(drawerResult.getCurrentSelectedPosition() - 1) > 0) {
            drawerResult.updateBadge(drawerResult.getCurrentSelectedPosition() - 1, new StringHolder(activeQtyForList(drawerResult.getCurrentSelectedPosition() - 1) + ""));
            if (db.listDataDao().getAllPositions().size() > 1)
                drawerResult.setSelection(selectedListIndex - 1, false);
        } else {
            drawerResult.updateBadge(drawerResult.getCurrentSelectedPosition() - 1, null);
            if (db.listDataDao().getAllPositions().size() > 1)
                drawerResult.setSelection(selectedListIndex - 1, false);
        }
    }

    private IDrawerItem[] setListsNamesForDrawer() {
        Log.d(TAG, "setListsNamesForDrawer() started");

        //IDrawerItem[] iDrawerItems = new IDrawerItem[keysForLists.size()];
        // IDrawerItem[] iDrawerItems = new IDrawerItem[keysForLists.size()];

        IDrawerItem[] iDrawerItems = new IDrawerItem[keysForLists.size()];
        Log.d(TAG, "IDrawerItem[]");
        for (int i = 0; i < keysForLists.size(); i++) {
            Log.d(TAG, "for (int i = " + i);
            int activeQty = activeQtyForList(i);
            Log.d(TAG, "int activeQty");
            if (activeQty > 0 && i != 0) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (sharedPreferences.getBoolean(PREF_DARK_THEME, true)) {
                    iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
                } else {
                    iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_700).withCornersDp(16));
                }
            } else {
                iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
            }
        }

        Log.d(TAG, "IDrawerItem[]");
        for (int i = 0; i < keysForLists.size(); i++) {
            Log.d(TAG, "for (int i = " + i);
            int activeQty = activeQtyForList(i);
            Log.d(TAG, "int activeQty");
            if (activeQty > 0 && i != 0) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (sharedPreferences.getBoolean(PREF_DARK_THEME, true)) {
                    iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
                } else {
                    iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_700).withCornersDp(16));
                }
            } else {
                iDrawerItems[i] = new PrimaryDrawerItem().withIdentifier(i).withName(keysForLists.get(i)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
            }

        }
        Log.d(TAG, "setListsNamesForDrawer() ended");
        return iDrawerItems;

    }

    void setActiveList(int position) {
        Log.d(TAG, "setActiveList started");

        if (keysForLists.size() > 1) {
            chosenListData = db.listDataDao().getChosenList(position - 1);
            setTitle(chosenListData.getList_name());
            if (selectedListIndex != 2) selectedListIndex--;
            setNavigationDrawerData();
        }

        setTabsVisibility();
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        // Close Navigation Drawer by press button "Back" if it is in open condition

        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();

            // Go to first element of ViewPager by press button "Back" if it is position >0

        } else if (!editButtonClicked) {
            editButtonClicked = true;
            addDepartmentButton.setVisibility(View.GONE);
            addDepartmentEndButton.setVisibility(View.GONE);
            holySpiritTV.setVisibility(View.GONE);
            setTabsVisibility();
            viewPagerAdapter.notifyDataSetChanged();
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

    public void setKeysForLists() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        keysForLists.clear();
        keysForLists.addAll(db.listDataDao().getAllNamesNotFlowable());

        if (keysForLists.size() > 1) {
            chosenListData = db.listDataDao().getChosenList(1);
        }

        setNavigationDrawerData();
    }


    /*public void getData() {
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
    }*/

    void getCrossOutNumber() {
        if (keysForLists.size() > 1) {
            //crossOutNumber = chosenDepartmentData.CrossOutNumber;
        }
    }

    void setCrossOutNumber() {
        if (keysForLists.size() > 1) {
            //chosenDepartmentData.CrossOutNumber = crossOutNumber;
            //db.departmentDataDao().update(chosenDepartmentData);
        }
    }

    //todo вроде не используется
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
                //getData();
                clicker = true;
                break;
            case R.id.image_to_high:
                //!   db.dataDao().plusQty(position, chosenDepartmentData.department_id);
                //getData();
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
                        .setMessage(R.string.delete_department + text.getText().toString() + "'?")
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //! возможно не используется deleteSingleItemInDepartments(position);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(
                                R.string.no,
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
                    // chosenData = db.dataDao().getChosenData(position, chosenDepartmentData.department_id);
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

    //todo!!! оптимизоровать метод ниже

    //for adding section, list and when create new list without loading default data
    public void inputTextDialogWindow(final View view, final int insertIndex, final int position) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        String title;
        final EditText et = new EditText(view.getContext());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        if (view.getId() == R.id.add_department_button
                || view.getId() == R.id.add_department_button_in_the_end) {
            deleteFlagForEdit = false;
            title = getString(R.string.add_department);
            et.setHint(getString(R.string.enter_text));
        } else {
            deleteFlagForEdit = false;
            title = getString(R.string.add_new_list);
            et.setHint(getString(R.string.enter_text));
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(title)
                .setCancelable(true)
                .setView(et)
                .setPositiveButton(getString(R.string.ok), null);

        if (view.getId() == R.id.add_department_button
                || view.getId() == R.id.add_department_button_in_the_end)
            builder.setNeutralButton(getString(R.string.next), null);

        builder.setNegativeButton(
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteFlagForEdit = false;
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        setAlertDialogButtonsColor(view, dialog);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                str = deleteLeftAndRightSpaces(str);

                boolean isItShare = str.contains("-=***=-");

                if (str.length() > 0) {
                    if (str.length() <= 12 || (isItShare && view.getId() != R.id.add_department_button
                            && view.getId() != R.id.add_department_button_in_the_end)) {

                        if (uniqueTest(str, view)) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                            inputButtonClicked(str, insertIndex, view);
                            setTabsOnLongClickListener();

                            dialog.dismiss();
                        } else {
                            Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.too_large_name, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                str = deleteLeftAndRightSpaces(str);

                if (str.length() <= 12) {
                    if (!str.isEmpty()) {
                        if (uniqueTest(str, view)) {

                            inputButtonClicked(str, insertIndex, view);

                            et.getText().clear();
                            et.setHint(getString(R.string.enter_text));

                            setTabsOnLongClickListener();
                        } else {
                            Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.too_large_name, Toast.LENGTH_SHORT).show();
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

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 9) {
                    dialog.setTitle(title + "                     " + (12 - s.toString().length()));
                } else {
                    dialog.setTitle(title);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private String deleteLeftAndRightSpaces(String str) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        if (str.length() > 0) {
            while (str.charAt(str.length() - 1) == ' ') {
                str = str.substring(0, str.length() - 1);
                if (str.length() == 0) break;
            }

            while (str.charAt(0) == ' ') {
                str = str.substring(1, str.length());
                if (str.length() == 0) break;
            }
        }
       return str;
    }

    static private String deleteLeftRightSpacesInItem(String str) {
        logThisMethodStatic(new Object() {}.getClass().getEnclosingMethod().getName());

        if (str.length() > 0) {
            while (str.charAt(str.length() - 1) == ' ') {
                str = str.substring(0, str.length() - 1);
                if (str.length() == 0) break;
            }
            while (str.charAt(0) == ' ') {
                str = str.substring(1, str.length());
                if (str.length() == 0) break;
            }
        }
        return str;
    }
//TODO!!! Если раздел скрыт, то не считать его невычеркнутые пункты активными

    public void editDepartmentDialogWindow(final View view, DepartmentData departmentData) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        int insertIndex = departmentData.department_position;

        final EditText et = setEditTextForEditDepartmentAlertDialog(view);
        String title = getString(R.string.to_edit);

        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(title)
                .setCancelable(true)
                .setView(et)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .create();
        setAlertDialogButtonsColor(view, dialog);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, name + " positiveButton onClick started");
                String str = et.getText().toString();
                if (uniqueTest(str, view)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    inputButtonClicked(str, insertIndex, view);
                    dialog.dismiss();
                } else {
                    Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
                }
            }
        });

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                Log.d(TAG, name + " onFocusChange started");

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

    private EditText setEditTextForEditDepartmentAlertDialog(View view) {
        final EditText et = new EditText(view.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        TextView text = view.findViewById(R.id.tvDepartmentsName);
        et.setText(text.getText().toString() + " ");
        et.setSelection(et.length());
        return et;
    }


    private void inputButtonClicked(String str, int insertIndex, View view) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        if (!str.isEmpty()) {
            if (deleteFlagForEdit) {
                deleteFlagForEdit = false;
            } else {
                insertFromPopup(str, insertIndex, view);
            }
        }
    }

    boolean uniqueTest(String str, View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        View parent = (View) view.getParent();
        parentID = parent.getId();
        View parentParent = (View) view.getParent().getParent();
        int parentParentID = parentParent.getId();

        return (parentID == R.id.rvAnimals
                || (!db.listDataDao().getAllNamesNotFlowable().contains(str)
                    && parentID == R.id.material_drawer_recycler_view)
                || ((view.getId() == R.id.add_department_button
                    || view.getId() == R.id.add_department_button_in_the_end)
                    && !db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str))
                || (parentParentID == R.id.tabs
                    && !db.departmentDataDao().getAllNames(chosenListData.list_id).contains(str))
                || (view.getId() == R.id.more_menu_button
                    && !db.listDataDao().getAllNames().contains(str))
        );
    }


    private void setNewDepartment(String s) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, 0, s, 0);
        db.departmentDataDao().incrementValues(chosenListData.list_id, 0);
        db.departmentDataDao().insert(departmentData);

        Data data = new Data(
                db.departmentDataDao().getChosenDepartment(0, chosenListData.list_id).department_id,
                0, getString(R.string.add), 0.0f);
        db.dataDao().insert(data);

        viewPagerAdapter.notifyDataSetChanged();
    }

    private void setNewDepartmentFromParse(String s, int position) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        DepartmentData departmentData = new DepartmentData(chosenListData.list_id, position, s, 0);
        db.departmentDataDao().insert(departmentData);

        Data data = new Data(
                db.departmentDataDao().getChosenDepartment(position, chosenListData.list_id).department_id,
                0, getString(R.string.add), 0.0f);
        db.dataDao().insert(data);
    }

    private static void setNewData(String s, int position, int id) {

        int departmentID = db.dataDao().getChosenDataById(id).department_id;

        Data newData = new Data(departmentID, position, s, 0.0f);
        db.dataDao().incrementValues(db.dataDao().getChosenDataById(id).department_id, position - 1);
        db.dataDao().insert(newData);
        //data.add(position, newData);
        //Log.d(TAG, "Test increment data position: " + db.dataDao().getAllPositions(chosenDepartmentData.department_id));
        setDepartmentVisible(departmentID);
        changeTotalActiveItemsCountInTab();
        Log.d(TAG, "new data set ended");
    }

    private static void setNewData(int position, String s, int departmentPosition, Float dataQty) {
        Log.d(TAG, "new data set started");
        int departmentID = db.departmentDataDao().getChosenDepartment(departmentPosition - 1, chosenListData.list_id).department_id;
        Data newData = new Data(departmentID, position, s, dataQty);
        //Log.d(TAG, " Data newData = new Data ended");
        db.dataDao().incrementValues(db.departmentDataDao().getChosenDepartment(departmentPosition - 1, chosenListData.list_id).department_id, position - 1);
        db.dataDao().insert(newData);
        setDepartmentVisible(departmentID);
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

    private static void setDepartmentVisible(int departmentID) {
        Log.d(TAG, "setDepartmentVisible started");

        DepartmentData departmentData = db.departmentDataDao().getDepartmentDataById(departmentID);

        if (departmentData.visibility != 1) {
            departmentData.visibility = 1;
            db.departmentDataDao().update(departmentData);

            setStaticTabsVisibility();
        }
    }

    private static void setDepartmentInvisible(int departmentID) {
        Log.d(TAG, "setDepartmentInvisible started");

        DepartmentData departmentData = db.departmentDataDao().getDepartmentDataById(departmentID);
        departmentData.visibility = 0;
        db.departmentDataDao().update(departmentData);

        setStaticTabsVisibility();
    }

    //todo! проработать метод
    private void insertFromPopup(String s, int insertIndex, View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        View parent = (View) view.getParent();
        View parentParent = (View) view.getParent().getParent();
        parentID = parent.getId();
        int parentParentID = parentParent.getId();

        if (view.getId() == R.id.add_department_button
                || view.getId() == R.id.add_department_button_in_the_end) {
            setNewDepartment(s);
        } else if (parentParentID == R.id.tabs) {
            DepartmentData departmentData = db.departmentDataDao().getChosenDepartment(insertIndex, chosenListData.list_id);
            editDepartmentName(s, departmentData);
        } else {
                parser(s);
                updateNavigationDrawer();

                if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() == 0) {
                    final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                            .setMessage(R.string.fill_list_with_default_values)
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setDefaultList();

                                            dialog.cancel();
                                            viewPagerAdapter.notifyDataSetChanged();

                                            editButtonClicked = true;
                                            setEditModeButtonsVisibility();
                                            setNavigationDrawerData();
                                            setTabsVisibility();
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            editButtonClicked = false;
                                            setEditModeButtonsVisibility();
                                            setTabsVisibility();

                                            inputTextDialogWindow(findViewById(R.id.add_department_button), 1, 0);
                                            dialog.cancel();
                                        }
                                    })
                            .create();
                    setAlertDialogButtonsColor(view, dialog);
                    dialog.show();
                }

                viewPagerAdapter.notifyDataSetChanged();

                //TODO!!! fix menu button visibility
                if (moreMenuButton.getVisibility() == View.GONE) {
                    moreMenuButton.setVisibility(View.VISIBLE);
                }

                setEditButtonVisibility();
        }
    }

    private void editDepartmentName(String s, DepartmentData editedDepartment) {
        Log.d(TAG, "editDepartmentName started");

        editedDepartment.department_name = s;
        db.departmentDataDao().update(editedDepartment);

        viewPagerAdapter.notifyDataSetChanged();
    }

    private void setDefaultList() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        LinkedHashMap<String, List<String>> defaultData = new LinkedHashMap<>();
        List<String> departmentData = new ArrayList<>();

        int departmentPosition = 0;

        departmentData.add(getString(R.string.Toothpaste));
        departmentData.add(getString(R.string.dishwashing_liquid));
        departmentData.add(getString(R.string.garbage_bags));
        departmentData.add(getString(R.string.cling_film));
        departmentData.add(getString(R.string.dish_sponges));
        departmentData.add(getString(R.string.washing_powder));
        defaultData.put(getString(R.string.household_goods), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.wet_wipes));
        departmentData.add(getString(R.string.Shampoo));
        departmentData.add(getString(R.string.soap));
        defaultData.put(getString(R.string.cosmetics), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.pork));
        departmentData.add(getString(R.string.beef));
        departmentData.add(getString(R.string.сhicken));
        departmentData.add(getString(R.string.mincemeat));
        defaultData.put(getString(R.string.butcher_s), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.fish));
        departmentData.add(getString(R.string.shrimp));
        defaultData.put(getString(R.string.seafood), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.apples));
        departmentData.add(getString(R.string.bananas));
        departmentData.add(getString(R.string.oranges));
        departmentData.add(getString(R.string.pears));
        departmentData.add(getString(R.string.cabbage));
        departmentData.add(getString(R.string.onion));
        departmentData.add(getString(R.string.carrot));
        departmentData.add(getString(R.string.potatoes));
        departmentData.add(getString(R.string.greens));
        departmentData.add(getString(R.string.tomatoes));
        departmentData.add(getString(R.string.сucumbers));
        departmentData.add(getString(R.string.garlic));
        departmentData.add(getString(R.string.mushrooms));
        defaultData.put(getString(R.string.veggies_fruit), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.milk));
        departmentData.add(getString(R.string.yogurt));
        departmentData.add(getString(R.string.butter));
        departmentData.add(getString(R.string.сream));
        departmentData.add(getString(R.string.cheese));
        departmentData.add(getString(R.string.сhicken_eggs));
        defaultData.put(getString(R.string.dairy), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.olives));
        departmentData.add(getString(R.string.peas));
        departmentData.add(getString(R.string.corn));
        departmentData.add(getString(R.string.beans));
        defaultData.put(getString(R.string.canned_items), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.pasta));
        departmentData.add(getString(R.string.buckwheat));
        departmentData.add(getString(R.string.rice));
        departmentData.add(getString(R.string.sugar));
        departmentData.add(getString(R.string.oatmeal));
        departmentData.add(getString(R.string.tea));
        departmentData.add(getString(R.string.coffee));
        departmentData.add(getString(R.string.olive_oil));
        defaultData.put(getString(R.string.cereal_coffee), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.bread));
        departmentData.add(getString(R.string.cookies));
        departmentData.add(getString(R.string.muffins));
        departmentData.add(getString(R.string.chocolate));
        defaultData.put(getString(R.string.deli), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.mayonnaise));
        departmentData.add(getString(R.string.ketchup));
        defaultData.put(getString(R.string.sauces_spices), departmentData);
        departmentData = new ArrayList<>();

        departmentData.add(getString(R.string.bottled_water));
        departmentData.add(getString(R.string.juice));
        defaultData.put(getString(R.string.water_drinks), departmentData);

        for (String key : defaultData.keySet()) {

            int dataPosition = 1;

            setNewDepartmentFromParse(key, departmentPosition);

            DepartmentData dd = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id);
            dd.visibility = 1;
            db.departmentDataDao().update(dd);

            for (String s : defaultData.get(key)) {
                Data tempData = new Data(dd.department_id, dataPosition, s, 0);
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

    //todo вроде не используется
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

      /*  Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        int pos = db.dataDao().getAllPositions(chosenDepartmentData.department_id).size();
        db.dataDao().updateSingleItemPosition(temp.data_id, pos);
        db.dataDao().decrementValues(chosenDepartmentData.department_id, fromPosition);
        adapter.notifyItemMoved(fromPosition, pos - 1);
        adapter.notifyItemChanged(pos - 1);*/
    }

    //todo вроде не используется
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

        /*Data temp = db.dataDao().getChosenData(fromPosition, chosenDepartmentData.department_id);
        db.dataDao().incrementValuesFromOneToPosition(chosenDepartmentData.department_id, fromPosition);
        db.dataDao().updateSingleItemPosition(temp.data_id, 1);
        adapter.notifyItemMoved(fromPosition, toPosition);
        adapter.notifyItemChanged(toPosition);*/
    }

    private static void deleteSingleItem(int position, int id) {
        int departmentID;
        if (position >= (db.dataDao().getAllNames(db.dataDao().getDepartmentIdByDataId(id)).size() - db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).CrossOutNumber)) {
            DepartmentData temp = db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id));
            temp.CrossOutNumber--;
            db.departmentDataDao().update(temp);
        }
        //  Log.d(TAG,"deleteSingleItem started");
        if (position > 0) {
            departmentID = db.dataDao().getDepartmentIdByDataId(id);
            db.dataDao().deleteSingleDataById(id);
            db.dataDao().decrementValues(departmentID, position);

            adapterListData.clear();
            adapterListData.addAll(db.dataDao().getAll(
                    departmentID
            ));

            adapter.notifyItemRemoved(position); // notify the adapter about the removed item

            TabLayout.Tab tab = tabLayout.getTabAt(myViewPager2.getCurrentItem());
            View tabView = tab.getCustomView();
            TextView textViewQty = (TextView) tabView.findViewById(R.id.tvDepartmentsQty);
            Log.d(TAG, "TextView textView ");
            int activeItem = db.dataDao().getAllNames(departmentID).size()
                    - db.departmentDataDao().getDepartmentDataById(departmentID).CrossOutNumber - 1;
            Log.d(TAG, "int activeItem");

            // textView.setText(db.departmentDataDao().getAll(chosenListData.list_id).get(myViewPager2.getCurrentItem()).department_name);
            //  textView.setText(db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(id)).department_name);
            Log.d(TAG, " textView.setText");
            if (activeItem != 0) {
                textViewQty.setVisibility(View.VISIBLE);
                textViewQty.setText(String.valueOf(activeItem));
            } else {
                textViewQty.setVisibility(View.GONE);
                if (db.dataDao().getAllNames(departmentID).size() < 2 && hideEmptyDepartmentPreference) {
                    setDepartmentInvisible(departmentID);
                } /*else {
                    setHideDepartmentAlertDialog(myViewPager2.getRootView());
                }*/
            }
            tab.setCustomView(tabView);
        }
        setStaticTabsVisibility();
    }

    //todo!!! bug если есть скрытые разделы, то перескакивает с раздела на раздел при переходе в режим редактирования и обратно
    private static void deleteAllItemInDepartment(DepartmentData departmentData) {

        db.dataDao().deleteAllDataByDepartmentID(departmentData.department_id);
        departmentData.CrossOutNumber = 0;
        db.departmentDataDao().update(departmentData);

        changeTabQty(departmentData.department_id);
        changeTotalActiveItemsCountInTab();
        setStaticTabsVisibility();
        viewPagerAdapter.notifyDataSetChanged();
    }

    private static void changeTabQty(int departmentID) {
        Log.d(TAG, "changeTabQty started");

        TabLayout.Tab tab = tabLayout.getTabAt(myViewPager2.getCurrentItem());
        View tabView = tab.getCustomView();
        TextView textViewQty = (TextView) tabView.findViewById(R.id.tvDepartmentsQty);

        int activeItem = db.dataDao().getAllNames(departmentID).size()
                - db.departmentDataDao().getDepartmentDataById(departmentID).CrossOutNumber - 1;

        if (activeItem != 0) {
            textViewQty.setVisibility(View.VISIBLE);
            textViewQty.setText(String.valueOf(activeItem));
        } else {
            textViewQty.setVisibility(View.GONE);
            if (db.dataDao().getAllNames(departmentID).size() < 2 && hideEmptyDepartmentPreference) {
                setDepartmentInvisible(departmentID);
            }
        }

        tab.setCustomView(tabView);
    }

    private void deleteSingleItemInDepartments(DepartmentData departmentData) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        db.departmentDataDao().deleteSingleData(departmentData.department_id, chosenListData.list_id);
        db.departmentDataDao().decrementValues(chosenListData.list_id, departmentData.department_position);

        changeTotalActiveItemsCountInTab();
        setTabsVisibility();
        viewPagerAdapter.notifyDataSetChanged();
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
        setEditButtonVisibility();
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
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        inputText = parserFilter(inputText);

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

                if (listName.isEmpty()) listName = getString(R.string.edit_list_name);

                if (!db.listDataDao().getAllNamesNotFlowable().contains(listName)) {
                    setNewList(listName);
                } else {
                    setUniqueListNameForParser(listName);
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
                    setNewDepartmentFromParse(departmentName, departmentPosition);
                } else {
                    setUniqueDepartmentForParser(departmentName, departmentPosition);
                }

                departmentName = "";
                index++;
                departmentPosition++;
                continue;
            } else if (s.equals("]") && index == 1) {
                index--;
                continue;
            } else if (index == 1) {
                departmentName += s;
            }

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
                dataName = "";
                dataQty = "";
                dataPosition++;
            } else if (s.equals("]") && index == 3) {
                if (!dataName.isEmpty())
                    setNewData(dataPosition, dataName, departmentPosition, Float.valueOf(dataQty));
                dataName = "";
                dataQty = "";
                index = 1;
                dataPosition = 1;
            } else if (index == 3) {
                dataQty += s;
            }
        }

        if (!listName.isEmpty()) setNewList(listName);
        if (!departmentName.isEmpty()) setNewDepartmentFromParse(departmentName, position);
        if (!dataName.isEmpty())
            setNewData(dataPosition, dataName, departmentPosition, Float.valueOf(dataQty));
    }

    private void logThisMethod(String name) {
        Log.d(TAG, name + " started");
    }

    private static void logThisMethodStatic(String name) {
        Log.d(TAG, name + " started");
    }
    private void setUniqueDepartmentForParser(String departmentName, int departmentPosition) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

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

    private void setUniqueListNameForParser(String listName) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

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

    private String parserFilter(String s) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

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
            for (String s : db.departmentDataDao().getAllVisibleDepartmentNames(chosenListData.list_id)) {

                int chosenDepartmentID = db.departmentDataDao().getChosenDepartment(departmentPosition, chosenListData.list_id).department_id;
                if ((db.dataDao().getAllForGenerator(chosenDepartmentID).size() - db.departmentDataDao().getDepartmentDataById(chosenDepartmentID).CrossOutNumber) != 0) {
                    stringToSend += s + "\n-*-\n";
                    int dataCounter = 0;
                    for (Data dataS : db.dataDao().getAllForGenerator(chosenDepartmentID)) {
                        if (dataCounter == (db.dataDao().getAllForGenerator(chosenDepartmentID).size() - db.departmentDataDao().getDepartmentDataById(chosenDepartmentID).CrossOutNumber)) {
                            /*Log.d(TAG, "BREAKER dataCounter: " + dataCounter + " size: " + db.dataDao().getAllForGenerator(chosenDepartmentID).size() + " CrossOutNumber: "
                                    + db.departmentDataDao().getDepartmentDataById(chosenDepartmentID).CrossOutNumber
                                    + " department position: " + departmentPosition);*/
                            break;
                        }
                       /* Log.d(TAG, "dataCounter: " + dataCounter + " size: " + db.dataDao().getAllForGenerator(chosenDepartmentID).size() +
                                " CrossOutNumber: " + db.departmentDataDao().getDepartmentDataById(chosenDepartmentID).CrossOutNumber
                                + " department position: " + departmentPosition);*/

                        Float data_qty_float = dataS.data_qty;
                        stringToSend += dataS.data_name + "-->" + data_qty_float.toString().replaceAll("\\.?0*$", "") + ";\n";
                        dataCounter++;

                    }
                    if (dataCounter > 0) {
                        stringToSend = stringToSend.substring(0, stringToSend.length() - 2);
                    } else {
                        stringToSend = stringToSend.substring(0, stringToSend.length() - 1);
                    }
                    //stringToSend += "]";
                    stringToSend += "\n---\n";
                }
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

       /* if (editButtonClicked) {
            popup.getMenu().findItem(R.id.menu_edit).setTitle(R.string.edit_list);
        } else {
            popup.getMenu().findItem(R.id.menu_edit).setTitle(R.string.stop_edit_list);
        }*/

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_share:
                        String stringToSend = listToStringGenerator();
                        newShare(item.getActionView(), stringToSend);
                        return true;
                    /*case R.id.menu_edit:
                        //addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
                        if (editButtonClicked) {
                            editButtonClicked = false;
                            addDepartmentButton.setVisibility(View.VISIBLE);
                            addDepartmentEndButton.setVisibility(View.VISIBLE);
                            holySpiritTV.setVisibility(View.VISIBLE);
                        } else {
                            editButtonClicked = true;
                            addDepartmentButton.setVisibility(View.GONE);
                            addDepartmentEndButton.setVisibility(View.GONE);
                            holySpiritTV.setVisibility(View.GONE);
                        }
                        setTabsVisibility();
                        viewPagerAdapter.notifyDataSetChanged();
                        // adapter.notifyDataSetChanged();
                        // adapterForDepartments.notifyDataSetChanged();
                        return true;*/
                    case R.id.menu_delete:
                        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                                // final AlertDialog dialog = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog)
                                .setMessage(getString(R.string.delete_list) + chosenListData.getList_name() + "'?")
                                .setCancelable(true)
                                .setPositiveButton(R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteSingleItemInList();
                                                dialog.cancel();
                                            }
                                        })
                                .setNegativeButton(
                                        R.string.no,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                //  .show();
                                .create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
                            }
                        });
                        dialog.show();
                        return true;
                    case R.id.menu_edit_name:
                        //addDepartmentButton = (ImageButton) findViewById(R.id.add_department_button);
                        editListName(view);
                        viewPagerAdapter.notifyDataSetChanged();
                        // adapter.notifyDataSetChanged();
                        // adapterForDepartments.notifyDataSetChanged();
                        return true;
                    case R.id.menu_import:
                        setAlertDialogForImport(view);


                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    private void setAlertDialogForImport(View view) {

        final EditText et = new EditText(view.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et.setHint(R.string.enter_text);

        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setMessage(R.string.import_list_dialog)
                .setView(et)
                .setCancelable(true)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String str = et.getText().toString();
                                if (!str.isEmpty()) parser(str);

                                updateNavigationDrawer();
                                viewPagerAdapter.notifyDataSetChanged();
                                if (moreMenuButton.getVisibility() == View.GONE) {
                                    moreMenuButton.setVisibility(View.VISIBLE);
                                }
                                setEditButtonVisibility();
                                setTabsVisibility();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(
                        R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create();
        setAlertDialogButtonsColor(view, dialog);
        dialog.show();
    }

    private void updateNavigationDrawer() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        setKeysForLists();
        selectedListIndex = 2;
        setNavigationDrawerData();
    }

    public void loadTheme() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(PREF_DARK_THEME, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void saveLanguage(String lang) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Language", lang);
        editor.commit();
        //   Log.d(TAG, "saved Theme = " + sharedPreferences.getInt("Theme",1));
        //  Log.d(TAG, "saved Theme =  " + theme);
    }

    public String loadLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        String lang = sharedPreferences.getString("Language", "en"); //en is default, when nothing is saved yet
        return lang;
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

        String title = getString(R.string.to_edit);
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
                .setPositiveButton(getString(R.string.ok), null)
                // .setNeutralButton("Следующее", null)
                .setNegativeButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // deleteFlagForEdit = false;
                                dialog.cancel();
                            }
                        })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.image_btn));
            }
        });
        dialog.show();
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
                    Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
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

    private static void createDataMoveSubMenu(View view, int dataID, int dataPosition) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }

        int depID = db.dataDao().getDepartmentIdByDataId(dataID);


        for (DepartmentData s : db.departmentDataDao().getAll(chosenListData.list_id)) {
            if (s.department_id != depID) popup.getMenu().add(s.department_name);
        }

        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DepartmentData destinationDepartmentData = db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(), chosenListData.list_id);

                Data newData = createNewDataForDestinationDepartment(item, view, destinationDepartmentData);
                insertDataToDestinationDepartment(newData, item);

                deleteMovedItemFromOldDepartment(dataPosition, dataID);

                setDepartmentVisible(destinationDepartmentData.department_id);
                hideDepartmentIfLastActiveItemOut(depID);

                Single.fromCallable(() -> notifyWithDelay(500)).subscribeOn(Schedulers.io()).subscribe();
                return false;
            }
        });
    }

    private static void deleteMovedItemFromOldDepartment(int dataPosition, int dataID) {
        deleteSingleItem(dataPosition, dataID);
    }

    private static void insertDataToDestinationDepartment(Data newData, MenuItem item) {
        db.dataDao().incrementValues(
                db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(),
                        chosenListData.list_id).department_id, 0);
        db.dataDao().insert(newData);
    }

    private static Data createNewDataForDestinationDepartment(MenuItem item, View view, DepartmentData destinationDepartmentData) {
        View parent = (View) view.getParent();
        TextView text = parent.findViewById(R.id.tvAnimalName);
        EditText dataQty = parent.findViewById(R.id.etAnimalCount);

        Data newData = new Data(
                destinationDepartmentData.department_id,
                1,
                text.getText().toString(),
                Float.parseFloat(dataQty.getText().toString()));
        return newData;
    }

    private static void hideDepartmentIfLastActiveItemOut(int depID) {

        int activeItem = db.dataDao().getAllNames(depID).size()
                - db.departmentDataDao().getDepartmentDataById(depID).CrossOutNumber - 1;

        if (activeItem == 0) {
            if (db.dataDao().getAllNames(depID).size() < 2 && hideEmptyDepartmentPreference) {
                setDepartmentInvisible(depID);
            } /*else {
                setHideDepartmentAlertDialog(myViewPager2.getRootView());
            }*/
        }
    }

    //TODO!!! При наличии скрытых разделов в списке, если выбрать в конце списка раздел при переходе из одного режима в другой перескакивает на другой раздел
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
                viewPagerAdapter.notifyItemChanged(0);
                //  viewPagerAdapter.notifyDataSetChanged();
            }
        });
        return 0;
    }

    public static void setHideEmptyDepartmentPreference(boolean status) {
        hideEmptyDepartmentPreference = status;
        Log.d(TAG, "setHideEmptyDepartmentPreference() " + hideEmptyDepartmentPreference);
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

//todo set current date like default in dialog edittext when new list in creating?

