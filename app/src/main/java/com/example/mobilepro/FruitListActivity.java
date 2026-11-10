package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FruitListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_list);

        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Apple", "30.000đ", "Táo đỏ nhập khẩu, giòn ngọt", R.drawable.apple));
        productList.add(new Product("Banana", "15.000đ", "Chuối chín tự nhiên", R.drawable.apple));
        productList.add(new Product("Cherry", "120.000đ", "Cherry Mỹ, quả to", R.drawable.apple));
        productList.add(new Product("Date", "90.000đ", "Quả chà là khô", R.drawable.apple));
        productList.add(new Product("Grapes", "60.000đ", "Nho xanh không hạt", R.drawable.apple));

        ListView listView = findViewById(R.id.listView);
        ProductAdapter adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);

            Intent intent = new Intent(FruitListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });
    }
}