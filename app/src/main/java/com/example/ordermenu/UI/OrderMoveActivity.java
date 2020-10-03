package com.example.ordermenu.UI;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Adapters.RVOrderHistoryAdapter;
import com.example.ordermenu.Adapters.RVOrderMoveAdapter;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.ordermenu.Utils.StrUtil.DB_CURRENT;

public class OrderMoveActivity extends AppCompatActivity {
    ArrayList<Section> sections = new ArrayList<>();
    private RVOrderMoveAdapter _orderMoveAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_move);

        getSections();
    }

    private void getSections() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(DB_CURRENT)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Section section = doc.toObject(Section.class);
                    sections.add(section);
                }
                if (!sections.isEmpty()) {
                    initRV();
                }
            }
        });
    }


    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.ordermove_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderMoveActivity.this));
        _orderMoveAdapter = new RVOrderMoveAdapter(this, sections);
//        _orderMoveAdapter.setClickListener(this);
        recyclerView.setAdapter(_orderMoveAdapter);

    }
}
