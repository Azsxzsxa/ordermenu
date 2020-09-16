package com.example.ordermenu.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.ordermenu.Adapters.RVOrderAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.OrderUtil;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ordermenu.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity implements RVOrderAdapter.ItemClickListener {

    ArrayList<MenuItem> _menuItemList = new ArrayList<>();
    RVOrderAdapter _rvOrderAdapter;

    String _section_doc_id = OrderUtil.getInstance().getSectionDocID();
    String _table_doc_id = OrderUtil.getInstance().getTableDocID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        FloatingActionButton toMenuFab = findViewById(R.id.order_toMenu_fab);
        toMenuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MenuActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton clearTableFab = findViewById(R.id.order_clear_fab);
        clearTableFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(OrderActivity.this)
                        .setMessage(R.string.question_clear_table)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Database.getInstance().getOrderRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                    }
                                });
                                Intent intent = new Intent(getApplication(), TablesActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        getOrder();
        Logger.debug("Order for table " + _table_doc_id + " from " + _section_doc_id);

    }

    private void getOrder() {
        Database.getInstance().getOrderRef().addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && error == null) {
                            _menuItemList = new ArrayList<>();
                            for (DocumentSnapshot doc : value) {
                                MenuItem menuItem = doc.toObject(MenuItem.class);
                                if (menuItem != null) {
                                    menuItem.setDocument_id(doc.getId());
                                    _menuItemList.add(menuItem);
                                }
                            }
                            OrderUtil.getInstance().setOrderedList(_menuItemList);
                            initOrderRV();
                        }
                    }
                });
    }

    private void initOrderRV() {
        if (_rvOrderAdapter != null) {
            _rvOrderAdapter.updateList(_menuItemList);
        } else {
            RecyclerView recyclerView = findViewById(R.id.order_items_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            _rvOrderAdapter = new RVOrderAdapter(this, _menuItemList);
            _rvOrderAdapter.setClickListener(this);
            recyclerView.setAdapter(_rvOrderAdapter);
        }
    }


    @Override
    public void onEditClick(View v, final int position) {
        final Dialog dialog = new Dialog(this, R.style.MyThemeDialogCustom);
        dialog.setContentView(R.layout.popup_edit_order);

        TextView itemName = dialog.findViewById(R.id.orderPopup_name_tv);
        final TextView itemQuantity = dialog.findViewById(R.id.orderPopup_quantity_tv);
        Button minusBtn = dialog.findViewById(R.id.orderPopup_minus_btn);
        Button plusBtn = dialog.findViewById(R.id.orderPopup_plus_btn);
        Button saveBtn = dialog.findViewById(R.id.orderPopup_save_btn);
        Button cancelBtn = dialog.findViewById(R.id.orderPopup_cancel_btn);

        itemName.setText(_menuItemList.get(position).getName());
        itemQuantity.setText(String.valueOf(_menuItemList.get(position).getQuantity()));

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(itemQuantity.getText().toString()) > 0) {
                    itemQuantity.setText(String.valueOf(Integer.parseInt(itemQuantity.getText().toString()) - 1));
                }
            }
        });
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemQuantity.setText(String.valueOf(Integer.parseInt(itemQuantity.getText().toString()) + 1));
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _menuItemList.get(position).setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                if (_menuItemList.get(position).getQuantity() == 0) {
                    Database.getInstance().getOrderRef()
                            .document(_menuItemList.get(position).getDocument_id())
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                        }
                    });
                } else {
                    Database.getInstance().getOrderRef()
                            .document(_menuItemList.get(position).getDocument_id())
                            .set(_menuItemList.get(position)).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.cancel();
                                }
                            });
                }
            }
        });
        dialog.show();
    }
}
