package com.example.ordermenu.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.ordermenu.Adapters.RVOrderAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Models.Order;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements RVOrderAdapter.ItemClickListener {

    ArrayList<MenuItem> _menuItemList = new ArrayList<>();
    RVOrderAdapter _rvOrderAdapter;

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
                finish();
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
                                        List<MenuItem> menuItems = new ArrayList<>();
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            menuItems.add(documentSnapshot.toObject(MenuItem.class));
                                            documentSnapshot.getReference().delete();
                                        }
                                        DocumentReference ref = Database.getInstance().restRef
                                                .document(Database.getInstance().getRestaurantId())
                                                .collection("History").document();
                                        Order order = new Order(ref.getId(), FirebaseAuth.getInstance().getUid(),
                                                OrderUtil.getInstance().getTableNumber(),
                                                OrderUtil.getInstance().getSectionName(),
                                                OrderUtil.getInstance().getStartOrderDate(),
                                                new Date(),
                                                menuItems
                                        );
                                        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                                                .collection("History").document(ref.getId()).set(order);
                                    }
                                });

                                //Update table to be free
                                Database.getInstance().getTableRef().update("occupied", false);

                                Intent intent = new Intent(getApplication(), TablesActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        getOrder();
    }

    private void getOrder() {
        Database.getInstance().getOrderRef().addSnapshotListener(OrderActivity.this, new EventListener<QuerySnapshot>() {
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
                    OrderUtil.getInstance().setAlreadyOrderedList(_menuItemList);
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
