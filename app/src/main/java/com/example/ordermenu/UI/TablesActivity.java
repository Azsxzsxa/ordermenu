package com.example.ordermenu.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.Adapters.TabSectionAdapter;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_CURRENT;
import static com.example.ordermenu.Utils.StrUtil.DB_MENU;

public class TablesActivity extends AppCompatActivity {
    private ArrayList<Section> _sections = new ArrayList<>();

    private TabSectionAdapter adapter;
    private TabLayout tab;
    private ViewPager viewPager;
    private Restaurant _restaurant = null;
    private ProgressBar progressBar;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        progressBar = findViewById(R.id.tablesActivity_pb);

        setTitle(R.string.toolBarTables);

        setupFirebaseAuth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }

    private void getRestaurant() {
        Database.getInstance().restRef.whereArrayContains("employees", Database.getInstance().userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            _restaurant = doc.toObject(Restaurant.class);
                            Database.getInstance().setRestaurantId(doc.getId());
                            doc.getReference().collection(DB_MENU).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<MenuItem> allMenuItems = new ArrayList<>();
                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        MenuItem menuItem = doc.toObject(MenuItem.class);
                                        if (menuItem != null) {
                                            menuItem.setDocument_id(doc.getId());
                                            if (!allMenuItems.contains(menuItem))
                                                allMenuItems.add(menuItem);
                                        }
                                    }
                                    OrderUtil.getInstance().setAllMenuItemsList(allMenuItems);
                                }
                            });
                            getSections();
                        }
                        if (_restaurant == null) {
                            setCreateRestaurant();
                        }
                    }
                });

    }

    private void setCreateRestaurant() {
        //TODO Add restaurant name, create employees, create sections, create menu
    }

    private void getSections() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(DB_CURRENT).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (progressBar.getVisibility() == View.VISIBLE)
                            progressBar.setVisibility(View.GONE);

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Section section = doc.toObject(Section.class);
                            if (section != null) {
                                section.setDocumentID(doc.getId());
                                _sections.add(section);
                            }
                        }
                        viewPager = findViewById(R.id.tables_vp);
                        tab = findViewById(R.id.tables_tabs);
                        for (Section section : _sections) {
                            tab.addTab(tab.newTab().setText(section.getName()));
                        }

                        adapter = new TabSectionAdapter
                                (getSupportFragmentManager(), _sections);
                        viewPager.setAdapter(adapter);
                        viewPager.setOffscreenPageLimit(10);
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
                    }
                });
    }


    /*
     ----------------------------- Firebase setup ---------------------------------
    */
    private void setupFirebaseAuth() {
//        Log.d(TAG, "setupFirebaseAuth: started");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    if (_restaurant == null) {
                        getRestaurant();
                    }
                } else {
                    // User is signed out
                    Logger.debug("onAuthStateChanged: signed_out");
                    Toast.makeText(TablesActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TablesActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionMenu_logOut:
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.actionMenu_toMenu:
                if (OrderUtil.getInstance().getAllMenuItemsList() != null) {
                    Intent intentMenu = new Intent(TablesActivity.this, MenuAvailableActivity.class);
                    startActivity(intentMenu);
                } else {
                    Toast.makeText(this, "Getting info from database, please try again", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.actionMenu_toOrderHistory:
                if (Database.getInstance().getRestaurantId() != null) {
                    Intent intentOrders = new Intent(TablesActivity.this, OrderHistoryActivity.class);
                    startActivity(intentOrders);
                } else {
                    Toast.makeText(this, "Getting info from database, please try again", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
