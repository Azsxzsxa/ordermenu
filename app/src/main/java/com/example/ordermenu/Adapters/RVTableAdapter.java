package com.example.ordermenu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ordermenu.Models.Table;
import com.example.ordermenu.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVTableAdapter extends RecyclerView.Adapter<RVTableAdapter.ViewHolder> {
    private static final String TAG = "RVTableAdapter";

    private List<Table> mTableList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RVTableAdapter(Context context, List<Table> tableList) {
        this.mInflater = LayoutInflater.from(context);
        this.mTableList = tableList;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_tables_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String line = mTableList.get(position).getNumber() + "";
        holder.myTextView.setText(line);

        if (mTableList.get(position).getOccupied() != null && mTableList.get(position).getOccupied()) {
            Log.d(TAG, "onBindViewHolder: NOT NULL, occupied");
            holder.relativeLayout.setBackgroundResource(R.drawable.table_occupied_background);
        } else {
            holder.relativeLayout.setBackgroundResource(R.drawable.table_free_background);
            Log.d(TAG, "onBindViewHolder: null / not occupied");
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTableList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tables_number_tv);
            relativeLayout = itemView.findViewById(R.id.tables_relativeLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onTableClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mTableList.get(id).getNumber() + "";
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
