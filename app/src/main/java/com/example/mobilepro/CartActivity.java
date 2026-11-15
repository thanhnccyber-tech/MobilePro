package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private final List<Product> cartProducts = new ArrayList<>();
    private CartAdapter adapter;
    private DrawerLayout drawerLayout;
    private DatabaseHelper dbHelper;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);
        cartProducts.addAll(dbHelper.getCartProducts());

        ListView listView = findViewById(R.id.listCart);
        tvTotal = findViewById(R.id.tvTotalPrice);

        adapter = new CartAdapter(this, cartProducts, dbHelper, this::refreshCart);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = cartProducts.get(position);
            Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        Button btnClear = findViewById(R.id.btnClearCart);
        btnClear.setOnClickListener(v -> {
            dbHelper.clearCart();
            refreshCart();
            Toast.makeText(this, "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        Button btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v ->
                Toast.makeText(this, "Thanh toán (chưa gán chức năng)", Toast.LENGTH_SHORT).show());

        updateTotal();

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                startActivity(new Intent(CartActivity.this, ProductListActivity.class));
                finish();
            } else if (id == R.id.nav_home) {
                Toast.makeText(this, "Trang chủ (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_cart) {

            } else if (id == R.id.nav_account) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().remove("username").apply();
                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_bottom_cart);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_bottom_cart) {
                return true;
            } else if (id == R.id.nav_bottom_explore) {
                startActivity(new Intent(CartActivity.this, ProductListActivity.class));
                finish();
            } else if (id == R.id.nav_bottom_home) {
                Toast.makeText(this, "Trang chủ (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_bottom_account) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                finish();
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCart();
    }

    private void refreshCart() {
        cartProducts.clear();
        cartProducts.addAll(dbHelper.getCartProducts());
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        int total = dbHelper.getCartTotal();
        tvTotal.setText("Tổng tiền: " + formatPrice(total));
    }

    private String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }


}