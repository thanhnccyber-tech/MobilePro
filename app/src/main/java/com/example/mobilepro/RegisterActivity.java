package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private MaterialButton btnRegister;
    private TextView tvGoToLogin;
    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText edtUsername, edtPassword, edtConfirmPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tilUsername = findViewById(R.id.tilRegisterUsername);
        tilPassword = findViewById(R.id.tilRegisterPassword);
        tilConfirmPassword = findViewById(R.id.tilRegisterConfirmPassword);
        edtUsername = findViewById(R.id.edtRegisterUsername);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword);

        String username = getIntent().getStringExtra("username");
        if (username != null) {
            edtUsername.setText(username);
        }

        btnRegister.setOnClickListener(v -> {
            tilUsername.setError(null);
            tilPassword.setError(null);
            tilConfirmPassword.setError(null);

            String email = edtUsername.getText() != null ? edtUsername.getText().toString().trim() : "";
            String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";
            String confirmPassword = edtConfirmPassword.getText() != null ? edtConfirmPassword.getText().toString().trim() : "";

            if (email.isEmpty()) {
                tilUsername.setError(getString(R.string.error_username_empty));
                return;
            }
            if (password.isEmpty()) {
                tilPassword.setError(getString(R.string.error_password_empty));
                return;
            }
            if (password.length() < 6) {
                tilPassword.setError(getString(R.string.error_password_short));
                return;
            }
            if (!password.equals(confirmPassword)) {
                tilConfirmPassword.setError(getString(R.string.error_password_not_match));
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("username", email);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(this, "Đăng ký thất bại: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}