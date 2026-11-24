package com.example.mobilepro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvUsername = findViewById(R.id.tvProfileUsername);
        Button btnEditInfo = findViewById(R.id.btnEditInfo);
        Button btnOrderHistory = findViewById(R.id.btnOrderHistory);
        Button btnFavorites = findViewById(R.id.btnFavorites);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnUserGuide = findViewById(R.id.btnUserGuide);
        Button btnLogout = findViewById(R.id.btnLogout);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        tvUsername.setText(username);

        btnEditInfo.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));

        btnOrderHistory.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class)));

        btnFavorites.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class)));

        findViewById(R.id.btnAddresses).setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, AddressListActivity.class)));

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        btnUserGuide.setOnClickListener(v ->
                Toast.makeText(this, "Hướng dẫn sử dụng (chưa gán chức năng)", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            prefs.edit().clear().apply();
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

    private void showChangePasswordDialog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        dialog.setContentView(view);

        TextInputLayout tilCurrent = view.findViewById(R.id.tilCurrentPassword);
        TextInputLayout tilNew = view.findViewById(R.id.tilNewPassword);
        TextInputLayout tilConfirm = view.findViewById(R.id.tilConfirmNewPassword);

        TextInputEditText edtCurrent = view.findViewById(R.id.edtCurrentPassword);
        TextInputEditText edtNew = view.findViewById(R.id.edtNewPassword);
        TextInputEditText edtConfirm = view.findViewById(R.id.edtConfirmNewPassword);

        MaterialButton btnCancel = view.findViewById(R.id.btnCancelChangePass);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirmChangePass);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            tilCurrent.setError(null);
            tilNew.setError(null);
            tilConfirm.setError(null);

            String currentPass = getText(edtCurrent);
            String newPass = getText(edtNew);
            String confirmPass = getText(edtConfirm);

            if (currentPass.isEmpty()) { tilCurrent.setError("Vui lòng nhập mật khẩu hiện tại"); return; }
            if (newPass.length() < 6) { tilNew.setError(getString(R.string.error_password_short)); return; }
            if (newPass.equals(currentPass)) { tilNew.setError("Mật khẩu mới phải khác mật khẩu hiện tại"); return; }
            if (!newPass.equals(confirmPass)) { tilConfirm.setError(getString(R.string.error_password_not_match)); return; }

            btnConfirm.setEnabled(false);
            btnConfirm.setText("Đang xử lý...");

            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPass))
                    .continueWithTask(t -> {
                        if (!t.isSuccessful() && t.getException() != null) throw t.getException();
                        return user.updatePassword(newPass);
                    })
                    .addOnSuccessListener(unused -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Cập nhật");
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            tilCurrent.setError("Mật khẩu hiện tại không đúng");
                        } else {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    private String getText(TextInputEditText edt) {
        return edt.getText() != null ? edt.getText().toString().trim() : "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        TextView tvUsername = findViewById(R.id.tvProfileUsername);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        tvUsername.setText(prefs.getString("username", ""));
        ImageLoader.loadAvatar(imgAvatar, prefs.getString("avatarUrl", ""));
    }
}