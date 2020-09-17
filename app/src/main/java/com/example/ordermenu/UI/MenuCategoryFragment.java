package com.example.ordermenu.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.ordermenu.Adapters.RVMenuCategoryAdapter;
import com.example.ordermenu.Adapters.RVMenuItemAdapter;
import com.example.ordermenu.Models.Restaurant;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuCategoryFragment extends Fragment implements RVMenuCategoryAdapter.ItemClickListener, RVMenuItemAdapter.ItemClickListener {
    private static final String TAG = "MenuCategoriesFragment";

    List<String> _menuCategories = new ArrayList<>();
    View view;
    RVMenuCategoryAdapter _rvMenuAdapter;
    RVMenuItemAdapter _rvMenuItemAdapter;
    ExtendedFloatingActionButton fab_review;
    private RecyclerView categoryRecyclerView;
    private RecyclerView searchRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_categories, container, false);
        fab_review = view.findViewById(R.id.menuCategory_review_fab);
        initRV();
        getCategories();

        fab_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(MenuCategoryFragmentDirections.actionMenuCategoriesFragmentToMenuReviewFragment());
            }
        });

        final SearchView searchView = view.findViewById(R.id.menuCategory_search_sv);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    categoryRecyclerView.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    categoryRecyclerView.setVisibility(View.INVISIBLE);
                    searchRecyclerView.setVisibility(View.VISIBLE);
                }
                _rvMenuItemAdapter.getFilter().filter(newText);
                return false;
            }
        });

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
                                    if (!_menuCategories.contains(category)) {
                                        _menuCategories.add(category);
                                        _rvMenuAdapter.notifyItemInserted(_menuCategories.size() - 1);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void initRV() {
        //category RV
        categoryRecyclerView = view.findViewById(R.id.menuCategory_items_rv);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuAdapter = new RVMenuCategoryAdapter(getContext(), _menuCategories);
        _rvMenuAdapter.setClickListener(this);
        categoryRecyclerView.setAdapter(_rvMenuAdapter);


        OrderUtil.getInstance().updateSearchItemList();
        //search menuitem rv
        searchRecyclerView = view.findViewById(R.id.menuCategory_search_rv);
        searchRecyclerView.setVisibility(View.INVISIBLE);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuItemAdapter = new RVMenuItemAdapter(getContext(), OrderUtil.getInstance().getSearchMenuItemsList());
        _rvMenuItemAdapter.setClickListener(this);
        searchRecyclerView.setAdapter(_rvMenuItemAdapter);

    }


    @Override
    public void onCategoryClick(View view, int position) {
        Navigation.findNavController(view)
                .navigate(MenuCategoryFragmentDirections.actionMenuCategoriesFragmentToMenuItemsFragment(_menuCategories.get(position)));
    }

    @Override
    public void onPlusClick(View view, int position) {
        Log.d(TAG, "onPlusClick: ");

        OrderUtil.getInstance().increaseQuantity(OrderUtil.getInstance().getSearchMenuItemsList().get(position));

        int indexOfSearchItemInCurrentOrder = OrderUtil.getInstance().getCurrentOrderList().indexOf(
                OrderUtil.getInstance().getSearchMenuItemsList().get(position));

        OrderUtil.getInstance().getSearchMenuItemsList().get(position).setQuantity(
                OrderUtil.getInstance().getCurrentOrderList().get(indexOfSearchItemInCurrentOrder).getQuantity());

        _rvMenuItemAdapter.notifyItemChanged(position);
    }

    @Override
    public void onMinusClick(View view, int position) {
        OrderUtil.getInstance().decreaseQuantity(OrderUtil.getInstance().getSearchMenuItemsList().get(position));

        int indexOfSearchItemInCurrentOrder = OrderUtil.getInstance().getCurrentOrderList().indexOf(
                OrderUtil.getInstance().getSearchMenuItemsList().get(position));

        OrderUtil.getInstance().getSearchMenuItemsList().get(position).setQuantity(
                OrderUtil.getInstance().getCurrentOrderList().get(indexOfSearchItemInCurrentOrder).getQuantity());

        _rvMenuItemAdapter.notifyItemChanged(position);
    }
}