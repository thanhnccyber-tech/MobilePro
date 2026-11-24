package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final List<Category> categoryList = new ArrayList<>();
    private final List<Product> featuredProducts = new ArrayList<>();
    private LinearLayout layoutCategories, layoutFeaturedProducts;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        layoutCategories = findViewById(R.id.layoutCategories);
        layoutFeaturedProducts = findViewById(R.id.layoutFeaturedProducts);

        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
        TextView tvWelcome = findViewById(R.id.tvWelcomeUser);
        if (!username.isEmpty()) tvWelcome.setText("Xin chào, " + username);

        findViewById(R.id.cardQuickSearch).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("focusSearch", true);
            startActivity(intent);
        });

        findViewById(R.id.btnExploreBanner).setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProductListActivity.class)));

        findViewById(R.id.tvSeeAllProducts).setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProductListActivity.class)));

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {

            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(HomeActivity.this, ProductListActivity.class));
                finish();
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                finish();
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().remove("username").apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_bottom_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_bottom_home) {
                return true;
            } else if (id == R.id.nav_bottom_explore) {
                startActivity(new Intent(HomeActivity.this, ProductListActivity.class));
                finish();
            } else if (id == R.id.nav_bottom_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                finish();
            } else if (id == R.id.nav_bottom_account) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
            }

            return false;
        });

        loadCategories();
        loadFeaturedProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
        TextView tvWelcome = findViewById(R.id.tvWelcomeUser);
        if (!username.isEmpty()) tvWelcome.setText("Xin chào, " + username);
    }

    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(snap -> {
            categoryList.clear();
            for (DocumentSnapshot doc : snap) {
                Category category = doc.toObject(Category.class);
                if (category != null) {
                    categoryList.add(category);
                }
            }
            renderCategories();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi tải danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void renderCategories() {
        layoutCategories.removeAllViews();
        CategoryAdapter adapter = new CategoryAdapter(this, categoryList);
        for (int i = 0; i < adapter.getCount(); i++) {
            Category category = categoryList.get(i);
            View itemView = adapter.getView(i, null, layoutCategories);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
                intent.putExtra("selectedCategory", category.getName());
                startActivity(intent);
            });
            layoutCategories.addView(itemView);
        }
    }

    private void loadFeaturedProducts() {
        db.collection("products").limit(4).get().addOnSuccessListener(snap -> {
            featuredProducts.clear();
            for (DocumentSnapshot doc : snap) {
                Product product = doc.toObject(Product.class);
                if (product != null) {
                    product.setDocumentId(doc.getId());
                    if (product.getImageResId() == 0) product.setImageResId(R.drawable.banhkem);
                    featuredProducts.add(product);
                }
            }
            renderFeaturedProducts();
        });
    }

    private void renderFeaturedProducts() {
        layoutFeaturedProducts.removeAllViews();
        ProductAdapter adapter = new ProductAdapter(this, featuredProducts);
        for (int i = 0; i < adapter.getCount(); i++) {
            Product product = featuredProducts.get(i);
            View itemView = adapter.getView(i, null, layoutFeaturedProducts);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            });
            layoutFeaturedProducts.addView(itemView);
        }
    }

}