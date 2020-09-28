package com.example.ordermenu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Models.Order;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.OrderUtil;
import com.example.ordermenu.Utils.Settings;
import com.example.ordermenu.Utils.StrUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class RVOrderHistoryAdapter extends RecyclerView.Adapter<RVOrderHistoryAdapter.ViewHolder> {
    private List<Order> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public RVOrderHistoryAdapter(Context context, List<Order> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_orderhistory_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String sectionName = mData.get(position).getTableSection();
        int tableNumber = mData.get(position).getTableNumber();
        Date startDate = mData.get(position).getStartTime();
        Date endDate = mData.get(position).getEndTime();

        holder.sectionName.setText(sectionName);
        holder.tableNumber.setText(String.valueOf(tableNumber));
        holder.startDate.setText(Settings.getShortDate(startDate));
        holder.endDate.setText(Settings.getShortDate(endDate));

        RVPrevAdapter _rvMenuPrevAdapter;
        holder.menuItemsRV.setLayoutManager(new LinearLayoutManager(context));
        _rvMenuPrevAdapter = new RVPrevAdapter(context, mData.get(position).getMenuItems());
        holder.menuItemsRV.setAdapter(_rvMenuPrevAdapter);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sectionName;
        TextView tableNumber;
        TextView startDate;
        TextView endDate;
        RecyclerView menuItemsRV;
        Button restoreBtn;

        ViewHolder(View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.orderhistory_item_sectionName_tv);
            tableNumber = itemView.findViewById(R.id.orderhistory_item_tableNumber_tv);
            startDate = itemView.findViewById(R.id.orderhistory_item_startDate_tv);
            endDate = itemView.findViewById(R.id.orderhistory_item_endDate_tv);
            menuItemsRV = itemView.findViewById(R.id.orderhistory_item_rv);
            restoreBtn = itemView.findViewById(R.id.orderhistory_item_restore_btn);
            restoreBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onRestoreClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    void getItem(int id) {
//        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onRestoreClick(View view, int position);
    }
}
