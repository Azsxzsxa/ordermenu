package com.example.ordermenu.Utils;

import androidx.annotation.NonNull;

import com.example.ordermenu.Models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Database {
    private static Database INSTANCE;

    private final static String RESTAURANTS = "Restaurants";
    private final static String CURRENT = "Current";

    public final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final CollectionReference restRef = db.collection(RESTAURANTS);
    public final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Database() {
    }

    public static Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }


}
