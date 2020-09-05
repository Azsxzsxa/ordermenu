package com.example.ordermenu.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ordermenu.Models.RVTableAdapter;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class DynamicSection extends Fragment implements RVTableAdapter.ItemClickListener{

    private View view;
    private String _sectionName;
    private int _tableCount;
    private RVTableAdapter _rvTableAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dynamic_section, container, false);
        _sectionName = getArguments().getString(StrUtil.SECTION_NAME, "");
        _tableCount = getArguments().getInt(StrUtil.SECTION_TABLE_COUNT, 0);
        Logger.error(_sectionName);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTablesRV(_tableCount);
    }

    public static DynamicSection addfrag(Section section) {
        DynamicSection fragment = new DynamicSection();
        Bundle args = new Bundle();
        args.putString(StrUtil.SECTION_NAME,section.getName());
        args.putInt(StrUtil.SECTION_TABLE_COUNT,section.getTableCount());
        fragment.setArguments(args);
        return fragment;
    }


    public void initTablesRV(int tableCount) {
        Logger.error(""+tableCount);
        ArrayList<String> tableNumbers = new ArrayList<>();
        for (int i = 1; i <= tableCount; i++) {
            tableNumbers.add(String.valueOf(i));
        }

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.RV_tables);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        _rvTableAdapter = new RVTableAdapter(getContext(), tableNumbers);
        _rvTableAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvTableAdapter);
    }

    @Override
    public void onTableClick(View view, int position) {
        Logger.debug("Item clicked " + position);
        Intent mIntent = new Intent(getActivity(), MenuOrder.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("table position", String.valueOf(position));
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}