package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnGoToRegister, btnGoToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnGoToList = findViewById(R.id.btnGoToList);

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnGoToList.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FruitListActivity.class);
            startActivity(intent);
        });
    }
}