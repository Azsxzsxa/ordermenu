package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ordermenu.Adapters.RVMenuItemAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_CATEGORY;
import static com.example.ordermenu.Utils.StrUtil.MENU;

public class MenuItemsFragment extends Fragment implements RVMenuItemAdapter.ItemClickListener {
    private static final String TAG = "MenuItemsFragment";

    String _category;
    List<MenuItem> _menuItemList = new ArrayList<>();
    RVMenuItemAdapter _rvMenuItemAdapter;
    View view;
    ExtendedFloatingActionButton fab_review;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_items, container, false);
        fab_review = view.findViewById(R.id.menuItems_review_fab);

        if (getArguments() != null) {
            MenuItemsFragmentArgs menuItemsFragmentArgs = MenuItemsFragmentArgs.fromBundle(getArguments());
            _category = menuItemsFragmentArgs.getCategory();
        }
        if (_category != null)
            getMenuItemsFromDb();

        initRV();

        //Back button listener
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack();
            }
        });

        fab_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(MenuItemsFragmentDirections.actionMenuItemsFragmentToMenuReviewFragment());
            }
        });

        return view;
    }

    private void initRV() {
        RecyclerView recyclerView = view.findViewById(R.id.menuItems_items_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuItemAdapter = new RVMenuItemAdapter(getContext(), _menuItemList);
        _rvMenuItemAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvMenuItemAdapter);
    }

    private void getMenuItemsFromDb() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(MENU).whereEqualTo(DB_CATEGORY, _category)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        MenuItem menuItem = documentSnapshot.toObject(MenuItem.class);
                        if (menuItem != null) {
                            menuItem.setDocument_id(documentSnapshot.getId());
                            if (_menuItemList.contains(menuItem)) {
                                if (OrderUtil.getInstance().getMenuItemList().contains(menuItem)) {
                                    int position = OrderUtil.getInstance().getMenuItemList().indexOf(menuItem);
                                    menuItem.setQuantity(OrderUtil.getInstance().getMenuItemList().get(position).getQuantity());
                                } else {
                                    _menuItemList.set(_menuItemList.indexOf(menuItem), menuItem);
                                }
                                _rvMenuItemAdapter.notifyItemChanged(_menuItemList.indexOf(menuItem));
                            } else {
                                if (OrderUtil.getInstance().getMenuItemList().contains(menuItem)) {
                                    int position = OrderUtil.getInstance().getMenuItemList().indexOf(menuItem);
                                    menuItem.setQuantity(OrderUtil.getInstance().getMenuItemList().get(position).getQuantity());
                                    Log.d(TAG, "onSuccess: contine " + menuItem.getName() + " " + OrderUtil.getInstance().getMenuItemList().get(position).getQuantity());
                                    _menuItemList.add(OrderUtil.getInstance().getMenuItemList().get(position));
                                } else {
                                    _menuItemList.add(menuItem);
                                }
                                _rvMenuItemAdapter.notifyItemInserted(_menuItemList.size() - 1);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onPlusClick(View view, int position) {
        OrderUtil.getInstance().increaseQuantity(_menuItemList.get(position));
        _rvMenuItemAdapter.notifyItemChanged(position);
    }

    @Override
    public void onMinusClick(View view, int position) {
        OrderUtil.getInstance().decreaseQuantity(_menuItemList.get(position));
        _rvMenuItemAdapter.notifyItemChanged(position);
    }
}