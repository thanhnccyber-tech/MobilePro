package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private TextView tvQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        Button btnBack = findViewById(R.id.btnBack);
        ImageButton btnGoToCart = findViewById(R.id.btnGoToCart);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            imgDetail.setImageResource(product.getImageResId());
            tvName.setText(product.getName());
            tvPrice.setText("Giá: " + product.getPrice());
            tvDescription.setText(product.getDescription());

            btnAddToCart.setOnClickListener(v -> {
                new DatabaseHelper(this).addToCart(product, quantity);
                startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                finish();
            });
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

        btnGoToCart.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}