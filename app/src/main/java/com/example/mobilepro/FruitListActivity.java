package com.example.mobilepro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class FruitListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_list);

        String[] items = {"Apple", "Banana", "Cherry", "Date", "Grapes"};
        // Find ListView by ID
        ListView listView = findViewById(R.id.listView);
        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        // Set adapter to ListView
        listView.setAdapter(adapter);
    }
}