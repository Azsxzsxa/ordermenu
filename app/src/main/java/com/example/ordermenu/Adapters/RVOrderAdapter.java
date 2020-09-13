package com.example.ordermenu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;

import java.util.ArrayList;

public class RVOrderAdapter extends RecyclerView.Adapter<RVOrderAdapter.ViewHolder> {
    private ArrayList<MenuItem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RVOrderAdapter(Context context, ArrayList<MenuItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_menuitem_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mData.get(position).getName();
        int quantity = mData.get(position).getQuantity();
        holder.itemName.setText(name);
        holder.itemQuantity.setText(String.valueOf(quantity));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName;
        TextView itemQuantity;
        Button itemMinusBtn;
        Button itemPlusBtn;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.tv_quantity);
            itemMinusBtn = itemView.findViewById(R.id.btn_minus);
            itemPlusBtn = itemView.findViewById(R.id.btn_plus);
            itemMinusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onMinusClick(v, getAdapterPosition());
                }
            });
            itemPlusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onPlusClick(v, getAdapterPosition());
                }
            });

        }


        @Override
        public void onClick(View v) {
        }
    }

    // convenience method for getting data at click position
    MenuItem getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onPlusClick(View view, int position);

        void onMinusClick(View view, int position);
    }

    public void updateList(ArrayList<MenuItem> newList) {
        mData = newList;
        notifyDataSetChanged();
    }
}
