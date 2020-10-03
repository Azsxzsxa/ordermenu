package com.example.ordermenu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Models.Order;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.Models.Table;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RVOrderMoveAdapter extends RecyclerView.Adapter<RVOrderMoveAdapter.ViewHolder> {
    private List<Section> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public RVOrderMoveAdapter(Context context, List<Section> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_ordermove_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String sectionName = mData.get(position).getName();
        holder.sectionName.setText(sectionName);


        ArrayList<Table> tables = new ArrayList<>();
        for (int tableNumber = 1; tableNumber < mData.get(position).getTableCount(); tableNumber++) {
            Table table = new Table(tableNumber,null,null,null);
            tables.add(table);
        }
        if (!tables.isEmpty()) {
            RVTableAdapter tableAdapter;
            holder.sectionRV.setLayoutManager(new GridLayoutManager(context,4));
            tableAdapter = new RVTableAdapter(context, tables);
            holder.sectionRV.setAdapter(tableAdapter);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sectionName;
        RecyclerView sectionRV;

        ViewHolder(View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.ordermove_item_title_tv);
            sectionRV = itemView.findViewById(R.id.ordermove_item_rv);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onTableClick(view, getAdapterPosition());
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
        void onTableClick(View view, int position);
    }
}
