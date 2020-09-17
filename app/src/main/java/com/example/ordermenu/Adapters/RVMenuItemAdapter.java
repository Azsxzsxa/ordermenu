package com.example.ordermenu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class RVMenuItemAdapter extends RecyclerView.Adapter<RVMenuItemAdapter.ViewHolder> {
    private List<MenuItem> mDataFull;
    private List<MenuItem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RVMenuItemAdapter(Context context, List<MenuItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDataFull = new ArrayList<>(mData);
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_menuitems_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mData.get(position).getName();
        int quantity = mData.get(position).getQuantity();
        holder.itemName.setText(name);
        holder.itemQuantity.setText(String.valueOf(quantity));

        if (mData.get(position).getAvailable()) {
        holder.itemMinusBtn.setEnabled(true);
        holder.itemPlusBtn.setEnabled(true);
        holder.itemQuantity.setEnabled(true);
        holder.itemName.setEnabled(true);
        } else {
            holder.itemMinusBtn.setEnabled(false);
            holder.itemPlusBtn.setEnabled(false);
            holder.itemQuantity.setEnabled(false);
            holder.itemName.setEnabled(false);
        }

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
            itemName = itemView.findViewById(R.id.menuItems_name_tv);
            itemQuantity = itemView.findViewById(R.id.menuItems_quantity_tv);
            itemMinusBtn = itemView.findViewById(R.id.menuItems_minus_btn);
            itemPlusBtn = itemView.findViewById(R.id.menuItems_plus_btn);
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

    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<MenuItem> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mDataFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (MenuItem item : mDataFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mData.clear();
            mData.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

}
