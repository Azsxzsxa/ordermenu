package com.example.ordermenu.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ordermenu.Models.RVTableAdapter;
import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.Models.TabAdapter;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class TableChoice extends AppCompatActivity {
    private ArrayList<Section> _sections = new ArrayList<>();

    private TabAdapter adapter;
    private TabLayout tab;
    private ViewPager viewPager;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFirebaseAuth();

        getRestaurant();
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
                        Restaurant restaurant = null;
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            restaurant = doc.toObject(Restaurant.class);
                            Database.getInstance().setRestaurantId(doc.getId());
                            getSections();
                        }
                        if (restaurant == null) {
                            setCreateRestaurant();
                        }
                    }
                });
    }

    private void setCreateRestaurant(){
        //TODO Add restaurant name, create employees, create sections, create menu
    }

    private void getSections(){
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.CURRENT).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Section section = doc.toObject(Section.class);
                    _sections.add(section);
                }
                viewPager =  findViewById(R.id.viewPager);
                tab = findViewById(R.id.tabLayout);


                for (Section section : _sections) {
                    tab.addTab(tab.newTab().setText(section.getName()));
                }

                adapter = new TabAdapter
                        (getSupportFragmentManager(), _sections);
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(1);
//                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        Logger.debug(""+_sections.get(position).getName());
                        //TODO reset fragment?
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
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

                    //TODO Start query here

                    /*
                    //check if email is verified
                    if (user.isEmailVerified()) {
                        // DO STUFF
                        Log.d(TAG, "onAuthStateChanged: MAIL VERIFIED");
                    } else {
                        Toast.makeText(TableChoice.this, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                    */

                } else {
                    // User is signed out
                    Logger.debug("onAuthStateChanged: signed_out");
                    Toast.makeText(TableChoice.this, "Not logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TableChoice.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };
    }

}
