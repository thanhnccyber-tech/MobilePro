package com.example.mobilepro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvUsername = findViewById(R.id.tvProfileUsername);
        Button btnEditInfo = findViewById(R.id.btnEditInfo);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnUserGuide = findViewById(R.id.btnUserGuide);
        Button btnLogout = findViewById(R.id.btnLogout);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        tvUsername.setText(username);

        btnEditInfo.setOnClickListener(v ->
                Toast.makeText(this, "Chỉnh sửa thông tin (chưa gán chức năng)", Toast.LENGTH_SHORT).show());

        btnChangePassword.setOnClickListener(v ->
                Toast.makeText(this, "Đổi mật khẩu (chưa gán chức năng)", Toast.LENGTH_SHORT).show());

        btnUserGuide.setOnClickListener(v ->
                Toast.makeText(this, "Hướng dẫn sử dụng (chưa gán chức năng)", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            prefs.edit().remove("username").apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        bottomNav.setSelectedItemId(R.id.nav_bottom_account);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_bottom_account) {
                return true;
            } else if (id == R.id.nav_bottom_explore) {
                startActivity(new Intent(ProfileActivity.this, ProductListActivity.class));
                finish();
            } else if (id == R.id.nav_bottom_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            } else if (id == R.id.nav_bottom_cart) {
                startActivity(new Intent(ProfileActivity.this, CartActivity.class));
                finish();
            }

            return false;
        });
    }
}