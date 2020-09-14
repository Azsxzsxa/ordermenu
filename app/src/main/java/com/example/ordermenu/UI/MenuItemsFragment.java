package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;

public class MenuItemsFragment extends Fragment {
    private static final String TAG = "MenuItemsFragment";
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_items, container, false);



        return view;
    }
}