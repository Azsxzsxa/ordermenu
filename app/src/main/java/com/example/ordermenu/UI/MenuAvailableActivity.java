package com.example.ordermenu.UI;

import android.os.Bundle;

import com.example.ordermenu.Adapters.RVMenuAvailableAdapter;
import com.example.ordermenu.Adapters.RVOrderAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Utils.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import com.example.ordermenu.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_MENU;

public class MenuAvailableActivity extends AppCompatActivity implements RVMenuAvailableAdapter.ItemClickListener {

    private RVMenuAvailableAdapter _menuAvailableAdapter;
    private List<MenuItem> _menuItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_available);
        getMenu();


    }

    private void getMenu() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(DB_MENU)
                .orderBy("name", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                _menuItemList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    _menuItemList.add(documentSnapshot.toObject(MenuItem.class));
                }
                initRV();
            }
        });
    }


    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.menuavailable_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(MenuAvailableActivity.this));
        _menuAvailableAdapter = new RVMenuAvailableAdapter(this, _menuItemList);
        _menuAvailableAdapter.setClickListener(this);
        recyclerView.setAdapter(_menuAvailableAdapter);
    }

    @Override
    public void onSwitchClicked(View view, int position, Boolean status) {
        String itemDocId = _menuItemList.get(position).getDocument_id();
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                .collection(DB_MENU).document(itemDocId).update("available", status);
    }
}