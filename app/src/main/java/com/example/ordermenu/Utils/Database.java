package com.example.ordermenu.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    private static Database INSTANCE;
    private final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final CollectionReference restRef = db.collection(StrUtil.DB_RESTAURANTS);
    public final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private String restaurantId;

    private Database() {
    }

    public static Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
