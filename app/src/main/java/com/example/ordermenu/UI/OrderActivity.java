package com.example.ordermenu.UI;

import android.content.Intent;
import android.os.Bundle;

import com.example.ordermenu.Adapters.RVMenuItemAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.OrderUtil;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.ordermenu.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.ordermenu.Utils.StrUtil.DB_ORDER;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;
import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_DOC_ID;

public class OrderActivity extends AppCompatActivity implements RVMenuItemAdapter.ItemClickListener {

    ArrayList<MenuItem> _menuItems = new ArrayList<>();
    RVMenuItemAdapter _rvOrderAdapter;

    String _section_doc_id = OrderUtil.getInstance().getSectionDocID();
    String _table_doc_id = OrderUtil.getInstance().getTableDocID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
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
                            _menuItems = new ArrayList<>();
                            for (DocumentSnapshot doc : value) {
                                _menuItems.add(doc.toObject(MenuItem.class));
                            }
                            initOrderRV();
                        }
                    }
                });
    }

    private void initOrderRV() {
        if (_rvOrderAdapter != null) {
            _rvOrderAdapter.updateList(_menuItems);
        } else {
            RecyclerView recyclerView = findViewById(R.id.RV_order);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            _rvOrderAdapter = new RVMenuItemAdapter(this, _menuItems);
            _rvOrderAdapter.setClickListener(this);
            recyclerView.setAdapter(_rvOrderAdapter);
        }
    }


    @Override
    public void onPlusClick(View view, int position) {
        _menuItems.get(position).setQuantity(_menuItems.get(position).getQuantity() + 1);
        _rvOrderAdapter.notifyItemChanged(position);

    }

    @Override
    public void onMinusClick(View view, int position) {
        if (_menuItems.get(position).getQuantity() > 0) {
            _menuItems.get(position).setQuantity(_menuItems.get(position).getQuantity() - 1);
            _rvOrderAdapter.notifyItemChanged(position);
        }
    }
}
