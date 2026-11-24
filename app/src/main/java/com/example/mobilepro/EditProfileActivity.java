package com.example.mobilepro;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextInputLayout tilFullName;
    private TextInputEditText edtEmail, edtFullName, edtPhone, edtAvatarUrl;
    private MaterialButton btnSave;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        ((MaterialToolbar) findViewById(R.id.topAppBar)).setNavigationOnClickListener(v -> finish());
        imgAvatar = findViewById(R.id.imgEditAvatar);
        tilFullName = findViewById(R.id.tilEditFullName);
        edtEmail = findViewById(R.id.edtEditEmail);
        edtFullName = findViewById(R.id.edtEditFullName);
        edtPhone = findViewById(R.id.edtEditPhone);
        edtAvatarUrl = findViewById(R.id.edtEditAvatarUrl);
        btnSave = findViewById(R.id.btnSaveProfile);

        edtEmail.setText(currentUser.getEmail());

        findViewById(R.id.btnPreviewAvatar).setOnClickListener(v -> {
            String url = getText(edtAvatarUrl);
            ImageLoader.loadAvatar(imgAvatar, url);
        });

        btnSave.setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    private void loadProfile() {
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    UserProfile profile = doc.toObject(UserProfile.class);
                    if (profile != null) {
                        edtFullName.setText(profile.getFullName());
                        edtPhone.setText(profile.getPhone());
                        edtAvatarUrl.setText(profile.getAvatarUrl());
                        ImageLoader.loadAvatar(imgAvatar, profile.getAvatarUrl());
                    } else {
                        String savedName = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                .getString("username", "");
                        edtFullName.setText(savedName);
                    }
                });
    }

    private void saveProfile() {
        tilFullName.setError(null);
        String fullName = getText(edtFullName);
        String phone = getText(edtPhone);
        String avatarUrl = getText(edtAvatarUrl);

        if (fullName.isEmpty()) {
            tilFullName.setError("Vui lòng nhập tên hiển thị");
            return;
        }

        btnSave.setEnabled(false);
        UserProfile profile = new UserProfile(fullName, currentUser.getEmail(), phone, avatarUrl);

        db.collection("users").document(currentUser.getUid())
                .set(profile, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    getSharedPreferences("user_prefs", MODE_PRIVATE)
                            .edit()
                            .putString("username", fullName)
                            .putString("avatarUrl", avatarUrl)
                            .apply();
                    Toast.makeText(this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi lưu thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getText(TextInputEditText edt) {
        return edt.getText() != null ? edt.getText().toString().trim() : "";
    }
}