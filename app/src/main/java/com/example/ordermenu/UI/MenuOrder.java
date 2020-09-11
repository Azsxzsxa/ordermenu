package com.example.ordermenu.UI;

import android.os.Bundle;

import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.Models.TabMenuAdapter;
import com.example.ordermenu.Models.TabSectionAdapter;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.View;

import com.example.ordermenu.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.ordermenu.Utils.StrUtil.TABLE_POSITION;
import static com.example.ordermenu.Utils.StrUtil.TABLE_SECTION;

public class MenuOrder extends AppCompatActivity {

    private TabMenuAdapter adapter;
    private TabLayout tab;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_order);
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

        String tablePosition = Objects.requireNonNull(getIntent().getExtras()).getString(TABLE_POSITION);
        String tableSection = Objects.requireNonNull(getIntent().getExtras()).getString(TABLE_SECTION);
        Logger.debug("Order for table "+ tablePosition + " from " +tableSection);

        getMenuSections();


    }

    private void getMenuSections() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                assert restaurant != null;
                ArrayList<String> menuCathegories = restaurant.getMenuCathegories();
                viewPager =  findViewById(R.id.viewPager);
                tab = findViewById(R.id.tabLayout);

                adapter = new TabMenuAdapter
                        (getSupportFragmentManager(), menuCathegories);
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(1);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
            }
        });
    }

}
