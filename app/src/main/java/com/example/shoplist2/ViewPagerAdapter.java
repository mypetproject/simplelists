package com.example.shoplist2;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder> {

    private Context context;

   // private List<Data> arrayList;
//private  List<DepartmentData> listOfDepartmentsData;
  //  private ItemClickListener mClickListener;

    private static final String TAG = "myLogs";

  //  public ViewPagerAdapter(Context context, List<Data> arrayList, List<DepartmentData> listOfDepartmentsData) {
    public ViewPagerAdapter(Context context) {
        Log.d(TAG, " MyAdapter constructor started");
        this.context = context;
     //   this.arrayList = arrayList;
      //  this.listOfDepartmentsData = listOfDepartmentsData;
        Log.d(TAG, " MyAdapter constructor ended");
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, " onCreateViewHolder started");
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager2_item, parent, false);
        Log.d(TAG, " onCreateViewHolder ended");
        return new ViewPagerHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        Log.d(TAG, "vp onBindViewHolder started, position: " + position);
        MainActivity.canUpdate = false;
        //если recyclable, то onMove не работает после перезаполнения
        holder.setIsRecyclable(false);
       int adapterPosition = holder.getAdapterPosition();
        final MyRecyclerViewAdapter adapter;
        ItemTouchHelper helper;
      //  Random rnd = new Random();
      //  int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
      //  holder.itemView.setBackgroundColor(color);
        List<Data> temp = new ArrayList<>();
        Log.d(TAG, " temp " + MainActivity.chosenListData.getList_name());
        temp.addAll(MainActivity.db.dataDao().getAll(
                MainActivity.db.departmentDataDao().getChosenDepartment(
                        adapterPosition,
                        MainActivity.chosenListData.list_id
                ).department_id));

        Log.d(TAG, " temp.addAll");
      // View parentView = (View) holder.itemView.getParent();
        RecyclerView recyclerView  = holder.itemView.findViewById(R.id.rvAnimals);
        recyclerView.getRecycledViewPool().clear();
                        adapter = new MyRecyclerViewAdapter(holder.itemView.getContext(), temp);
     //   holder.adapter = new MyRecyclerViewAdapter(parentView.getContext(), temp);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
      //  LinearLayoutManager layoutManager = new LinearLayoutManager(parentView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        // holder.adapter = new MyRecyclerViewAdapter(holder.itemView.getContext(), arrayList);
        //adapter = new MyRecyclerViewAdapter(itemView.getContext(), temp);
      //todo if editbutton clicked many times then holder formatting ruins if item decoration are working
        //  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(holder.recyclerView.getContext(),
      //          layoutManager.getOrientation());
       // holder.recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int id) {
                Log.d(TAG, position + " <- rv adapter position");
                MainActivity.ViewPagerItemClicked(view, id, adapter, position, temp);
            }

            @Override
            public void onItemTouch(View view, MotionEvent event, int id) {
               // Toast.makeText(view.getContext(), "item touched", Toast.LENGTH_SHORT).show();
                MainActivity.viewPagerOnTouchListener(view,event,id, adapter, temp);
              //  return 0;
            }
        });
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "adapter.setClickListener");

//todo после обновления холдеров ломается анимация перетаскивания и после первого перетаскивания меняется ViewPagerHolder и position

        helper = new ItemTouchHelper(new ItemTouchHelper.
                SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                Log.d(TAG, "adapter.onMove started" + " adapter position: " + adapterPosition);

                //adapter = (MyRecyclerViewAdapter) recyclerView.getAdapter();
                /*temp.clear();
                temp.addAll(MainActivity.db.dataDao().getAll(
                        MainActivity.db.departmentDataDao().getChosenDepartment(
                                position,
                                MainActivity.chosenListData.list_id
                        ).department_id));*/
                Log.d(TAG, "adapter.onMove started" + " adapter name: " + MainActivity.db.departmentDataDao().getDepartmentDataById(temp.get(0).department_id).department_name);
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();
                //Log.d(TAG, "adapter name: " + MainActivity.db.departmentDataDao().getDepartmentDataById(temp.get(position_dragged).department_id).department_name);
                int crossOutNumber = MainActivity.db.departmentDataDao().getDepartmentDataById(temp.get(position_dragged).department_id).CrossOutNumber;
                DepartmentData depData = MainActivity.db.departmentDataDao().getDepartmentDataById(temp.get(position_dragged).department_id);
                Log.d(TAG, "DepartmentData depData = ");
                Log.d(TAG, " if (position_dragged >= (temp.size() - crossOutNumber) started dep name: " + depData.department_name + " temp.size():"  + temp.size() + " crossOutNumber: " + crossOutNumber);
                if (position_dragged >= (temp.size() - crossOutNumber)
                        || position_dragged == 0) {
                   // position_target = position_dragged;
                    Log.d(TAG, " if (position_dragged >= (temp.size() - crossOutNumber)");
                    return false;
                } else if (position_target == 0) {
                  //  position_target = 1;
                    Log.d(TAG, "if (position_target == 0)");
                    return false;
                } else if (position_target >= (temp.size() - crossOutNumber)) {
                 //   position_target = temp.size() - crossOutNumber - 1;
                    Log.d(TAG, "if (position_target >= (temp.size() - crossOutNumber))");
                    return false;
                } else {
                    //todo возможно поменять
                    List<Data> oldTemp = new ArrayList<>();
                    oldTemp.addAll(temp);
                    Collections.swap(temp, position_dragged, position_target);

                    Log.d(TAG, " Collections.swap");
                  //adapter.notifyItemMoved(position_target, position_dragged);

                   ProductDiffUtilCallback productDiffUtilCallback =
                            new ProductDiffUtilCallback(oldTemp, temp);
                    DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);

                    //adapter.setData(productList);
                    productDiffResult.dispatchUpdatesTo(adapter);

                    //holder.adapter.notifyItemChanged(position_dragged);
                   // holder.adapter.notifyItemChanged(position_target);

                    Log.d(TAG, " holder.adapter.notifyItemMoved");
                }
                temp.clear();
                temp.addAll(MainActivity.db.dataDao().getAll(
                        MainActivity.db.departmentDataDao().getChosenDepartment(
                                position,
                                MainActivity.chosenListData.list_id
                        ).department_id));

               // holder.adapter.notifyItemMoved(position_target,position_dragged);


                Data tempData = MainActivity.db.dataDao().getChosenData(position_dragged, temp.get(position_dragged).department_id);
                Log.d(TAG, " Data tempData = ");
                tempData.data_position = position_target;
                if (position_dragged > position_target) {
                    MainActivity.db.dataDao().incrementValuesFromPositionToPosition(tempData.department_id, position_dragged, position_target);
                } else {
                    MainActivity.db.dataDao().decrementValuesFromPositionToPosition(tempData.department_id, position_dragged, position_target);
                }
                MainActivity.db.dataDao().update(tempData);
               // Log.d(TAG, "data keys after swipe: " + db.dataDao().getAllNames(chosenDepartmentData.department_id));
                Log.d(TAG, "adapter.onMove ended, pos_dragged: " + position_dragged + " pos_target: " + position_target
                 + "\nDataset: " + MainActivity.db.dataDao().getAllNames(depData.department_id));
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

    @Override
    public int getItemCount() {
       // return arrayList.size();
      //  Log.d(TAG, "getItemCount() started");
        int itemCount = 0;
        /*if (MainActivity.db.departmentDataDao().getAll(MainActivity.chosenListData.list_id).size() != 0) {
            itemCount = MainActivity.db.departmentDataDao().getAll(MainActivity.chosenListData.list_id).size();
        }*/
        if (MainActivity.db.departmentDataDao().getAll(MainActivity.chosenListData.list_id) != null) {
            itemCount = MainActivity.db.departmentDataDao().getAll(MainActivity.chosenListData.list_id).size();
        }
       // Log.d(TAG, "getItemCount() ended");
    return itemCount;
    }

    /*@Override
    public void onClick(View v) {
        Log.d(TAG, "vp clicked");
    }*/

    public class ViewPagerHolder extends RecyclerView.ViewHolder{


       // RecyclerView recyclerView;
      //  MyRecyclerViewAdapter adapter;
      //  ItemTouchHelper helper;

        public ViewPagerHolder(@NonNull View itemView) {

            super(itemView);
            Log.d(TAG, "public ViewPagerHolder(@NonNull View itemView)");
         //   itemView.setOnClickListener(this);
            Log.d(TAG, "itemView.setOnClickListener(this);");
         //   recyclerView  = itemView.findViewById(R.id.rvAnimals);

        }


      /*  @Override
        public void onClick(View v) {
            Log.d(TAG, "myAdapter clicked");
        }*/
    }
    // allows clicks events to be caught
    /*void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public interface ItemClickListener {
        void onItemClickView(View view, int id, int position);
        //  void onItemLongClick(int position,View view);
        //  int onItemTouch(View view, MotionEvent event, int position);
    }*/
}
