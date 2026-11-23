package com.example.mobilepro;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private final List<Order> allOrders = new ArrayList<>();
    private final List<Order> filteredOrders = new ArrayList<>();
    private OrderHistoryAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ((MaterialToolbar) findViewById(R.id.topAppBar)).setNavigationOnClickListener(v -> finish());

        tabLayout = findViewById(R.id.tabLayout);
        adapter = new OrderHistoryAdapter(this, filteredOrders);
        ((ListView) findViewById(R.id.listOrders)).setAdapter(adapter);

        for (String status : Order.ALL_STATUSES) {
            tabLayout.addTab(tabLayout.newTab().setText(status));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                filterOrders(Order.ALL_STATUSES[tab.getPosition()]);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadOrders();
    }

    private void loadOrders() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            finish();
            return;
        }

        FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(snap -> {
                    allOrders.clear();
                    for (DocumentSnapshot doc : snap) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) allOrders.add(order);
                    }
                    allOrders.sort(Comparator.comparing(o -> o.createdAt, Comparator.nullsLast(Comparator.reverseOrder())));
                    filterOrders(Order.ALL_STATUSES[Math.max(0, tabLayout.getSelectedTabPosition())]);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterOrders(String status) {
        filteredOrders.clear();
        for (Order order : allOrders) {
            if (Order.STATUS_ALL.equals(status) || status.equals(order.status)) {
                filteredOrders.add(order);
            }
        }
        adapter.notifyDataSetChanged();
        findViewById(R.id.tvEmptyOrders).setVisibility(filteredOrders.isEmpty() ? View.VISIBLE : View.GONE);
    }
}