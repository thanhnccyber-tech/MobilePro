package com.example.mobilepro;

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

        // Dữ liệu sản phẩm mẫu
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Apple", "30.000đ", "Táo đỏ nhập khẩu, giòn ngọt"));
        productList.add(new Product("Banana", "15.000đ", "Chuối chín tự nhiên"));
        productList.add(new Product("Cherry", "120.000đ", "Cherry Mỹ, quả to"));
        productList.add(new Product("Date", "90.000đ", "Quả chà là khô"));
        productList.add(new Product("Grapes", "60.000đ", "Nho xanh không hạt"));

        ListView listView = findViewById(R.id.listView);
        ProductAdapter adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

    }
}