package com.example.ordermenu.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.ordermenu.Utils.StrUtil.DB_ORDER;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;

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

    public FirebaseFirestore getDb() {
        return db;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public CollectionReference getOrderRef(){
        return restRef.document(restaurantId).collection(StrUtil.DB_CURRENT).document(OrderUtil.getInstance().getSectionDocID())
                .collection(DB_TABLES).document(OrderUtil.getInstance().getTableDocID()).collection(DB_ORDER);
    }

    public DocumentReference getTableRef(){
        return restRef.document(restaurantId).collection(StrUtil.DB_CURRENT).document(OrderUtil.getInstance().getSectionDocID())
                .collection(DB_TABLES).document(OrderUtil.getInstance().getTableDocID());
    }
}
