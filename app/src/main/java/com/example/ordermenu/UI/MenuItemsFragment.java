package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ordermenu.Adapters.RVMenuItemAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.MENU;

public class MenuItemsFragment extends Fragment implements RVMenuItemAdapter.ItemClickListener {
    private static final String TAG = "MenuItemsFragment";

    String _category;
    List<MenuItem> menuItemList = new ArrayList<>();
    RVMenuItemAdapter _rvMenuItemAdapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_items, container, false);

        if (getArguments() != null) {
            MenuItemsFragmentArgs menuItemsFragmentArgs = MenuItemsFragmentArgs.fromBundle(getArguments());
            _category = menuItemsFragmentArgs.getCategory();
            getMenuItemsFromDb();
        }
        initRV();
        return view;
    }

    private void initRV() {
        RecyclerView recyclerView = view.findViewById(R.id.RV_menuItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuItemAdapter = new RVMenuItemAdapter(getContext(), menuItemList);
        _rvMenuItemAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvMenuItemAdapter);
    }

    private void getMenuItemsFromDb() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(MENU).whereEqualTo("category", _category)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        MenuItem menuItem = documentSnapshot.toObject(MenuItem.class);
                        if (menuItemList.contains(menuItem)) {
                            menuItemList.set(menuItemList.indexOf(menuItem), menuItem);
                            _rvMenuItemAdapter.notifyItemChanged(menuItemList.indexOf(menuItem));
                        } else {
                            menuItemList.add(menuItem);
                            _rvMenuItemAdapter.notifyItemInserted(menuItemList.size() - 1);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), menuItemList.get(position).getName(), Toast.LENGTH_SHORT).show();
    }
}