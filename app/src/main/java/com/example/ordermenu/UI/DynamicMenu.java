package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DynamicMenu extends Fragment {

    private View view;
    private String _menuCathegory;
    private ArrayList<MenuItem> _menuItems = new ArrayList<>();

    public DynamicMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static DynamicMenu addfrag(String cathegory) {
        DynamicMenu fragment = new DynamicMenu();
        Bundle args = new Bundle();
        args.putString(StrUtil.MENU_CATHEGORY,cathegory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_dynamic_menu, container, false);
        assert getArguments() != null;
        _menuCathegory = getArguments().getString(StrUtil.MENU_CATHEGORY, "");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView testMenu = view.findViewById(R.id.test_menu);
        testMenu.setText(_menuCathegory);

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
            }
        });
    }
}