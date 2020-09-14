package com.example.ordermenu.UI;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
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

import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_POSITION;
import static com.example.ordermenu.Utils.StrUtil.TABLE_SECTION;

public class MenuActivity extends AppCompatActivity implements RVMenuAdapter.ItemClickListener {
    private ArrayList<String> _menuCategories = new ArrayList<>();
    RVMenuAdapter _rvMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (getIntent() != null && getIntent().getExtras() != null) {
            String _table_doc_id = getIntent().getExtras().getString(TABLE_DOC_ID, "");
            String _section_doc_id = getIntent().getExtras().getString(SECTION_DOC_ID, "");

            NavController navController;
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();
                Bundle bundle = new Bundle();
                bundle.putString(TABLE_DOC_ID, _table_doc_id);
                bundle.putString(SECTION_DOC_ID, _section_doc_id);
                navController.setGraph(R.navigation.nav_graph, bundle);
            }
        }else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }


//        getMenuCategories();
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
