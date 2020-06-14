package com.example.shoplist2;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.itemView.setBackgroundColor(color);
        List<Data> temp = new ArrayList<>();
        Log.d(TAG, " temp " + MainActivity.chosenListData.getList_name());
        temp.addAll(MainActivity.db.dataDao().getAll(
                MainActivity.db.departmentDataDao().getChosenDepartment(
                        position,
                        MainActivity.chosenListData.list_id
                ).department_id));

        Log.d(TAG, " temp.addAll");

        holder.adapter = new MyRecyclerViewAdapter(holder.itemView.getContext(), temp);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        // holder.adapter = new MyRecyclerViewAdapter(holder.itemView.getContext(), arrayList);
        //adapter = new MyRecyclerViewAdapter(itemView.getContext(), temp);
        holder.adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int id) {
                Log.d(TAG, position + " <- rv adapter position");
                MainActivity.ViewPagerItemClicked(view, id, holder.adapter, position, temp);
            }
        });

        Log.d(TAG, "adapter.setClickListener");
        holder.recyclerView.setAdapter(holder.adapter);
holder.adapter.notifyDataSetChanged();



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


        RecyclerView recyclerView;
        MyRecyclerViewAdapter adapter;

        public ViewPagerHolder(@NonNull View itemView) {

            super(itemView);
            Log.d(TAG, "public ViewPagerHolder(@NonNull View itemView)");
         //   itemView.setOnClickListener(this);
            Log.d(TAG, "itemView.setOnClickListener(this);");
            recyclerView  = itemView.findViewById(R.id.rvAnimals);

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
