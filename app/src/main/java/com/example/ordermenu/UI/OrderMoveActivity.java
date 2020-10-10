package com.example.ordermenu.UI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordermenu.Adapters.RVOrderMoveAdapter;
import com.example.ordermenu.Models.MenuItem;
import com.example.ordermenu.Models.Section;
import com.example.ordermenu.R;
import com.example.ordermenu.Utils.Database;
import com.example.ordermenu.Utils.OrderUtil;
import com.example.ordermenu.Utils.StrUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import static com.example.ordermenu.Utils.StrUtil.DB_CURRENT;
import static com.example.ordermenu.Utils.StrUtil.DB_ORDER_INPROGRESS;
import static com.example.ordermenu.Utils.StrUtil.DB_ORDER_SERVED;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLES;
import static com.example.ordermenu.Utils.StrUtil.DB_TABLE_STATUS_FREE;

public class OrderMoveActivity extends AppCompatActivity implements RVOrderMoveAdapter.TableClick {
    private static final String TAG = "OrderMoveActivity";
    ArrayList<Section> sections = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_move);
        progressBar = findViewById(R.id.ordermove_pb);

        getSections();
    }

    private void getSections() {
        Database.getInstance().restRef.document(Database.getInstance().getRestaurantId()).collection(DB_CURRENT)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Section section = doc.toObject(Section.class);
                    if (section != null) {
                        section.setDocumentID(doc.getId());
                        sections.add(section);
                    }
                }
                if (!sections.isEmpty()) {
                    initRV();
                    if (progressBar.getVisibility() == View.VISIBLE){
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.ordermove_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderMoveActivity.this));
        RVOrderMoveAdapter _orderMoveAdapter = new RVOrderMoveAdapter(this, sections);
        _orderMoveAdapter.setClickListener(this);
        recyclerView.setAdapter(_orderMoveAdapter);

    }

    @Override
    public void onMoveTableClick(View view, final int tablePosition, final int sectionPosition) {
        Log.d(TAG, "onMoveTableClick: "+ tablePosition + " " +sectionPosition);
        final Dialog dialog = new Dialog(OrderMoveActivity.this, R.style.MyThemeDialogCustom);
        dialog.setContentView(R.layout.dialog_yes_no);
        TextView textView = dialog.findViewById(R.id.dialog_yesNo_text);
        MaterialButton yesBtn = dialog.findViewById(R.id.dialog_yesNo_yes_btn);
        MaterialButton noBtn = dialog.findViewById(R.id.dialog_yesNo_No_btn);
        int tableTextInt = tablePosition + 1;
        String moveQuestion = getString(R.string.move_to) + getString(R.string.table_move) + tableTextInt + getString(R.string.from_move) + sections.get(sectionPosition).getName();
        textView.setText(moveQuestion);

        final String tableDocId = sections.get(sectionPosition).getTableList().get(tablePosition).getDocumentID();
        final String tableStatus = OrderUtil.getInstance().getTableStatus();
        Log.d(TAG, "onMoveTableClick: "+tableStatus);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                final CollectionReference oldInProgRef = Database.getInstance().getInProgressRef();
                final CollectionReference oldServedRef = Database.getInstance().getServedRef();
                final DocumentReference newTableRef = Database.getInstance().restRef.document(Database.getInstance().getRestaurantId())
                        .collection(StrUtil.DB_CURRENT).document(sections.get(sectionPosition).getDocumentID())
                        .collection(DB_TABLES).document(tableDocId);

                newTableRef.collection(DB_ORDER_INPROGRESS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            WriteBatch batch = Database.getInstance().getDb().batch();
                            for (MenuItem menuItem : OrderUtil.getInstance().getServedOrderedList()) {
                                Log.d(TAG, "onClick: " + menuItem.getName());
                                batch.set(newTableRef.collection(DB_ORDER_SERVED).document(menuItem.getDocument_id()), menuItem);
                                batch.delete(oldServedRef.document(menuItem.getDocument_id()));
                            }
                            for (MenuItem menuItem : OrderUtil.getInstance().getInProgressOrderedList()) {
                                Log.d(TAG, "onClick: " + menuItem.getName());
                                batch.set(newTableRef.collection(DB_ORDER_INPROGRESS).document(menuItem.getDocument_id()), menuItem);
                                batch.delete(oldInProgRef.document(menuItem.getDocument_id()));
                            }
                            batch.update(Database.getInstance().getTableRef(), "occupied", DB_TABLE_STATUS_FREE);
                            batch.update(newTableRef, "occupied", tableStatus);

                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: ");
                                    dialog.cancel();
                                    Intent intent = new Intent(getApplication(), TablesActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }else{
                            dialog.cancel();
                            final Dialog innerDialog = new Dialog(OrderMoveActivity.this, R.style.MyThemeDialogCustom);
                            innerDialog.setContentView(R.layout.dialog_info_warning);

                            TextView textView = innerDialog.findViewById(R.id.dialog_infoWarning_text);
                            MaterialButton okBtn = innerDialog.findViewById(R.id.dialog_infoWarning_ok_btn);
                            okBtn.setText(R.string.ok);
                            textView.setText(R.string.order_move_info);

                            okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    innerDialog.cancel();
                                }
                            });

                            innerDialog.setCancelable(true);
                            innerDialog.show();
                        }
                    }
                });

            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
