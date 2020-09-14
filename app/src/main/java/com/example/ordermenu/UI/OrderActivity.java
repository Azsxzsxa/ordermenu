package com.example.ordermenu.UI;

import android.content.Intent;
import android.os.Bundle;

import com.example.ordermenu.Adapters.RVOrderAdapter;
import com.example.ordermenu.Adapters.RVTableAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.ordermenu.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_POSITION;
import static com.example.ordermenu.Utils.StrUtil.TABLE_SECTION;

public class OrderActivity extends AppCompatActivity implements RVOrderAdapter.ItemClickListener {

    ArrayList<MenuItem> _menuItems = new ArrayList<>();
    RVOrderAdapter _rvOrderAdapter;

    String _section_doc_id;
    String _table_doc_id;

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
                Intent mIntent = new Intent(getApplication(), MenuActivity.class);
                startActivity(mIntent);
            }
        });

        if (getIntent().getExtras() != null) {
            _table_doc_id = getIntent().getExtras().getString(TABLE_DOC_ID, "");
            _section_doc_id = getIntent().getExtras().getString(SECTION_DOC_ID, "");
            getOrder();
        }

        Logger.debug("Order for table " + _table_doc_id + " from " + _section_doc_id);

    }

    private void getOrder() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.CURRENT).document(_section_doc_id)
                .collection("Tables").document(_table_doc_id).collection("Order")
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
            _rvOrderAdapter = new RVOrderAdapter(this, _menuItems);
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
