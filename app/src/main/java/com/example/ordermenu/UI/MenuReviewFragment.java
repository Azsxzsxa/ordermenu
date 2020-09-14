package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ordermenu.R;

public class MenuReviewFragment extends Fragment {
    View view;

    public MenuReviewFragment() {
    }

    public static MenuReviewFragment newInstance(String param1, String param2) {
        MenuReviewFragment fragment = new MenuReviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_review, container, false);
        return view;
    }
}