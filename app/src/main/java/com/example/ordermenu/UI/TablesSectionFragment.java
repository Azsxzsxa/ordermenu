package com.example.ordermenu.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.ordermenu.Adapters.RVTableAdapter;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.Models.Table;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.Logger;
import com.example.ordermenu.Utils.OrderUtil;
import com.example.ordermenu.Utils.StrUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;
import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.SECTION_NAME;


public class TablesSectionFragment extends Fragment implements RVTableAdapter.ItemClickListener {
    private static final String TAG = "DynamicSectionFragment";

    private View view;
    private String _sectionName, _section_doc_id;
    private List<Table> _tableList = new ArrayList<>();
    private int _tableCount;
    private RVTableAdapter _rvTableAdapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tables_section, container, false);

        progressBar = view.findViewById(R.id.tablesFrag_pb);

        if (getArguments() != null) {
            _sectionName = getArguments().getString(StrUtil.SECTION_NAME, "");
            _section_doc_id = getArguments().getString(SECTION_DOC_ID, "");
            _tableCount = getArguments().getInt(StrUtil.SECTION_TABLE_COUNT, 0);

            if (_section_doc_id != null)
                getTables(_section_doc_id);
        }
        initTablesRV();
        return view;
    }

    private void getTables(String section_doc_id) {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.DB_CURRENT).document(section_doc_id)
                .collection(DB_TABLES).orderBy("number", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (queryDocumentSnapshots != null && error == null && queryDocumentSnapshots.size() > 0) {

                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);

                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot != null) {
                                    Table table = documentSnapshot.toObject(Table.class);
                                    if (table != null) {
                                        table.setDocumentID(documentSnapshot.getId());
                                    }
                                    if (_tableList.contains(table)) {
                                        _tableList.set(_tableList.indexOf(table), table);
                                        _rvTableAdapter.notifyItemChanged(_tableList.indexOf(table));
                                    } else {
                                        _tableList.add(table);
                                        _rvTableAdapter.notifyItemInserted(_tableList.size() - 1);
                                    }
                                }
                            }

                        }
                    }
                });
    }

    public static TablesSectionFragment addFragment(Section section) {
        TablesSectionFragment fragment = new TablesSectionFragment();
        Bundle args = new Bundle();
        args.putString(SECTION_DOC_ID, section.getDocumentID());
        args.putString(SECTION_NAME, section.getName());
        fragment.setArguments(args);
        return fragment;
    }


    public void initTablesRV() {
        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.tablesFrag_numbers_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        _rvTableAdapter = new RVTableAdapter(getContext(), _tableList);
        _rvTableAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvTableAdapter);
    }

    @Override
    public void onTableClick(View view, int position, Table table) {
        Intent mIntent = new Intent(getActivity(), OrderActivity.class);

        OrderUtil.getInstance().setTableSwitched(_section_doc_id,
                _tableList.get(position).getDocumentID()
                , _tableList.get(position).getNumber(), _sectionName,
                _tableList.get(position).getStartOrderDate(),
                _tableList.get(position).getEndOrderDate(),
                _tableList.get(position).getOccupied());

        startActivity(mIntent);
    }
}