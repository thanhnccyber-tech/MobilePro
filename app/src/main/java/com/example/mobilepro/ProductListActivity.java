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

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private static final int ITEMS_PER_PAGE = 10;

    private List<Product> allProducts = new ArrayList<>();
    private List<Product> pageProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private int currentPage = 0;
    private int totalPages = 1;

    private TextView tvPageInfo;
    private Button btnPrev, btnNext;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Danh sách sản phẩm đầy đủ
        allProducts.add(new Product("Apple", "30.000đ", "Táo đỏ nhập khẩu, giòn ngọt", R.drawable.apple));
        allProducts.add(new Product("Banana", "15.000đ", "Chuối chín tự nhiên", R.drawable.apple));
        allProducts.add(new Product("Cherry", "120.000đ", "Cherry Mỹ, quả to", R.drawable.apple));
        allProducts.add(new Product("Date", "90.000đ", "Quả chà là khô", R.drawable.apple));
        allProducts.add(new Product("Grapes", "60.000đ", "Nho xanh không hạt", R.drawable.apple));
        allProducts.add(new Product("Kiwi", "50.000đ", "Kiwi New Zealand", R.drawable.apple));
        allProducts.add(new Product("Lemon", "20.000đ", "Chanh tươi", R.drawable.apple));
        allProducts.add(new Product("Mango", "45.000đ", "Xoài cát Hòa Lộc", R.drawable.apple));
        allProducts.add(new Product("Orange", "35.000đ", "Cam sành", R.drawable.apple));
        allProducts.add(new Product("Papaya", "25.000đ", "Đu đủ chín", R.drawable.apple));
        allProducts.add(new Product("Pineapple", "30.000đ", "Thơm mật", R.drawable.apple));
        allProducts.add(new Product("Strawberry", "150.000đ", "Dâu tây Đà Lạt", R.drawable.apple));
        allProducts.add(new Product("Watermelon", "20.000đ", "Dưa hấu không hạt", R.drawable.apple));
        allProducts.add(new Product("Blueberry", "200.000đ", "Việt quất Mỹ", R.drawable.apple));
        allProducts.add(new Product("Raspberry", "180.000đ", "Mâm xôi đỏ", R.drawable.apple));
        allProducts.add(new Product("Peach", "70.000đ", "Đào tiên", R.drawable.apple));
        allProducts.add(new Product("Pear", "55.000đ", "Lê Hàn Quốc", R.drawable.apple));
        allProducts.add(new Product("Plum", "40.000đ", "Mận hậu", R.drawable.apple));
        allProducts.add(new Product("Coconut", "25.000đ", "Dừa xiêm", R.drawable.apple));
        allProducts.add(new Product("Avocado", "80.000đ", "Bơ sáp", R.drawable.apple));
        allProducts.add(new Product("Dragon Fruit", "35.000đ", "Thanh long ruột đỏ", R.drawable.apple));
        allProducts.add(new Product("Lychee", "65.000đ", "Vải thiều", R.drawable.apple));
        allProducts.add(new Product("Rambutan", "45.000đ", "Chôm chôm", R.drawable.apple));

        totalPages = (int) Math.ceil((double) allProducts.size() / ITEMS_PER_PAGE);

        ListView listView = findViewById(R.id.listView);
        adapter = new ProductAdapter(this, pageProducts);
        listView.setAdapter(adapter);

        tvPageInfo = findViewById(R.id.tvPageInfo);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        // Nút chuyển trang trước
        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });

        // Nút chuyển trang sau
        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                updatePage();
            }
        });

        // Mở chi tiết sản phẩm
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = pageProducts.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        // Thiết lập menu trượt bên trái
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Button btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Về trang chủ = trang đầu danh sách
                currentPage = 0;
                updatePage();
            } else if (id == R.id.nav_account) {
                // Chưa gán chức năng
                Toast.makeText(this, "Thông tin tài khoản (chưa gán chức năng)",
                        Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                // Đăng xuất: quay về Login và xoá back stack
                Intent intent = new Intent(ProductListActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        updatePage();
    }

    // Cập nhật danh sách hiển thị theo trang hiện tại
    private void updatePage() {
        pageProducts.clear();
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allProducts.size());
        for (int i = start; i < end; i++) {
            pageProducts.add(allProducts.get(i));
        }
        adapter.notifyDataSetChanged();

        tvPageInfo.setText("Trang " + (currentPage + 1) + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
    }

    // Bấm back: nếu drawer đang mở thì đóng, ngược lại thoát bình thường
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}