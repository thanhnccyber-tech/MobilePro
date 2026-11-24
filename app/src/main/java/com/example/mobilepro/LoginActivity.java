package com.example.mobilepro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText edtUsername, edtPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        edtUsername = findViewById(R.id.edtLoginUsername);
        edtPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        String registeredUser = getIntent().getStringExtra("username");
        if (registeredUser != null) {
            edtUsername.setText(registeredUser);
        }

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("username", edtUsername.getText().toString());
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            tilUsername.setError(null);
            tilPassword.setError(null);

            String email = edtUsername.getText() != null ? edtUsername.getText().toString().trim() : "";
            String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

            if (email.isEmpty()) {
                tilUsername.setError(getString(R.string.error_username_empty));
                return;
            }
            if (password.isEmpty()) {
                tilPassword.setError(getString(R.string.error_password_empty));
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                                    .addOnCompleteListener(profileTask -> {
                                        String displayName = email;
                                        String avatarUrl = "";
                                        if (profileTask.isSuccessful() && profileTask.getResult() != null) {
                                            UserProfile profile = profileTask.getResult().toObject(UserProfile.class);
                                            if (profile != null) {
                                                if (profile.getFullName() != null && !profile.getFullName().isEmpty()) {
                                                    displayName = profile.getFullName();
                                                }
                                                if (profile.getAvatarUrl() != null) {
                                                    avatarUrl = profile.getAvatarUrl();
                                                }
                                            }
                                        }
                                        getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                                                .putString("username", displayName)
                                                .putString("avatarUrl", avatarUrl)
                                                .apply();

                                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    });
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Sai thông tin";
                            Toast.makeText(this, "Đăng nhập thất bại: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}