package com.example.ordermenu.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Models.Section;
import com.example.ordermenu.Models.Table;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;

public class RVOrderMoveAdapter extends RecyclerView.Adapter<RVOrderMoveAdapter.ViewHolder> implements RVTableAdapter.ItemClickListener {
    private static final String TAG = "OrderMoveActivity";
    private List<Section> mData;
    private LayoutInflater mInflater;
    private TableClick tableClickListener;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String sectionName = mData.get(position).getName();
        holder.sectionName.setText(sectionName);

        final ArrayList<Table> tables = new ArrayList<>();
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.DB_CURRENT).document(mData.get(position).getDocumentID())
                .collection(DB_TABLES).orderBy("number", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Table table = doc.toObject(Table.class);
                    table.setDocumentID(doc.getId());
                    table.setSection(position);
                    tables.add(table);
                }
                if (!tables.isEmpty()) {
                    mData.get(position).setTableList(tables);
                    RVTableAdapter tableAdapter;
                    holder.sectionRV.setLayoutManager(new GridLayoutManager(context, 4));
                    tableAdapter = new RVTableAdapter(context, tables);
                    tableAdapter.setClickListener(RVOrderMoveAdapter.this);
                    holder.sectionRV.setAdapter(tableAdapter);
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onTableClick(View view, int position, Table table) {
        Log.d(TAG, "onTableClick: " + position);
        tableClickListener.onMoveTableClick(view, position, table.getSection());
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
//            if (mClickListener != null) mClickListener.onTableClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(TableClick tableClickListener) {
        this.tableClickListener = tableClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface TableClick {
        void onMoveTableClick(View view, int tablePosition, int sectionPosition);
    }
}
