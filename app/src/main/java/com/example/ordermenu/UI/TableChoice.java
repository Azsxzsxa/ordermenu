package com.example.ordermenu.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ordermenu.Models.RVTableAdapter;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Logger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TableChoice extends AppCompatActivity implements RVTableAdapter.ItemClickListener {
    private static final String TAG = "TableChoice";


    private RVTableAdapter rvTableAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFirebaseAuth();

        initViews();
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

    private void initViews() {
        initTablesRV();
    }

    public void initTablesRV() {
        ArrayList<String> tableNumbers = new ArrayList<>();
        tableNumbers.add("1");
        tableNumbers.add("2");
        tableNumbers.add("3");
        tableNumbers.add("4");
        tableNumbers.add("5");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.RV_tables);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        rvTableAdapter = new RVTableAdapter(this, tableNumbers);
        rvTableAdapter.setClickListener(this);
        recyclerView.setAdapter(rvTableAdapter);
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
