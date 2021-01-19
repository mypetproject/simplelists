package com.appsverse.SimpleLists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
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
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    static MyRecyclerViewAdapter adapter;
    static boolean editButtonClicked = true;
    static int adapterPosition;

    private Drawer drawerResult = null;
    private int selectedListIndex = 2;

    Toolbar toolbar;

    static ListDataDatabase db;
    static ListData chosenListData = new ListData();

    int parentID;
    int parentParentID;

    static boolean canUpdate;

    static ViewPagerAdapter viewPagerAdapter;
    static ViewPager2 myViewPager2;
    static TabLayout tabLayout;

    //for OnItemTouch
    private static final int MAX_CLICK_DURATION = 250;
    private static long startClickTime;
    private static long clickDuration;

    private static final String TAG = "myLogs";

    private static List<Data> adapterListData;
    ImageButton addDepartmentButton;
    ImageButton addDepartmentEndButton;
    TextView holySpiritTV;
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

        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setPreferences();
        setContentView(R.layout.activity_main);

        setToolbar();
        setFirstElementOfList();
        setFirstElementInAllSections();

        if (db.listDataDao().getAllNamesNotFlowable().size() > 1)
            chosenListData = db.listDataDao().getChosenList(1);
        setNavigationDrawerData();

        setViewPager(this);
        setTabs();
        setEditButton();

        holySpiritTV = findViewById(R.id.holy_spirit_tv);

        setStartAndEndAddDepartmentButtons();
        setTabsVisibility();
        changeTotalActiveItemsCountInTab();

        //for bug with tab text color when app started
        viewPagerAdapter.notifyDataSetChanged();
    }

    private void setStartAndEndAddDepartmentButtons() {
        logThisMethod(Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName());

        addDepartmentButton = findViewById(R.id.add_department_button);
        addDepartmentButton.setOnClickListener(v -> {
            Log.d(TAG, "setStartAndEndAddDepartmentButtons() onClick addDepartmentButton started");
            inputTextDialogWindow(v, 1, 15);
        });

        addDepartmentEndButton = findViewById(R.id.add_department_button_in_the_end);
        addDepartmentEndButton.setOnClickListener(v -> {
            Log.d(TAG, "setStartAndEndAddDepartmentButtons() onClick addDepartmentEndButton started");
            inputTextDialogWindow(v, 1, 15);
        });
    }

    private void setEditButton() {
        logThisMethod(Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName());

        editButton = findViewById(R.id.edit_button);
        setEditButtonVisibility();

        editButton.setOnClickListener(v -> {


            editButtonClicked = !editButtonClicked;
            setEditModeButtonsVisibility();
            setTabsVisibility();

            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            viewPagerAdapter.notifyDataSetChanged();
        });
    }

    private void setEditModeButtonsVisibility() {
        logThisMethod(Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName());

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
        logThisMethod(Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName());

        tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, myViewPager2, (tab, position) -> {
            Log.d(TAG, "setTabs() onConfigureTab started");
            DepartmentData currentDepartment = getCurrentDepartment(position);

            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_tab, null);
            TextView textView = (TextView) view.findViewById(R.id.tvDepartmentsName);
            TextView textViewQty = (TextView) view.findViewById(R.id.tvDepartmentsQty);

            textView.setText(currentDepartment.department_name);

            setDepartmentsItemQtyInTabs(textViewQty, currentDepartment);

            tab.setCustomView(view);
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
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

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

    //todo BUG when theme changed and main activity recreated toolbar items gravity change to center mode
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
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        hideEmptyDepartmentPreference = sharedPreferences.getBoolean(PREF_HIDE_EMPTY_DEPARTMENT, true);
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
        if (db.listDataDao().getAllNamesNotFlowable().size() < 2) {
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
                        changeTotalActiveItemsCountInTab();
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

    //TODO!!! оптимизировать метод ниже
    public static void ViewPagerItemClicked(View view, int dataID, MyRecyclerViewAdapter adapter, int position, List<Data> adapterData) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        setAdapter(adapter);
        setAdapterData(adapterData);

        if (editButtonClicked) {
            DepartmentData temp = db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(dataID));

            if (position < (db.dataDao().getAllNames(temp.department_id).size() - temp.CrossOutNumber)) {
                temp.CrossOutNumber++;
                moveItemToBottom(dataID, position);
            } else {
                temp.CrossOutNumber--;
                moveItemToTop(dataID, position);
            }
            db.departmentDataDao().update(temp);

            TabLayout.Tab tab = tabLayout.getTabAt(myViewPager2.getCurrentItem());
            View tabView = tab.getCustomView();
            TextView textViewQty = (TextView) tabView.findViewById(R.id.tvDepartmentsQty);

            int activeItem = db.dataDao().getAllNames(temp.department_id).size() - temp.CrossOutNumber - 1;

            if (activeItem != 0) {
                textViewQty.setVisibility(View.VISIBLE);
                textViewQty.setText(String.valueOf(activeItem));
                tab.setCustomView(tabView);
            } else if (hideEmptyDepartmentPreference) {
                setDepartmentInvisible(getChosenDepartmentID(myViewPager2.getCurrentItem()));
                viewPagerAdapter.notifyDataSetChanged();
            } else {
                textViewQty.setVisibility(View.GONE);
                tab.setCustomView(tabView);
            }

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
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

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
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

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
        //List<DepartmentData> listOfDepartmentsData = new ArrayList<DepartmentData>(db.departmentDataDao().getAll(chosenListData.list_id));
        List<DepartmentData> listOfDepartmentsData = new ArrayList<DepartmentData>(db.departmentDataDao().getAllVisibleDepartmentData(chosenListData.list_id));
        int sumOfActive = 0;

        for (DepartmentData dd : listOfDepartmentsData) {
            sumOfActive += db.dataDao().getAll(dd.department_id).size() - dd.CrossOutNumber - 1;
        }

        return sumOfActive;
    }

    public static void viewPagerOnTouchListener(View view, MotionEvent event, int id, MyRecyclerViewAdapter adapter, List<Data> adapterData) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        setAdapter(adapter);
        setAdapterData(adapterData);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                clickDuration = 0;
                startClickTime = Calendar.getInstance().getTimeInMillis();

                switch (view.getId()) {
                    case R.id.image_to_low:

                        if (!stopClick)
                            Single.fromCallable(() -> itemCount(false, id, view)).subscribeOn(Schedulers.io()).subscribe();
                        break;
                    case R.id.image_to_high:
                        if (!stopClick)
                            Single.fromCallable(() -> itemCount(true, id, view)).subscribeOn(Schedulers.io()).subscribe();
                        break;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                adapterListData.clear();
                adapterListData.addAll(db.dataDao().getAll(
                        db.dataDao().getDepartmentIdByDataId(id)
                ));

                adapter.notifyDataSetChanged();
                break;
            case MotionEvent.ACTION_CANCEL:
                clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                adapterListData.clear();
                adapterListData.addAll(db.dataDao().getAll(
                        db.dataDao().getDepartmentIdByDataId(id)
                ));
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private static int itemCount(boolean sign, int id, View view) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        stopClick = true;
        while (clickDuration == 0) {
            if (sign) {
                db.dataDao().plusQty(id);
            } else {
                db.dataDao().minusQty(id);
            }

            mn.runOnUiThread(new Runnable() {
                @Override
                public void run() {

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
                }
            });
            SystemClock.sleep(200);
        }
        stopClick = false;
        return 0;
    }

    private static void moveItemToTop(int id, int fromPosition) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        List<Data> oldTemp = new ArrayList<>();
        oldTemp.addAll(adapterListData);

        db.dataDao().incrementValuesFromOneToPosition(db.dataDao().getDepartmentIdByDataId(id), fromPosition);
        db.dataDao().updateSingleItemPosition(id, 1);

        adapterListData.clear();
        adapterListData.addAll(db.dataDao().getAll(db.dataDao().getDepartmentIdByDataId(id)));

        ProductDiffUtilCallback productDiffUtilCallback =
                new ProductDiffUtilCallback(oldTemp, adapterListData);
        DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);

        productDiffResult.dispatchUpdatesTo(adapter);
        adapter.notifyItemChanged(1);
    }

    private static void moveItemToBottom(int dataID, int position) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        List<Data> oldTemp = new ArrayList<>();
        int newDataPosition = db.dataDao().getAllPositions(db.dataDao().getDepartmentIdByDataId(dataID)).size();

        oldTemp.addAll(adapterListData);

        db.dataDao().updateSingleItemPosition(db.dataDao().getChosenDataById(dataID).data_id, newDataPosition);
        db.dataDao().decrementValues(db.dataDao().getDepartmentIdByDataId(dataID), position);

        adapterListData.clear();
        adapterListData.addAll(db.dataDao().getAll(db.dataDao().getDepartmentIdByDataId(dataID)));

        ProductDiffUtilCallback productDiffUtilCallback =
                new ProductDiffUtilCallback(oldTemp, adapterListData);
        DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);

        productDiffResult.dispatchUpdatesTo(adapter);
        adapter.notifyItemChanged(newDataPosition - 1);
    }

    private static void inputTextDialogWindowForViewHolderItem(View view, int position, int dataID) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        String title;
        final EditText et = setEditText(view);

        if (position != 0) {
            editFlag = true;
            et.setText(db.dataDao().getChosenDataById(dataID).data_name + " ");
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

                String str = et.getText().toString();
                str = deleteLeftRightSpacesInItem(str);

                if (!str.isEmpty()) {
                    if (editFlag) {
                        editOldData(str, dataID);
                    } else {
                        if (position == 0) {
                            setNewData(str, 1, dataID);
                        } else {
                            setNewData(str, position, dataID);
                        }
                    }

                    adapterListData.clear();
                    adapterListData.addAll(db.dataDao().getAll(
                            db.dataDao().getDepartmentIdByDataId(dataID)
                    ));

                    viewPagerAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et.getText().toString();
                str = deleteLeftRightSpacesInItem(str);

                if (!str.isEmpty()) {
                    if (editFlag) {
                        editOldData(str, dataID);
                        editFlag = false;
                        dialog.setTitle(view.getContext().getString(R.string.add));
                    } else {
                        if (position == 0) {
                            setNewData(str, 1, dataID);
                        } else {
                            setNewData(str, position, dataID);
                        }
                    }
                    et.setText("");

                    adapterListData.clear();
                    adapterListData.addAll(db.dataDao().getAll(db.dataDao().getDepartmentIdByDataId(dataID)));

                    viewPagerAdapter.notifyDataSetChanged();
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
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        Data tempData = db.dataDao().getChosenDataById(id);
        tempData.data_name = str;
        db.dataDao().update(tempData);
    }


    public static void setAdapterData(List<Data> adapterData) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        adapterListData = adapterData;
    }


    public static void setAdapter(MyRecyclerViewAdapter getAdapter) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        adapter = getAdapter;
    }

    private void newShare(String stringToShare) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Поделиться"));
    }

    private int activeQtyForList(int position) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        List<DepartmentData> listOfDepartmentsData = new ArrayList<DepartmentData>(db.departmentDataDao().getAll(db.listDataDao().getChosenList(position).list_id));

        int sumOfActive = 0;

        for (DepartmentData dd : listOfDepartmentsData) {
            sumOfActive += MainActivity.db.dataDao().getAll(dd.department_id).size() - dd.CrossOutNumber - 1;
        }

        return sumOfActive;
    }

    void setNavigationDrawerData() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

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

                        View parent = (View) view.getParent();
                        parentID = parent.getId();
                        View parentParent = (View) view.getParent();
                        parentParentID = parentParent.getId();

                        if (position == 1) {
                            inputTextDialogWindow(view, 1, 18);
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

        if (db.listDataDao().getAllNamesNotFlowable().size() > 1) {
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
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        List<String> listsNames = db.listDataDao().getAllNamesNotFlowable();
        IDrawerItem[] iDrawerItems = new IDrawerItem[listsNames.size()];

        for (int i = 0; i < listsNames.size(); i++) {
            int activeQty = activeQtyForList(i);
            iDrawerItems[i] = setBadgeStyle(listsNames.get(i), activeQty, i);
        }

        return iDrawerItems;
    }

    private IDrawerItem setBadgeStyle(String name, int activeQty, int i) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        if (activeQty > 0 && i != 0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPreferences.getBoolean(PREF_DARK_THEME, true)) {
                return new PrimaryDrawerItem().withIdentifier(i).withName(name).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
            } else {
                return new PrimaryDrawerItem().withIdentifier(i).withName(name).withBadge(activeQty + "").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_700).withCornersDp(16));
            }
        } else {
            return new PrimaryDrawerItem().withIdentifier(i).withName(name).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_grey_400).withCornersDp(16));
        }
    }

    void setActiveList(int position) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        if (db.listDataDao().getAllNamesNotFlowable().size() > 1) {
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

    //for adding section, list and when create new list without loading default data
    public void inputTextDialogWindow(final View view, final int insertIndex, int maxTextLength) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        String title = setTitleForDialogWindow(view);
        final EditText et = setEditTextForInputTextDialogWindow(view);

        AlertDialog dialog = createAlertDialog(view, title, et);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logThisMethod(new Object() {
                }.getClass().getEnclosingMethod().getName() + " inputTextDialogWindow positiveButton");

                String str = deleteLeftAndRightSpaces(et.getText().toString());
                boolean isItShare = str.contains("-=***=-");

                if (str.length() > 0) {

                    if (str.length() <= maxTextLength || (isItShare && view.getId() != R.id.add_department_button
                            && view.getId() != R.id.add_department_button_in_the_end)) {

                        tryingToWriteTextToBase(view, dialog, insertIndex, str);

                    } else {
                        Toast.makeText(MainActivity.this, R.string.too_large_name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.too_small_name, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logThisMethod(new Object() {
                }.getClass().getEnclosingMethod().getName() + "  inputTextDialogWindow neutralButton");

                String str = et.getText().toString();
                str = deleteLeftAndRightSpaces(str);
                if (!str.isEmpty()) {
                    if (str.length() <= maxTextLength) {
                        tryingToWriteTextToBaseByPushNeutralButton(str, view, insertIndex, et);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.too_large_name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.too_small_name, Toast.LENGTH_SHORT).show();
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
                if (s.toString().length() > maxTextLength-4) {
                    dialog.setTitle(title + "                     " + (maxTextLength - s.toString().length()));
                } else {
                    dialog.setTitle(title);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void tryingToWriteTextToBaseByPushNeutralButton(String str, View view, int insertIndex, EditText et) {


        if (uniqueTest(str, view)) {

            insertFromPopup(str, insertIndex, view);

            et.getText().clear();
            et.setHint(getString(R.string.enter_text));

            setTabsOnLongClickListener();
        } else {
            Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
        }


    }

    private void tryingToWriteTextToBase(View view, AlertDialog dialog, int insertIndex, String str) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        if (uniqueTest(str, view)) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

            insertFromPopup(str, insertIndex, view);
            setTabsOnLongClickListener();
            changeTotalActiveItemsCountInTab();
            dialog.dismiss();
        } else {
            Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
        }
    }

    private AlertDialog createAlertDialog(View view, String title, EditText et) {
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
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();

        setAlertDialogButtonsColor(view, dialog);

        return dialog;
    }

    private String setTitleForDialogWindow(View view) {
        String title;
        if (view.getId() == R.id.add_department_button
                || view.getId() == R.id.add_department_button_in_the_end) {
            title = getString(R.string.add_department);
        } else {
            title = getString(R.string.add_new_list);
        }
        return title;
    }

    private EditText setEditTextForInputTextDialogWindow(View view) {
        final EditText et = new EditText(view.getContext());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et.setHint(getString(R.string.enter_text));

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        return et;
    }

    private static EditText setEditText(View view) {
        final EditText et = new EditText(view.getContext());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        et.setLayoutParams(lp);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        return et;
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
        logThisMethodStatic(new Object() {
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

    public void editDepartmentDialogWindow(final View view, DepartmentData departmentData) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, name + " started");

        int insertIndex = departmentData.department_position;

        View parentParent = (View) view.getParent().getParent();
        parentParentID = parentParent.getId();

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

                if (str.length() > 0) {
                    if (str.length() < 12) {
                        if (uniqueTest(str, view)) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                            insertFromPopup(str, insertIndex, view);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), R.string.too_large_name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), R.string.too_small_name, Toast.LENGTH_SHORT).show();
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

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        return et;
    }

    boolean uniqueTest(String str, View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

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

        myViewPager2.setCurrentItem(0);
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

    private static void setNewData(String s, int position, int dataID) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        int departmentID = db.dataDao().getChosenDataById(dataID).department_id;

        Data newData = new Data(departmentID, position, s, 0.0f);
        db.dataDao().incrementValues(departmentID, position - 1);
        db.dataDao().insert(newData);

        setDepartmentVisible(departmentID);
        changeTotalActiveItemsCountInTab();
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

    private void insertFromPopup(String s, int insertIndex, View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

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
                setAlertDialogWhenNewListCreated(view);
            }

            viewPagerAdapter.notifyDataSetChanged();
            setEditButtonVisibility();
        }
    }

    private void setAlertDialogWhenNewListCreated(View view) {
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
                                changeTotalActiveItemsCountInTab();
                            }
                        })
                .setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                editButtonClicked = false;
                                setEditModeButtonsVisibility();
                                setTabsVisibility();

                                inputTextDialogWindow(findViewById(R.id.add_department_button), 1,18);
                                dialog.cancel();
                            }
                        })
                .create();
        setAlertDialogButtonsColor(view, dialog);
        dialog.show();
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

    private static void deleteSingleItem(int position, int dataID) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        DepartmentData department = db.departmentDataDao().getDepartmentDataById(db.dataDao().getDepartmentIdByDataId(dataID));

        if (position >= (db.dataDao().getAllNames(department.department_id).size() - department.CrossOutNumber)) {
            department.CrossOutNumber--;
            db.departmentDataDao().update(department);
        }

        if (position > 0) {
            db.dataDao().deleteSingleDataById(dataID);
            db.dataDao().decrementValues(department.department_id, position);

            adapterListData.clear();
            adapterListData.addAll(db.dataDao().getAll(department.department_id));

            adapter.notifyItemRemoved(position);

            TabLayout.Tab tab = tabLayout.getTabAt(myViewPager2.getCurrentItem());
            View tabView = tab.getCustomView();
            TextView textViewQty = (TextView) tabView.findViewById(R.id.tvDepartmentsQty);

            int activeItem = db.dataDao().getAllNames(department.department_id).size()
                    - department.CrossOutNumber - 1;

            if (activeItem != 0) {
                textViewQty.setVisibility(View.VISIBLE);
                textViewQty.setText(String.valueOf(activeItem));
            } else {
                textViewQty.setVisibility(View.GONE);
                if (db.dataDao().getAllNames(department.department_id).size() < 2 && hideEmptyDepartmentPreference) {
                    setDepartmentInvisible(department.department_id);
                }
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

        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 0) myViewPager2.setCurrentItem(myViewPager2.getCurrentItem());
        viewPagerAdapter.notifyDataSetChanged();
    }

    private void deleteSingleItemInList() {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        int position = chosenListData.list_position;

        if (position > 0 && db.listDataDao().getAllNamesNotFlowable().size() > 2) {
            db.listDataDao().deleteSingleItem(chosenListData.list_id);
            db.listDataDao().decrementValues(chosenListData.list_position);


            if (position != 1) {
                setActiveList(position);
            } else {
                setActiveList(position + 1);
            }

        } else if (position > 0) {
            db.listDataDao().deleteSingleItem(chosenListData.list_id);
        }

        setTabsVisibility();
        setEditButtonVisibility();
        changeTotalActiveItemsCountInTab();
        setNavigationDrawerData();
        viewPagerAdapter.notifyDataSetChanged();
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
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        String stringToSend = chosenListData.getList_name() + "\n-=***=-\n";

        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size() > 0)
            for (String s : db.departmentDataDao().getAllVisibleDepartmentNames(chosenListData.list_id)) {
                int chosenDepartmentID = db.departmentDataDao().getChosenDepartmentByName(s, chosenListData.list_id).department_id;

                if ((db.dataDao().getAllForGenerator(chosenDepartmentID).size() - db.departmentDataDao().getDepartmentDataById(chosenDepartmentID).CrossOutNumber) != 0) {

                    stringToSend += s + "\n-*-\n";
                    int visibleItemsCounter = 0;

                    for (Data itemData : db.dataDao().getAllForGenerator(chosenDepartmentID)) {

                        if (visibleItemsCounter == (db.dataDao().getAllForGenerator(chosenDepartmentID).size() - db.departmentDataDao().getDepartmentDataById(chosenDepartmentID).CrossOutNumber)) {
                            break;
                        }

                        Float data_qty_float = itemData.data_qty;
                        stringToSend += itemData.data_name + "-->" + data_qty_float.toString().replaceAll("\\.?0*$", "") + ";\n";
                        visibleItemsCounter++;
                    }

                    if (visibleItemsCounter > 0) {
                        stringToSend = stringToSend.substring(0, stringToSend.length() - 2);
                    } else {
                        stringToSend = stringToSend.substring(0, stringToSend.length() - 1);
                    }
                    stringToSend += "\n---\n";
                }
            }

        stringToSend += "--=*=--";

        Log.d(TAG, "StringToSend ready " + stringToSend);
        return generatorFilter(stringToSend);
    }

    private String generatorFilter(String s) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        s = s.replace('[', '(');
        s = s.replace(']', ')');
        return s;
    }

    //menu in toolbar on right side
    public void onMoreMenuItemButtonClick(View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        PopupMenu popup = setPopupMenuForMenuButton(view);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_settings:
                        editButtonClicked = true;
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);

                        return true;

                    case R.id.menu_share:
                        String stringToSend = listToStringGenerator();
                        newShare(stringToSend);
                        return true;

                    case R.id.menu_delete:
                        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
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
                                .create();
                        setAlertDialogButtonsColor(view, dialog);
                        dialog.show();
                        return true;
                    case R.id.menu_edit_name:
                        editListName(view);
                        viewPagerAdapter.notifyDataSetChanged();
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

    private PopupMenu setPopupMenuForMenuButton(View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.popup_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }

        setMoreMenuItemsVisibility(popup);

        popup.show();
        return popup;
    }

    private void setMoreMenuItemsVisibility(PopupMenu popup) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        Menu popupMenu = popup.getMenu();
        if (db.listDataDao().getAllNames().size() < 2) {
            popupMenu.findItem(R.id.menu_delete).setVisible(false);
            popupMenu.findItem(R.id.menu_edit_name).setVisible(false);
            popupMenu.findItem(R.id.menu_share).setVisible(false);
        } else {
            popupMenu.findItem(R.id.menu_delete).setVisible(true);
            popupMenu.findItem(R.id.menu_edit_name).setVisible(true);
            popupMenu.findItem(R.id.menu_share).setVisible(true);
        }
    }

    private void setAlertDialogForImport(View view) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        final EditText et = setEditTextForInputTextDialogWindow(view);

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
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        String title = getString(R.string.to_edit);
        final EditText et = setEditTextForInputTextDialogWindow(view);
        et.setText(chosenListData.getList_name());


        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(title)
                .setCancelable(true)
                .setView(et)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                String str = et.getText().toString();

                if (str.length() > 0) {
                    if (str.length() < 19) {
                        if (uniqueTest(str, view)) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                            saveEditedListName(str);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(view.getContext(), R.string.unique_alert, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), R.string.too_large_name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), R.string.too_small_name, Toast.LENGTH_SHORT).show();
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

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 14) {
                    dialog.setTitle(title + "                     " + (18 - s.toString().length()));
                } else {
                    dialog.setTitle(title);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void saveEditedListName(String str) {
        logThisMethod(new Object() {
        }.getClass().getEnclosingMethod().getName());

        ListData newListData = chosenListData;
        newListData.setList_name(str);
        db.listDataDao().update(newListData);
        setNavigationDrawerData();
    }

    private static void dataHolderMenuItemButtonClick(View view, int position, int dataID) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.data_popup_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.data_menu_delete:
                        deleteSingleItem(position, dataID);

                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        return true;
                    case R.id.data_menu_edit:
                        inputTextDialogWindowForViewHolderItem(view, position, dataID);
                        return true;
                    case R.id.data_menu_move:
                        if (db.departmentDataDao().getAllNames(chosenListData.list_id).size()>1) {
                            createDataMoveSubMenu(view, dataID, position);
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    private static void createDataMoveSubMenu(View view, int dataID, int dataPosition) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        int depID = db.dataDao().getDepartmentIdByDataId(dataID);

        PopupMenu popup = setPopupMenuForDataMove(view, depID);
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DepartmentData destinationDepartmentData = db.departmentDataDao().getChosenDepartmentByName(item.getTitle().toString(), chosenListData.list_id);

                Data newData = createNewDataForDestinationDepartment(view, destinationDepartmentData);
                insertDataToDestinationDepartment(newData, item);

                deleteMovedItemFromOldDepartment(dataPosition, dataID);

                setDepartmentVisible(destinationDepartmentData.department_id);
                hideDepartmentIfLastActiveItemOut(depID);

                Single.fromCallable(() -> notifyWithDelay(500)).subscribeOn(Schedulers.io()).subscribe();
                return false;
            }
        });
    }

    private static PopupMenu setPopupMenuForDataMove(View view, int depID) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }

        for (DepartmentData s : db.departmentDataDao().getAll(chosenListData.list_id)) {
            if (s.department_id != depID) popup.getMenu().add(s.department_name);
        }
        return popup;
    }

    private static void deleteMovedItemFromOldDepartment(int dataPosition, int dataID) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        deleteSingleItem(dataPosition, dataID);
    }

    private static void insertDataToDestinationDepartment(Data newData, MenuItem item) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        db.dataDao().incrementValues(newData.department_id, 0);
        db.dataDao().insert(newData);
    }

    private static Data createNewDataForDestinationDepartment(View view, DepartmentData destinationDepartmentData) {
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

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
        logThisMethodStatic(new Object() {
        }.getClass().getEnclosingMethod().getName());

        int activeItem = db.dataDao().getAllNames(depID).size()
                - db.departmentDataDao().getDepartmentDataById(depID).CrossOutNumber - 1;

        if (activeItem == 0) {
            if (db.dataDao().getAllNames(depID).size() < 2 && hideEmptyDepartmentPreference) {
                setDepartmentInvisible(depID);
            }
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

    public static void setHideEmptyDepartmentPreference(boolean status) {
        hideEmptyDepartmentPreference = status;
        Log.d(TAG, "setHideEmptyDepartmentPreference() " + hideEmptyDepartmentPreference);
    }

}


//todo Добавление списков, отделов, элементов с помощью google assistant
//todo Обучение интерфейсу при первом старте


//todo блокировка списка отпечатком и пинкодом
//todo поиск по списку?

//todo аттач фото к элементу отдела


//todo проверка на hardware клавиатуру при вызове alertdialog для корректировки или добавления элемента (те, где есть edittext)


//todo по лонгтапу по элементу отдела появляется чекбокс, где можно выделить элементы и удалить несколько сразу

//todo set current date like default in dialog edittext when new list in creating?

