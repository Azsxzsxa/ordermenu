package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ordermenu.Adapters.RVMenuCategoriesAdapter;
import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.SECTION_DOC_ID;
import static com.example.ordermenu.Utils.StrUtil.TABLE_DOC_ID;

public class MenuCategoriesFragment extends Fragment implements RVMenuCategoriesAdapter.ItemClickListener {
    private static final String TAG = "MenuCategoriesFragment";

    List<String> _menuCategories = new ArrayList<>();
    View view;
    String _section_doc_id, _table_doc_id;
    RVMenuCategoriesAdapter _rvMenuAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_categories, container, false);

        initRV();
        getCategories();
        return view;
    }

    private void getCategories() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                            if (restaurant != null) {
                                for (String category : restaurant.getMenuCategories()) {
                                    _menuCategories.add(category);
                                    _rvMenuAdapter.notifyItemInserted(_menuCategories.size() - 1);
                                }
                            }
                        }
                    }
                });
    }

    private void initRV() {
        //init RV
        RecyclerView recyclerView = view.findViewById(R.id.RV_menuCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuAdapter = new RVMenuCategoriesAdapter(getContext(), _menuCategories);
        _rvMenuAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvMenuAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            _section_doc_id = getArguments().getString(SECTION_DOC_ID, "");
            _table_doc_id = getArguments().getString(TABLE_DOC_ID, "");

            Toast.makeText(getContext(), _table_doc_id, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCategoryClick(View view, int position) {
        Navigation.findNavController(view).navigate(MenuCategoriesFragmentDirections.actionMenuCategoriesFragmentToMenuItemsFragment(_menuCategories.get(position)));
    }
}