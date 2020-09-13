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

import static com.example.ordermenu.Utils.StrUtil.TABLE_POSITION;
import static com.example.ordermenu.Utils.StrUtil.TABLE_SECTION;

public class OrderActivity extends AppCompatActivity implements RVOrderAdapter.ItemClickListener{

    ArrayList<MenuItem> _menuItems = new ArrayList<>();
    RVOrderAdapter _rvOrderAdapter;

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

        int tablePosition = Objects.requireNonNull(getIntent().getExtras()).getInt(TABLE_POSITION);
        String tableSection = Objects.requireNonNull(getIntent().getExtras()).getString(TABLE_SECTION);
        Logger.debug("Order for table " + tablePosition + " from " + tableSection);
        getOrder(tablePosition, tableSection);




    }

    private void getOrder(final int tablePosition, String tableSection) {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.CURRENT).whereEqualTo("name",tableSection).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    doc.getReference().collection("Tables").whereEqualTo("number",tablePosition).get().
                            addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                doc.getReference().collection("Order").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        _menuItems = new ArrayList<>();
                                        for (DocumentSnapshot doc : value) {
                                            _menuItems.add(doc.toObject(MenuItem.class));
                                        }
                                        initOrderRV();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void initOrderRV() {
        if (_rvOrderAdapter != null) {
            _rvOrderAdapter.updateList(_menuItems);
        }else {
            RecyclerView recyclerView = findViewById(R.id.RV_order);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            _rvOrderAdapter = new RVOrderAdapter(this, _menuItems);
            _rvOrderAdapter.setClickListener(this);
            recyclerView.setAdapter(_rvOrderAdapter);
        }
    }


    @Override
    public void onPlusClick(View view, int position) {
        _menuItems.get(position).setQuantity(_menuItems.get(position).getQuantity()+1);
        _rvOrderAdapter.notifyItemChanged(position);

    }

    @Override
    public void onMinusClick(View view, int position) {
        if (_menuItems.get(position).getQuantity() > 0) {
            _menuItems.get(position).setQuantity(_menuItems.get(position).getQuantity()-1);
            _rvOrderAdapter.notifyItemChanged(position);
        }
    }
}
