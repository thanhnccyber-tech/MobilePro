package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private TextView tvQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        MaterialButton btnAddToCart = findViewById(R.id.btnAddToCart);
        MaterialButton btnMinus = findViewById(R.id.btnMinus);
        MaterialButton btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);

        topAppBar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.btnCart).setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
            finish();
        });

        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            imgDetail.setImageResource(product.getImageResId());
            tvName.setText(product.getName());
            tvPrice.setText("Giá: " + Product.formatPrice(product.getPrice()));
            tvDescription.setText(product.getDescription());

            btnAddToCart.setOnClickListener(v ->
                    new FirebaseCartHelper().addToCart(product, quantity)
                            .addOnSuccessListener(unused -> {
                                startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(ProductDetailActivity.this,
                                            "Lỗi thêm giỏ: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()));
        }

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });
    }
}