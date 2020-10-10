package com.example.ordermenu.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    ExtendedFloatingActionButton reviewFAB;
    private RecyclerView categoryRecyclerView;
    private RecyclerView searchRecyclerView;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_categories, container, false);
        reviewFAB = view.findViewById(R.id.menuCategory_review_fab);
        initRV();
        getCategories();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (TextUtils.isEmpty(searchView.getQuery())) {
                    Intent intent = new Intent(getActivity(), OrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    requireActivity().finish();
                }else{
                    searchView.setQuery("",true);
                    searchView.clearFocus();
                }

            }
        });

        reviewFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(MenuCategoryFragmentDirections.actionMenuCategoriesFragmentToMenuReviewFragment());
            }
        });

        searchView = view.findViewById(R.id.menuCategory_search_sv);
        searchView.onActionViewExpanded();
        searchView.clearFocus();



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
        categoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    reviewFAB.hide();
                } else{
                    reviewFAB.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });


        OrderUtil.getInstance().updateSearchItemList();
        //search menuitem rv
        searchRecyclerView = view.findViewById(R.id.menuCategory_search_rv);
        searchRecyclerView.setVisibility(View.INVISIBLE);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuItemAdapter = new RVMenuItemAdapter(getContext(), OrderUtil.getInstance().getSearchMenuItemsList());
        _rvMenuItemAdapter.setClickListener(this);
        searchRecyclerView.setAdapter(_rvMenuItemAdapter);
        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    reviewFAB.hide();
                } else{
                    reviewFAB.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

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