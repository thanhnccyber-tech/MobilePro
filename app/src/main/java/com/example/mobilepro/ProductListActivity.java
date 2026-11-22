package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> filteredProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;
    private ChipGroup chipGroupCategory;

    private String selectedCategory = "Tất cả";
    private String searchKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = FirebaseFirestore.getInstance();

        ListView listView = findViewById(R.id.listView);
        adapter = new ProductAdapter(this, filteredProducts);
        listView.setAdapter(adapter);

        setupSearchAndFilter();
        loadCategoriesFromFirestore();
        loadProductsFromFirestore();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = filteredProducts.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

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

    private void setupSearchAndFilter() {
        TextInputEditText edtSearch = findViewById(R.id.edtSearch);
        chipGroupCategory = findViewById(R.id.chipGroupCategory);

        String intentCategory = getIntent().getStringExtra("selectedCategory");
        if (intentCategory != null && !intentCategory.isEmpty()) {
            selectedCategory = intentCategory;
        }

        if (getIntent().getBooleanExtra("focusSearch", false)) {
            edtSearch.requestFocus();
        }

        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                if (chip != null) {
                    selectedCategory = chip.getText().toString();
                    filterProducts();
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchKeyword = s != null ? s.toString().trim().toLowerCase() : "";
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCategoriesFromFirestore() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    chipGroupCategory.removeAllViews();
                    addCategoryChip("Tất cả", selectedCategory.equalsIgnoreCase("Tất cả"));

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null && !name.isEmpty()) {
                            addCategoryChip(name, selectedCategory.equalsIgnoreCase(name));
                        }
                    }
                });
    }

    private void addCategoryChip(String title, boolean isChecked) {
        Chip chip = new Chip(this, null, com.google.android.material.R.attr.chipStyle);
        chip.setText(title);
        chip.setCheckable(true);
        chip.setChecked(isChecked);
        chipGroupCategory.addView(chip);
    }

    private void loadProductsFromFirestore() {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        if (product.getImageResId() == 0) product.setImageResId(R.drawable.apple);
                        if (product.getCategory() == null) product.setCategory("Khác");
                        allProducts.add(product);
                    }
                    filterProducts();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void filterProducts() {
        filteredProducts.clear();
        for (Product product : allProducts) {
            boolean matchCategory = selectedCategory.equalsIgnoreCase("Tất cả")
                    || (product.getCategory() != null && product.getCategory().equalsIgnoreCase(selectedCategory));

            boolean matchKeyword = searchKeyword.isEmpty()
                    || (product.getName() != null && product.getName().toLowerCase().contains(searchKeyword));

            if (matchCategory && matchKeyword) {
                filteredProducts.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }
}