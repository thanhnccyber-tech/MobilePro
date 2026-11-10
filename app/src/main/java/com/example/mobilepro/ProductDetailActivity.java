package com.example.mobilepro;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            imgDetail.setImageResource(product.getImageResId());
            tvName.setText(product.getName());
            tvPrice.setText("Giá: " + product.getPrice());
            tvDescription.setText(product.getDescription());
        }
    }
}