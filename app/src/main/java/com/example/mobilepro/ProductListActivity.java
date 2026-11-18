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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private final List<Product> allProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = FirebaseFirestore.getInstance();

        ListView listView = findViewById(R.id.listView);
        adapter = new ProductAdapter(this, allProducts);
        listView.setAdapter(adapter);

        loadProductsFromFirestore();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = allProducts.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Button btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_explore) {
                listView.setSelection(0);
            } else if (id == R.id.nav_home) {
                Toast.makeText(this, "Trang chủ (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(ProductListActivity.this, CartActivity.class));
                finish();
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(ProductListActivity.this, ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().remove("username").apply();
                Intent intent = new Intent(ProductListActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_bottom_explore);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_bottom_explore) {
                return true;
            } else if (id == R.id.nav_bottom_home) {
                Toast.makeText(this, "Trang chủ (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_bottom_cart) {
                startActivity(new Intent(ProductListActivity.this, CartActivity.class));
                finish();
            } else if (id == R.id.nav_bottom_account) {
                startActivity(new Intent(ProductListActivity.this, ProfileActivity.class));
                finish();
            }

            return false;
        });
    }

    private void loadProductsFromFirestore() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allProducts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String price = document.getString("price");
                            String description = document.getString("description");

                            if (name != null && price != null) {
                                allProducts.add(new Product(
                                        name,
                                        price,
                                        description != null ? description : "",
                                        R.drawable.apple
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi kết nối";
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}