package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Adapters.RVMenuAdapter;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DynamicMenu extends Fragment implements RVMenuAdapter.ItemClickListener {

    private View view;
    private String _menuCategory;
    private ArrayList<MenuItem> _menuItems = new ArrayList<>();
    ArrayList<MenuItem> _categoryItems = new ArrayList<>();
    private RVMenuAdapter _rvMenuAdapter;

    public DynamicMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static DynamicMenu addFragment(String category) {
        DynamicMenu fragment = new DynamicMenu();
        Bundle args = new Bundle();
        args.putString(StrUtil.MENU_CATHEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dynamic_menu, container, false);
        assert getArguments() != null;
        _menuCategory = getArguments().getString(StrUtil.MENU_CATHEGORY, "");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView testMenu = view.findViewById(R.id.test_menu);
        testMenu.setText(_menuCategory);

        getMenuItems();
    }

    private void getMenuItems() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(StrUtil.MENU)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    _menuItems.add(doc.toObject(MenuItem.class));
                }

                for (MenuItem menuItem : _menuItems) {
                    if (menuItem.getCategory().contains(_menuCategory)) {
                        _categoryItems.add(menuItem);
                    }
                }

                // set up the RecyclerView
                RecyclerView recyclerView = view.findViewById(R.id.RV_tables);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                _rvMenuAdapter = new RVMenuAdapter(getContext(), _categoryItems);
                _rvMenuAdapter.setClickListener(DynamicMenu.this);
                recyclerView.setAdapter(_rvMenuAdapter);
            }
        });
    }

    @Override
    public void onPlusClick(View view, int position) {
        _categoryItems.get(position).setQuantity(_categoryItems.get(position).getQuantity() + 1);
        _rvMenuAdapter.notifyItemChanged(position);
    }

    @Override
    public void onMinusClick(View view, int position) {
        _categoryItems.get(position).setQuantity(_categoryItems.get(position).getQuantity() - 1);
        _rvMenuAdapter.notifyItemChanged(position);
    }
}