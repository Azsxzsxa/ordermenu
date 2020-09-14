package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.StrUtil;

import java.util.ArrayList;

import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_DOC_ID;

public class MenuCategoriesFragment extends Fragment {
    private static final String TAG = "MenuCategoriesFragment";
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_categories, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String section_doc_id = getArguments().getString(SECTION_DOC_ID, "");
            String table_doc_Id = getArguments().getString(TABLE_DOC_ID, "");

            Toast.makeText(getContext(), table_doc_Id, Toast.LENGTH_SHORT).show();
        }

//        Navigation.findNavController(view).navigate(R.id.action_menuCategoriesFragment_to_menuItemsFragment);
    }
}