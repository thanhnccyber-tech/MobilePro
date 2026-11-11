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

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private List<Product> allProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        allProducts.add(new Product("Apple", "30.000đ", "Táo đỏ nhập khẩu, giòn ngọt", R.drawable.apple));
        allProducts.add(new Product("Banana", "15.000đ", "Chuối chín tự nhiên", R.drawable.apple));
        allProducts.add(new Product("Cherry", "120.000đ", "Cherry Mỹ, quả to", R.drawable.apple));
        allProducts.add(new Product("Date", "90.000đ", "Quả chà là khô", R.drawable.apple));
        allProducts.add(new Product("Grapes", "60.000đ", "Nho xanh không hạt", R.drawable.apple));


        ListView listView = findViewById(R.id.listView);
        adapter = new ProductAdapter(this, allProducts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = allProducts.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        // Menu trượt bên trái
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Button btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_explore) {
                // Đang ở màn Khám phá → cuộn về đầu danh sách
                listView.setSelection(0);
            } else if (id == R.id.nav_home) {
                Toast.makeText(this, "Trang chủ (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_cart) {
                Toast.makeText(this, "Giỏ hàng (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_account) {
                Toast.makeText(this, "Tài khoản (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(ProductListActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Thanh điều hướng dưới
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        // Mặc định chọn tab "Khám phá"
        bottomNav.setSelectedItemId(R.id.nav_bottom_explore);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_bottom_explore) {
                // Đang ở màn hình này, giữ nguyên
                return true;
            } else if (id == R.id.nav_bottom_home) {
                Toast.makeText(this, "Trang chủ (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_bottom_cart) {
                Toast.makeText(this, "Giỏ hàng (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_bottom_account) {
                Toast.makeText(this, "Tài khoản (chưa gán chức năng)", Toast.LENGTH_SHORT).show();
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