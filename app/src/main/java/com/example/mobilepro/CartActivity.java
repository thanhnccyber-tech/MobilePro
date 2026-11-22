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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private final List<Product> cartProducts = new ArrayList<>();
    private CartAdapter adapter;
    private DrawerLayout drawerLayout;
    private FirebaseCartHelper cartHelper;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartHelper = new FirebaseCartHelper();

        ListView listView = findViewById(R.id.listCart);
        tvTotal = findViewById(R.id.tvTotalPrice);

        adapter = new CartAdapter(this, cartProducts, cartHelper, this::loadCart);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = cartProducts.get(position);
            Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        Button btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            if (cartProducts.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        Button btnClear = findViewById(R.id.btnClearCart);
        btnClear.setOnClickListener(v ->
                cartHelper.clearCart().addOnSuccessListener(unused -> {
                    loadCart();
                    Toast.makeText(this, "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                }));

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

        loadCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        cartHelper.getCart().addOnSuccessListener(snap -> {
            cartProducts.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Product product = doc.toObject(Product.class);
                if (product != null) {
                    if (product.getImageResId() == 0) {
                        product.setImageResId(R.drawable.apple);
                    }
                    cartProducts.add(product);
                }
            }
            adapter.notifyDataSetChanged();
            updateTotal();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi tải giỏ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateTotal() {
        int total = 0;
        for (Product p : cartProducts) {
            total += parsePrice(p.getPrice()) * p.getQuantity();
        }
        tvTotal.setText("Tổng tiền: " + formatPrice(total));
    }

    private int parsePrice(String price) {
        try {
            return Integer.parseInt(price.replace("đ", "").replace(".", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}