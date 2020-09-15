package com.example.ordermenu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;

import java.util.List;

public class RVPrevAdapter extends RecyclerView.Adapter<RVPrevAdapter.ViewHolder> {
    private List<MenuItem> prevList;
    private LayoutInflater mInflater;
//    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RVPrevAdapter(Context context, List<MenuItem> prevList) {
        this.mInflater = LayoutInflater.from(context);
        this.prevList = prevList;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_menu_prev_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = prevList.get(position).getName();
        String quant = String.valueOf(prevList.get(position).getQuantity());
        holder.itemName.setText(name);
        holder.itemQuant.setText(quant);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return prevList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemQuant;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.TV_prev_name);
            itemQuant = itemView.findViewById(R.id.TV_prev_quant);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onTableClick(view, getAdapterPosition());
//        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return prevList.get(id).getDocument_id() + "";
    }

    // allows clicks events to be caught
//    public void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
//
//    // parent activity will implement this method to respond to click events
//    public interface ItemClickListener {
//        void onTableClick(View view, int position);
//    }
}
