package com.example.ordermenu.UI;

import android.app.Dialog;
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

import static com.example.ordermenu.Utils.StrUtil.DB_CURRENT;
import static com.example.ordermenu.Utils.StrUtil.DB_ORDER;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;

public class OrderActivity extends AppCompatActivity implements RVOrderAdapter.ItemClickListener {

    ArrayList<MenuItem> _menuItemList = new ArrayList<>();
    RVOrderAdapter _rvOrderAdapter;

    String _section_doc_id = OrderUtil.getInstance().getSectionDocID();
    String _table_doc_id = OrderUtil.getInstance().getTableDocID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        FloatingActionButton fab = findViewById(R.id.order_toMenu_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to menu
                Intent intent = new Intent(getApplication(), MenuActivity.class);
//                intent.putExtra(TABLE_DOC_ID, _table_doc_id);
//                intent.putExtra(SECTION_DOC_ID, _section_doc_id);
                startActivity(intent);
            }
        });

//        if (getIntent().getExtras() != null) {
//            _table_doc_id = getIntent().getExtras().getString(TABLE_DOC_ID, "");
//            _section_doc_id = getIntent().getExtras().getString(SECTION_DOC_ID, "");
//            getOrder();
//        }
        getOrder();
        Logger.debug("Order for table " + _table_doc_id + " from " + _section_doc_id);

    }

    private void getOrder() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.DB_CURRENT).document(_section_doc_id)
                .collection(DB_TABLES).document(_table_doc_id).collection(DB_ORDER)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
//        View popupView = getLayoutInflater().inflate(R.layout.popup_edit_order, (ViewGroup) v, false);
        final Dialog dialog = new Dialog(this,R.style.MyThemeDialogCustom);
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
                    Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                            .collection(DB_CURRENT)
                            .document(OrderUtil.getInstance().getSectionDocID())
                            .collection(DB_TABLES)
                            .document(OrderUtil.getInstance().getTableDocID())
                            .collection(DB_ORDER)
                            .document(_menuItemList.get(position).getDocument_id())
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                        }
                    });
                } else {
                    Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                            .collection(DB_CURRENT)
                            .document(OrderUtil.getInstance().getSectionDocID())
                            .collection(DB_TABLES)
                            .document(OrderUtil.getInstance().getTableDocID())
                            .collection(DB_ORDER)
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
