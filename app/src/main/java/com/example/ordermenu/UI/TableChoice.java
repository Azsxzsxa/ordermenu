package com.example.ordermenu.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ordermenu.Models.RVTableAdapter;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Logger;

import java.util.ArrayList;

public class TableChoice extends AppCompatActivity implements RVTableAdapter.ItemClickListener{

    private RVTableAdapter rvTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        initTablesRV();
    }

    public void initTablesRV(){
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
        Logger.debug("Item clicked "+position);

        Intent mIntent = new Intent(this, MenuOrder.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("table position", String.valueOf(position));
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}
