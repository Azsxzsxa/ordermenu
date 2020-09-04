package com.example.ordermenu.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ordermenu.Models.RVTableAdapter;
import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class TableChoice extends AppCompatActivity implements RVTableAdapter.ItemClickListener {
    private static final String TAG = "TableChoice";
    private final static String RESTAURANTS = "Restaurants";
    private final static String CURRENT = "Current";


    private RVTableAdapter _rvTableAdapter;
    private Restaurant _restaurant;
    private String _restaurantId;
    private ArrayList<Section> _sections = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            _restaurant = doc.toObject(Restaurant.class);
                            _restaurantId = doc.getId();
                            getSections();
                        }
                        if (_restaurant == null) {
                            setCreateRestaurant();
                        }
                    }

                });
    }

    private void setCreateRestaurant(){
        //TODO Add restaurant name, create employees, create sections, create menu
    }

    private void getSections(){
        Database.getInstance().restRef.document(_restaurantId).collection(CURRENT).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Section section = doc.toObject(Section.class);
                    _sections.add(section);
                }
                //TODO init the sections
            }
        });
    }

    public void initTablesRV(int tableCount) {
        ArrayList<String> tableNumbers = new ArrayList<>();
        for (int i = 1; i < tableCount; i++) {
            tableNumbers.add(String.valueOf(i));
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.RV_tables);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        _rvTableAdapter = new RVTableAdapter(this, tableNumbers);
        _rvTableAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvTableAdapter);
    }

    @Override
    public void onTableClick(View view, int position) {
        Logger.debug("Item clicked " + position);
        Intent mIntent = new Intent(this, MenuOrder.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("table position", String.valueOf(position));
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
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
                    Log.d(TAG, "onAuthStateChanged: signed_out");
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
