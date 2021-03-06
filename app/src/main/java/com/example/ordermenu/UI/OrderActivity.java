package com.example.ordermenu.UI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Adapters.RVOrderAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Models.Order;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.ordermenu.Utils.StrUtil.DB_MENUITEM_STATUS_CANCELED;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLE_STATUS_FREE;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLE_STATUS_SERVED;


public class OrderActivity extends AppCompatActivity implements RVOrderAdapter.ItemClickListener {

    private static final String TAG = "OrderActivity";
    List<MenuItem> _inProgressList = new ArrayList<>();
    List<MenuItem> _servedList = new ArrayList<>();
    private RVOrderAdapter _rvInProgressAdapter;
    private RVOrderAdapter _rvServedAdapter;
    private TextView _totalPriceTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        _totalPriceTv = findViewById(R.id.order_total_price_textView);
        FloatingActionButton fabAdd = findViewById(R.id.order_add_fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavAdd();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.order_bottomnavigation);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order_bottomnav_clear:
                        bottomNavClear();
                        return false;
                    case R.id.order_bottomnav_served:
                        bottomNavServed();
                        return false;
                    case R.id.order_bottomnav_cancel:
                        bottomNavCancel();
                        return false;
                    case R.id.order_bottomnav_move:
                        bottomNavMove();
                        return false;
                    default:
                        return false;

                }
            }
        });
        getOrder();
    }


    private void getOrder() {
        Database.getInstance().getInProgressRef().addSnapshotListener(OrderActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && error == null) {
                    _inProgressList = new ArrayList<>();
                    for (DocumentSnapshot doc : value) {
                        MenuItem menuItem = doc.toObject(MenuItem.class);
                        if (menuItem != null) {
                            if (!menuItem.getStatus().equals(DB_MENUITEM_STATUS_CANCELED)) {
                                _inProgressList.add(menuItem);
                            }
                        }
                    }
                    OrderUtil.getInstance().setInProgressOrderedList(_inProgressList);
                    initInProgressRV();
                    if (!_inProgressList.isEmpty()) {
                        int price = 0;
                        for (MenuItem menuItem : _inProgressList)
                            price += menuItem.getPrice() * menuItem.getQuantity();

                        _totalPriceTv.setText(String.format(Locale.getDefault(), "Order total price: %d", price));
                    }
                }
            }
        });
        Database.getInstance().getServedRef().addSnapshotListener(OrderActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && error == null) {
                    _servedList = new ArrayList<>();
                    for (DocumentSnapshot doc : value) {
                        MenuItem menuItem = doc.toObject(MenuItem.class);
                        if (menuItem != null) {
                            _servedList.add(menuItem);
                        }
                    }
                    OrderUtil.getInstance().setServedOrderedList(_servedList);
                    initServedRV();
                    if (!_servedList.isEmpty()) {
                        int price = 0;
                        for (MenuItem menuItem : _servedList)
                            price += menuItem.getPrice() * menuItem.getQuantity();

                        _totalPriceTv.setText(String.format(Locale.getDefault(), "Order total price: %d", price));
                    }
                }
            }
        });
    }

    private void initServedRV() {
        OrderUtil.getInstance().setServedOrderedList(_servedList);
        if (_servedList.isEmpty()) {
            TextView servedTV = findViewById(R.id.order_served_tv);
            servedTV.setVisibility(View.GONE);
        }
        RecyclerView servedRV = findViewById(R.id.order_served_items_rv);
        servedRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _rvServedAdapter = new RVOrderAdapter(this, _servedList);
        servedRV.setAdapter(_rvServedAdapter);

    }

    public void initInProgressRV() {
        OrderUtil.getInstance().setInProgressOrderedList(_inProgressList);
        if (_inProgressList.isEmpty()) {
            TextView inProgressTV = findViewById(R.id.order_inProgress_tv);
            inProgressTV.setVisibility(View.GONE);
        }
        RecyclerView inProgressRV = findViewById(R.id.order_inProgress_items_rv);
        inProgressRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _rvInProgressAdapter = new RVOrderAdapter(this, _inProgressList);
        _rvInProgressAdapter.setClickListener(this);
        inProgressRV.setAdapter(_rvInProgressAdapter);
    }


    @Override
    public void onEditClick(View v, final int position) {
//        final Dialog dialog = new Dialog(this, R.style.MyThemeDialogCustom);
//        dialog.setContentView(R.layout.dialog_edit_order);
//
//        TextView itemName = dialog.findViewById(R.id.orderPopup_name_tv);
//        final TextView itemQuantity = dialog.findViewById(R.id.orderPopup_quantity_tv);
//        Button minusBtn = dialog.findViewById(R.id.orderPopup_minus_btn);
//        Button plusBtn = dialog.findViewById(R.id.orderPopup_plus_btn);
//        Button saveBtn = dialog.findViewById(R.id.orderPopup_save_btn);
//        Button cancelBtn = dialog.findViewById(R.id.orderPopup_cancel_btn);
//
//        itemName.setText(_menuItemList.get(position).getName());
//        itemQuantity.setText(String.valueOf(_menuItemList.get(position).getQuantity()));
//
//        minusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int lowLimit = 0;
//                if (_menuItemList.get(position).getStatus().equals(DB_MENUITEM_STATUS_SERVED)) {
//                    lowLimit = _menuItemList.get(position).getQuantity();
//                }
//                if (Integer.parseInt(itemQuantity.getText().toString()) > lowLimit) {
//                    itemQuantity.setText(String.valueOf(Integer.parseInt(itemQuantity.getText().toString()) - 1));
//                }
//            }
//        });
//        plusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                itemQuantity.setText(String.valueOf(Integer.parseInt(itemQuantity.getText().toString()) + 1));
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//            }
//        });
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int kitchenQuantity;
//                switch (_menuItemList.get(position).getStatus()) {
//                    case DB_MENUITEM_STATUS_ORDERED:
//                        _menuItemList.get(position).setStatus(DB_MENUITEM_STATUS_MODIFIED);
//                        _menuItemList.get(position).setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
//                        _menuItemList.get(position).setKitchenQuantity(Integer.parseInt(itemQuantity.getText().toString()));
//                        Log.d(TAG, "onClick: orderd");
//                        break;
//                    case DB_MENUITEM_STATUS_MODIFIED:
//                        kitchenQuantity = Math.abs(_menuItemList.get(position).getQuantity() - Integer.parseInt(itemQuantity.getText().toString()));
//                        _menuItemList.get(position).setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
//                        _menuItemList.get(position).setKitchenQuantity(kitchenQuantity);
//                        Log.d(TAG, "onClick: modif");
//                        break;
//                    case DB_MENUITEM_STATUS_SERVED:
//                        _menuItemList.get(position).setStatus(DB_MENUITEM_STATUS_ORDERED);
//                        kitchenQuantity = Math.abs(_menuItemList.get(position).getQuantity() - Integer.parseInt(itemQuantity.getText().toString()));
//                        Log.d(TAG, "onClick: " + kitchenQuantity);
//                        _menuItemList.get(position).setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
//                        _menuItemList.get(position).setKitchenQuantity(kitchenQuantity);
//                        break;
//                    case DB_MENUITEM_STATUS_CANCELED:
//                        _menuItemList.get(position).setStatus(DB_MENUITEM_STATUS_ORDERED);
//                        _menuItemList.get(position).setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
//                        _menuItemList.get(position).setKitchenQuantity(Integer.parseInt(itemQuantity.getText().toString()));
//                        Log.d(TAG, "onClick: cancel");
//                        break;
//
//                }
//                if (_menuItemList.get(position).getQuantity() == 0) {
//                    _menuItemList.get(position).setStatus(DB_MENUITEM_STATUS_CANCELED);
//                }
//                Database.getInstance().getTableRef().update("occupied", DB_TABLE_STATUS_BUSY);
//                Database.getInstance().getOrderRef()
//                        .document(_menuItemList.get(position).getDocument_id())
//                        .set(_menuItemList.get(position)).
//                        addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                dialog.cancel();
//                            }
//                        });
//            }
//        });
//        dialog.show();
    }

    private void bottomNavMove() {
        Intent intent = new Intent(getApplication(), OrderMoveActivity.class);
        startActivity(intent);
    }

    private void bottomNavCancel() {
        final Dialog dialog = new Dialog(OrderActivity.this, R.style.MyThemeDialogCustom);
        dialog.setContentView(R.layout.dialog_yes_no);

        TextView textView = dialog.findViewById(R.id.dialog_yesNo_text);
        MaterialButton yesBtn = dialog.findViewById(R.id.dialog_yesNo_yes_btn);
        MaterialButton noBtn = dialog.findViewById(R.id.dialog_yesNo_No_btn);

        textView.setText(R.string.question_clear_table);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteBatch batch = Database.getInstance().getDb().batch();
                for (MenuItem menuItem : OrderUtil.getInstance().getInProgressOrderedList()) {
                    menuItem.setStatus(DB_MENUITEM_STATUS_CANCELED);
                    batch.set(Database.getInstance().getInProgressRef().document(menuItem.getDocument_id()), menuItem);
                }
                if (!_servedList.isEmpty()) {
                    batch.update(Database.getInstance().getTableRef(), "occupied", DB_TABLE_STATUS_SERVED);
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.cancel();
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

    private void bottomNavAdd() {
        Intent intent = new Intent(getApplication(), MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void bottomNavServed() {
        final Dialog dialog = new Dialog(OrderActivity.this, R.style.MyThemeDialogCustom);
        dialog.setContentView(R.layout.dialog_yes_no);

        TextView textView = dialog.findViewById(R.id.dialog_yesNo_text);
        MaterialButton yesBtn = dialog.findViewById(R.id.dialog_yesNo_yes_btn);
        MaterialButton noBtn = dialog.findViewById(R.id.dialog_yesNo_No_btn);

        textView.setText(R.string.question_order_ready);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_inProgressList.isEmpty()) {
                    WriteBatch batch = Database.getInstance().getDb().batch();
                    for (MenuItem menuItem : _inProgressList) {
                        if (OrderUtil.getInstance().getServedOrderedList().contains(menuItem)) {
                            int servedQuantity = _servedList.get(_servedList.indexOf(menuItem)).getQuantity();
                            menuItem.setQuantity(menuItem.getQuantity() + servedQuantity);
                        }
                        batch.set(Database.getInstance().getServedRef().document(menuItem.getDocument_id()), menuItem);
                        batch.delete(Database.getInstance().getInProgressRef().document(menuItem.getDocument_id()));
                    }
                    batch.update(Database.getInstance().getTableRef(), "occupied", DB_TABLE_STATUS_SERVED);
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            finish();
                        }
                    });

                } else {
                    dialog.cancel();
                }

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

    private void bottomNavClear() {
        final Dialog dialog = new Dialog(OrderActivity.this, R.style.MyThemeDialogCustom);
        dialog.setContentView(R.layout.dialog_yes_no);

        TextView textView = dialog.findViewById(R.id.dialog_yesNo_text);
        MaterialButton yesBtn = dialog.findViewById(R.id.dialog_yesNo_yes_btn);
        MaterialButton noBtn = dialog.findViewById(R.id.dialog_yesNo_No_btn);

        textView.setText(R.string.question_clear_table);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.getInstance().getInProgressRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<DocumentReference> docsToDelete = new ArrayList<>();
                        boolean isProgressListValid = true;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            boolean isAllCanceled = true;
                            List<DocumentReference> unkownStatusProgressItems = new ArrayList<>();
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                unkownStatusProgressItems.add(doc.getReference());
                                MenuItem menuItem = doc.toObject(MenuItem.class);
                                if (menuItem != null && !menuItem.getStatus().equals(DB_MENUITEM_STATUS_CANCELED)) {
                                    isAllCanceled = false;
                                }
                            }
                            if (!isAllCanceled) {
                                isProgressListValid = false;
                                dialog.cancel();
                                final Dialog innerDialog = new Dialog(OrderActivity.this, R.style.MyThemeDialogCustom);
                                innerDialog.setContentView(R.layout.dialog_info_warning);

                                TextView textView = innerDialog.findViewById(R.id.dialog_infoWarning_text);
                                MaterialButton okBtn = innerDialog.findViewById(R.id.dialog_infoWarning_ok_btn);
                                okBtn.setText(R.string.ok);
                                textView.setText(R.string.order_clear_not_served);

                                okBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        innerDialog.cancel();
                                    }
                                });
                                innerDialog.setCancelable(true);
                                innerDialog.show();
                            } else {
                                docsToDelete.addAll(unkownStatusProgressItems);
                            }

                        }
                        if (isProgressListValid) {
                            Database.getInstance().getServedRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        WriteBatch resetOrderBatch = Database.getInstance().getDb().batch();
                                        resetOrderBatch.update(Database.getInstance().getTableRef(), "occupied", DB_TABLE_STATUS_FREE);
                                        List<MenuItem> menuItems = new ArrayList<>();
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            docsToDelete.add(documentSnapshot.getReference());
                                            menuItems.add(documentSnapshot.toObject(MenuItem.class));
                                        }
                                        for (DocumentReference documentReference : docsToDelete) {
                                            resetOrderBatch.delete(documentReference);
                                        }
                                        DocumentReference ref = Database.getInstance().restRef
                                                .document(Database.getInstance().getRestaurantId())
                                                .collection("History").document();
                                        Order order = new Order(FirebaseAuth.getInstance().getUid(),
                                                OrderUtil.getInstance().getTableNumber(),
                                                OrderUtil.getInstance().getSectionName(),
                                                OrderUtil.getInstance().getStartOrderDate(),
                                                new Date(),
                                                ref.getId(),
                                                menuItems,
                                                OrderUtil.getInstance().getTableDocID(),
                                                OrderUtil.getInstance().getSectionDocID()
                                        );
                                        resetOrderBatch.set(Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                                                .collection("History").document(ref.getId()), order);
                                        resetOrderBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.cancel();
                                                Intent intent = new Intent(getApplication(), TablesActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            });
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


    private void clearServedCollection(final Dialog dialog) {
        Database.getInstance().getServedRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    WriteBatch resetOrderBatch = Database.getInstance().getDb().batch();
                    resetOrderBatch.update(Database.getInstance().getTableRef(), "occupied", DB_TABLE_STATUS_FREE);
                    List<MenuItem> menuItems = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        menuItems.add(documentSnapshot.toObject(MenuItem.class));
                        resetOrderBatch.delete(documentSnapshot.getReference());
                    }
                    DocumentReference ref = Database.getInstance().restRef
                            .document(Database.getInstance().getRestaurantId())
                            .collection("History").document();
                    Order order = new Order(FirebaseAuth.getInstance().getUid(),
                            OrderUtil.getInstance().getTableNumber(),
                            OrderUtil.getInstance().getSectionName(),
                            OrderUtil.getInstance().getStartOrderDate(),
                            new Date(),
                            ref.getId(),
                            menuItems,
                            OrderUtil.getInstance().getTableDocID(),
                            OrderUtil.getInstance().getSectionDocID()
                    );
                    resetOrderBatch.set(Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                            .collection("History").document(ref.getId()), order);
                    resetOrderBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Intent intent = new Intent(getApplication(), TablesActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!OrderUtil.getInstance().getCurrentOrderList().isEmpty()) {

            //Display dialog
            final Dialog dialog = new Dialog(this, R.style.MyThemeDialogCustom);
            dialog.setContentView(R.layout.dialog_yes_no);

            TextView textView = dialog.findViewById(R.id.dialog_yesNo_text);
            MaterialButton yesBtn = dialog.findViewById(R.id.dialog_yesNo_yes_btn);
            MaterialButton noBtn = dialog.findViewById(R.id.dialog_yesNo_No_btn);

            textView.setText(R.string.exit_discrd_order);

            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    finish();
                }
            });

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();

        } else {
            super.onBackPressed();
        }
    }
}
