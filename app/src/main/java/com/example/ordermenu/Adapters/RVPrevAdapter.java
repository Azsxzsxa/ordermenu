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

    // data is passed into the constructor
    public RVPrevAdapter(Context context, List<MenuItem> prevList) {
        this.mInflater = LayoutInflater.from(context);
        this.prevList = prevList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_menureview_prev_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = prevList.get(position).getName();
        String quantity = String.valueOf(prevList.get(position).getQuantity());
        holder.itemName.setText(name);
        holder.itemQuantity.setText(quantity);
    }

    @Override
    public int getItemCount() {
        return prevList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            itemQuantity = itemView.findViewById(R.id.menuReview_prev_quantity_tv);
            itemName = itemView.findViewById(R.id.menuReview_prev_name_tv);
        }
    }
}
