package com.example.mobilepro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
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
        TextView tvEmail = findViewById(R.id.tvProfileEmail);
        View btnEditInfo = findViewById(R.id.btnEditInfo);
        View btnOrderHistory = findViewById(R.id.btnOrderHistory);
        View btnFavorites = findViewById(R.id.btnFavorites);
        View btnAddresses = findViewById(R.id.btnAddresses);
        View btnChangePassword = findViewById(R.id.btnChangePassword);
        View btnBakeryInfo = findViewById(R.id.btnBakeryInfo);
        Button btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            tvEmail.setText(currentUser.getEmail());
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        NavigationView navigationView = findViewById(R.id.navigationView);

        DrawerHelper.setup(this, drawerLayout, topAppBar, navigationView, R.id.nav_profile);

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

        btnBakeryInfo.setOnClickListener(v -> showBakeryInfoDialog());

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            prefs.edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        BottomNavHelper.setup(this, BottomNavHelper.TAB_ACCOUNT);
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

    private void showBakeryInfoDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_bakery_info, null);
        dialog.setContentView(view);

        MaterialButton btnClose = view.findViewById(R.id.btnCloseBakeryInfo);
        MaterialButton btnCall = view.findViewById(R.id.btnCallBakery);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnCall.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:0909123456"));
            startActivity(dialIntent);
        });

        dialog.show();
    }
}