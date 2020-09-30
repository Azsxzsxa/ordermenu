package com.example.ordermenu.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Adapters.RVMenuAvailableAdapter;
import com.example.ordermenu.Adapters.RVOrderHistoryAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Models.Order;
import com.example.ordermenu.Models.Table;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_CURRENT;
import static com.example.ordermenu.Utils.StrUtil.DB_HISTORY;
import static com.example.ordermenu.Utils.StrUtil.DB_MENU;
import static com.example.ordermenu.Utils.StrUtil.DB_ORDER;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLE_STATUS_BUSY;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLE_STATUS_FREE;

public class OrderHistoryActivity extends AppCompatActivity implements RVOrderHistoryAdapter.ItemClickListener {

    private static final String TAG = "OrderHistoryActivity";
    private RVOrderHistoryAdapter _orderHistoryAdapter;
    private List<Order> _orderItemList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        progressBar = findViewById(R.id.orderhistory_pb);
        getOrderHistory();
    }

    private void getOrderHistory() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(DB_HISTORY)
                .orderBy("endTime", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);

                _orderItemList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    _orderItemList.add(documentSnapshot.toObject(Order.class));
                }
                initRV();
            }
        });
    }


    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.orderhistory_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderHistoryActivity.this));
        _orderHistoryAdapter = new RVOrderHistoryAdapter(this, _orderItemList);
        _orderHistoryAdapter.setClickListener(this);
        recyclerView.setAdapter(_orderHistoryAdapter);
    }

    @Override
    public void onRestoreClick(View view, final int position) {
        final String sectionId = _orderItemList.get(position).getSectionId();
        final String tableId = _orderItemList.get(position).getTableId();

        //Display dialog
        final Dialog dialog = new Dialog(this, R.style.MyThemeDialogCustom);
        dialog.setContentView(R.layout.dialog_yes_no);

        TextView textView = dialog.findViewById(R.id.dialog_yesNo_text);
        MaterialButton yesBtn = dialog.findViewById(R.id.dialog_yesNo_yes_btn);
        MaterialButton noBtn = dialog.findViewById(R.id.dialog_yesNo_No_btn);

        textView.setText(R.string.question_restore_order);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                        .collection(DB_CURRENT).document(sectionId)
                        .collection(DB_TABLES).document(tableId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Table table = documentSnapshot.toObject(Table.class);
                                assert table != null;
                                if (table.getOccupied().equals(DB_TABLE_STATUS_FREE)) {
                                    //add the items to the restored order
                                    WriteBatch batch = Database.getInstance().getDb().batch();
                                    for (MenuItem menuItem : _orderItemList.get(position).getMenuItems()) {
                                        DocumentReference documentReference = Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                                                .collection(DB_CURRENT).document(sectionId)
                                                .collection(DB_TABLES).document(tableId)
                                                .collection(DB_ORDER)
                                                .document(menuItem.getDocument_id());
                                        batch.set(documentReference, menuItem);
                                    }

                                    //update table status
                                    batch.update(Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                                            .collection(DB_CURRENT).document(sectionId)
                                            .collection(DB_TABLES).document(tableId), "occupied", DB_TABLE_STATUS_BUSY);

                                    //delete order from history
                                    DocumentReference deleteFromHistory = Database.getInstance().restRef.document(Database.getInstance()
                                            .getRestaurantId()).collection(DB_HISTORY).document(_orderItemList.get(position).getDocumentId());
                                    batch.delete(deleteFromHistory);
                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            _orderHistoryAdapter.notifyItemRemoved(position);
                                            dialog.cancel();
                                        }
                                    });
                                } else {
                                    dialog.cancel();

                                    //Display dialog
                                    final Dialog innerDialog = new Dialog(OrderHistoryActivity.this, R.style.MyThemeDialogCustom);
                                    innerDialog.setContentView(R.layout.dialog_info_warning);

                                    TextView textView = innerDialog.findViewById(R.id.dialog_infoWarning_text);
                                    MaterialButton okBtn = innerDialog.findViewById(R.id.dialog_infoWarning_ok_btn);

                                    textView.setText(R.string.table_not_free);

                                    okBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            innerDialog.cancel();
                                        }
                                    });

                                    innerDialog.setCancelable(true);
                                    innerDialog.show();
                                }
                            }
                        });

            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}