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
import com.example.ordermenu.Adapters.RVPrevAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.DocumentTransform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuReviewFragment extends Fragment implements RVMenuItemAdapter.ItemClickListener {
    View view;
    List<MenuItem> _menuItemList = new ArrayList<>();
    RVMenuItemAdapter _rvMenuItemAdapter;
    RVPrevAdapter _rvMenuPrevAdapter;
    ExtendedFloatingActionButton fab_add_to_order;
    int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_review, container, false);
        fab_add_to_order = view.findViewById(R.id.menuReview_add_fab);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack();
            }
        });

        initRV();

        fab_add_to_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                Log.d("asdfasdf", "onClick: "+OrderUtil.getInstance().getOrderedList().size());
                for (MenuItem menuItem : _menuItemList) {
                    List<MenuItem> orderedList = OrderUtil.getInstance().getOrderedList();

                    if (orderedList.contains(menuItem)) {
                        menuItem.setQuantity(menuItem.getQuantity() + orderedList.get(orderedList.indexOf(menuItem)).getQuantity());
                    }
                    Log.d("asdfasdf", "afterif: "+OrderUtil.getInstance().getOrderedList().size());
                    Database.getInstance().getOrderRef()
                            .document(menuItem.getDocument_id())
                            .set(menuItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            counter++;
                            Log.d("asdfasdf", "onsucc: "+OrderUtil.getInstance().getOrderedList().size());
                            if (counter == _menuItemList.size()) {
                                OrderUtil.getInstance().clearMenuItemList();

                                //Update table to be occupied
                                WriteBatch batch = Database.getInstance().getDb().batch();
                                batch.update(Database.getInstance().getTableRef(), "occupied", true);
                                Log.d("asdfasdf", "onSuccess: " + OrderUtil.getInstance().getOrderedList().size());
                                if (OrderUtil.getInstance().getOrderedList().size() == 0) {
                                    batch.update(Database.getInstance().getTableRef(), "startOrderDate", new Date());
                                    OrderUtil.getInstance().setStartOrderDate(new Date());
                                }
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (getActivity() != null)
                                            getActivity().finish();
                                    }
                                });

                            }
                        }
                    });

                }
            }
        });

        return view;
    }

    private void initRV() {
        _menuItemList = OrderUtil.getInstance().getMenuItemList();

        RecyclerView recyclerView = view.findViewById(R.id.menuReview_order_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuItemAdapter = new RVMenuItemAdapter(getContext(), _menuItemList);
        _rvMenuItemAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvMenuItemAdapter);

        Log.d("asdfasdf", "initRV: "+OrderUtil.getInstance().getOrderedList().size());
        if (OrderUtil.getInstance().getOrderedList().size() > 0) {
            RecyclerView prevRecyclerView = (RecyclerView) view.findViewById(R.id.menuReview_prev_rv);
            prevRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            _rvMenuPrevAdapter = new RVPrevAdapter(getContext(), OrderUtil.getInstance().getOrderedList());
            prevRecyclerView.setAdapter(_rvMenuPrevAdapter);
        }


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