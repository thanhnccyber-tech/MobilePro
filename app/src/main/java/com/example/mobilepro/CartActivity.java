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
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("cartProducts", new ArrayList<>(cartProducts));
            startActivity(intent);
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

        DrawerHelper.setup(this, drawerLayout, topAppBar, navigationView, R.id.nav_cart);

        BottomNavHelper.setup(this, BottomNavHelper.TAB_CART);

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
                        product.setImageResId(R.drawable.banhkem);
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
            total += p.getPrice() * p.getQuantity();
        }
        tvTotal.setText("Tổng tiền: " + Product.formatPrice(total));
    }

}