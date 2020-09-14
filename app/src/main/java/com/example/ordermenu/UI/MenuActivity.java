package com.example.ordermenu.UI;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ordermenu.R;

import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_DOC_ID;

public class MenuActivity extends AppCompatActivity {


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

    }

}
