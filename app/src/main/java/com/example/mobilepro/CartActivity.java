package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private List<Product> cartProducts;
    private ProductAdapter adapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        cartProducts = dbHelper.getCartProducts();

        ListView listView = findViewById(R.id.listCart);
        adapter = new ProductAdapter(this, cartProducts);
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
            cartProducts.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show();
        });

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

    // Bấm back: nếu drawer đang mở thì đóng
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}