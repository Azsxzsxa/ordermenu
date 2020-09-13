package com.example.ordermenu.UI;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Adapters.RVMenuAdapter;
import com.example.ordermenu.Adapters.RVOrderAdapter;
import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.ordermenu.Utils.StrUtil.TABLE_POSITION;
import static com.example.ordermenu.Utils.StrUtil.TABLE_SECTION;

public class MenuActivity extends AppCompatActivity implements RVMenuAdapter.ItemClickListener{

    private ArrayList<String> _menuCategories = new ArrayList<>();
    RVMenuAdapter _rvMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getMenuCategories();




    }

    private void getMenuCategories() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                assert restaurant != null;
                _menuCategories = restaurant.getMenuCategories();

                //init RV
                RecyclerView recyclerView = findViewById(R.id.RV_menuCategory);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                _rvMenuAdapter = new RVMenuAdapter(MenuActivity.this, _menuCategories);
                _rvMenuAdapter.setClickListener(MenuActivity.this);
                recyclerView.setAdapter(_rvMenuAdapter);

            }
        });
    }


    @Override
    public void onMenuClick(View view, int position) {
        Logger.debug("Menu clicked");
    }
}
