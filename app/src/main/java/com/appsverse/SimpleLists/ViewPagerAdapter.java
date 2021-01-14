package com.appsverse.SimpleLists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder> {

    private Context context;
    private static final String TAG = "myLogs";

    public ViewPagerAdapter(Context context) {
        Log.d(TAG, " MyAdapter constructor started");
        this.context = context;
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, " onCreateViewHolder started");
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager2_item, parent, false);
        return new ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        Log.d(TAG, "vp onBindViewHolder started, position: " + position);

        int adapterPosition = holder.getAdapterPosition();
        final MyRecyclerViewAdapter adapter;
        ItemTouchHelper helper;
        List<Data> temp = setTempData(adapterPosition);

        MainActivity.canUpdate = false;

        //if recyclable, than onMove doesn't work after refilling
        holder.setIsRecyclable(false);

        RecyclerView recyclerView = holder.itemView.findViewById(R.id.rvAnimals);
        recyclerView.getRecycledViewPool().clear();
        adapter = new MyRecyclerViewAdapter(holder.itemView.getContext(), temp);

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());

        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int id) {
                MainActivity.ViewPagerItemClicked(view, id, adapter, position, temp);
            }

            @Override
            public void onItemTouch(View view, MotionEvent event, int id) {
                MainActivity.viewPagerOnTouchListener(view, event, id, adapter, temp);
            }
        });
        recyclerView.setAdapter(adapter);

        helper = new ItemTouchHelper(new ItemTouchHelper.
                SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                Log.d(TAG, "ViewPagerAdapter.onMove started" + " adapter position: " + adapterPosition);

                DepartmentData chosenDepartmentData = MainActivity.getChosenDepartmentData(position);

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                int crossOutNumber = chosenDepartmentData.CrossOutNumber;

                if (position_dragged >= (temp.size() - crossOutNumber)
                        || position_dragged == 0) {
                    return false;
                } else if (position_target == 0) {
                    return false;
                } else if (position_target >= (temp.size() - crossOutNumber)) {
                    return false;
                } else {
                    List<Data> oldTemp = new ArrayList<>();
                    oldTemp.addAll(temp);
                    Collections.swap(temp, position_dragged, position_target);

                    ProductDiffUtilCallback productDiffUtilCallback =
                            new ProductDiffUtilCallback(oldTemp, temp);
                    DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);
                    productDiffResult.dispatchUpdatesTo(adapter);
                }

                Data tempData = MainActivity.db.dataDao().getChosenData(position_dragged, chosenDepartmentData.department_id);

                if (position_dragged > position_target) {
                    MainActivity.db.dataDao().incrementValuesFromPositionToPosition(chosenDepartmentData.department_id, position_dragged, position_target);
                } else {
                    MainActivity.db.dataDao().decrementValuesFromPositionToPosition(chosenDepartmentData.department_id, position_dragged, position_target);
                }
                tempData.data_position = position_target;

                MainActivity.db.dataDao().update(tempData);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

        adapter.notifyDataSetChanged();

        MainActivity.canUpdate = true;
    }

    private List<Data> setTempData(int adapterPosition) {

        List<Data> temp = new ArrayList<>();

        if (!MainActivity.editButtonClicked) {
            temp.addAll(MainActivity.db.dataDao().getAll(
                    MainActivity.db.departmentDataDao().getChosenDepartment(
                            adapterPosition,
                            MainActivity.chosenListData.list_id
                    ).department_id));
        } else {
            List<Integer> allVisibleDepartmentsID = MainActivity.db.departmentDataDao().getAllVisibleDepartmentsID(MainActivity.chosenListData.list_id);
            temp.addAll(MainActivity.db.dataDao().getAll(
                    allVisibleDepartmentsID.get(adapterPosition)));
        }

        return temp;
    }

    @Override
    public int getItemCount() {

        int itemCount = 0;

        if (MainActivity.db.departmentDataDao().getAll(MainActivity.chosenListData.list_id) != null) {
            if (!MainActivity.editButtonClicked) {
                itemCount = MainActivity.db.departmentDataDao().getAll(MainActivity.chosenListData.list_id).size();
            } else {
                itemCount = MainActivity.db.departmentDataDao().getAllVisibleDepartmentsID(MainActivity.chosenListData.list_id).size();
            }
        }
        return itemCount;
     }

    public class ViewPagerHolder extends RecyclerView.ViewHolder {
        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
