package com.example.ordermenu.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Adapters.RVMenuItemAdapter;
import com.example.ordermenu.Adapters.RVPrevAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_TABLE_STATUS_BUSY;

public class MenuReviewFragment extends Fragment implements RVMenuItemAdapter.ItemClickListener {
    private static final String TAG = "MenuReviewFragment";

    View view;
    List<MenuItem> _menuItemList = new ArrayList<>();
    RVMenuItemAdapter _rvMenuItemAdapter;
    RVPrevAdapter _rvMenuPrevAdapter;
    ExtendedFloatingActionButton fab_add_to_order;
    int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_review, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack();
            }
        });

        initViews();
        return view;
    }

    private void initViews() {
        fab_add_to_order = view.findViewById(R.id.menuReview_add_fab);
        fab_add_to_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderToDB();
            }
        });

        //current order RV
        _menuItemList = OrderUtil.getInstance().getCurrentOrderList();
        RecyclerView recyclerView = view.findViewById(R.id.menuReview_order_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _rvMenuItemAdapter = new RVMenuItemAdapter(getContext(), _menuItemList);
        _rvMenuItemAdapter.setClickListener(this);
        recyclerView.setAdapter(_rvMenuItemAdapter);

        //already ordered RV
        if (OrderUtil.getInstance().getServedOrderedList().size() > 0) {
            RecyclerView prevRecyclerView = view.findViewById(R.id.menuReview_prev_rv);
            prevRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            _rvMenuPrevAdapter = new RVPrevAdapter(getContext(), OrderUtil.getInstance().getServedOrderedList());
            prevRecyclerView.setAdapter(_rvMenuPrevAdapter);
        }

    }

    private void sendOrderToDB() {
        counter = 0;
        List<MenuItem> inProgressList = OrderUtil.getInstance().getInProgressOrderedList();
        for (MenuItem menuItem : _menuItemList) {
            if (inProgressList.contains(menuItem)) {
                menuItem.setQuantity(menuItem.getQuantity() + inProgressList.get(inProgressList.indexOf(menuItem)).getQuantity());
            }

            Database.getInstance().getInProgressRef().document(menuItem.getDocument_id())
                    .set(menuItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    counter++;
                    if (counter == _menuItemList.size()) {
                        OrderUtil.getInstance().clearMenuItemList();

                        //Update table to be occupied
                        WriteBatch batch = Database.getInstance().getDb().batch();
                        batch.update(Database.getInstance().getTableRef(), "occupied", DB_TABLE_STATUS_BUSY);

                        if (OrderUtil.getInstance().getServedOrderedList().size() == 0) {
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