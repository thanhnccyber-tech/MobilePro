package com.example.mobilepro;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditAddressActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilPhone, tilDetail;
    private TextInputEditText edtName, edtPhone, edtDetail;
    private ChipGroup chipGroupLabel;
    private MaterialSwitch switchDefault;
    private MaterialButton btnSave;
    private Address editingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        tilName = findViewById(R.id.tilAddrName);
        tilPhone = findViewById(R.id.tilAddrPhone);
        tilDetail = findViewById(R.id.tilAddrDetail);
        edtName = findViewById(R.id.edtAddrName);
        edtPhone = findViewById(R.id.edtAddrPhone);
        edtDetail = findViewById(R.id.edtAddrDetail);
        chipGroupLabel = findViewById(R.id.chipGroupLabel);
        switchDefault = findViewById(R.id.switchDefaultAddress);
        btnSave = findViewById(R.id.btnSaveAddress);

        editingAddress = (Address) getIntent().getSerializableExtra("address");
        if (editingAddress != null) {
            topAppBar.setTitle("Chỉnh sửa địa chỉ");
            edtName.setText(editingAddress.getFullName());
            edtPhone.setText(editingAddress.getPhone());
            edtDetail.setText(editingAddress.getAddress());
            switchDefault.setChecked(editingAddress.isDefault());

            if ("Văn phòng".equals(editingAddress.getLabel())) {
                chipGroupLabel.check(R.id.chipOffice);
            } else if ("Khác".equals(editingAddress.getLabel())) {
                chipGroupLabel.check(R.id.chipOther);
            } else {
                chipGroupLabel.check(R.id.chipHome);
            }
        }

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        tilName.setError(null);
        tilPhone.setError(null);
        tilDetail.setError(null);

        String name = getText(edtName);
        String phone = getText(edtPhone);
        String detail = getText(edtDetail);

        if (name.isEmpty()) {
            tilName.setError("Vui lòng nhập tên người nhận");
            return;
        }
        if (phone.isEmpty()) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (detail.isEmpty()) {
            tilDetail.setError("Vui lòng nhập địa chỉ chi tiết");
            return;
        }

        String label = "Nhà riêng";
        int checkedId = chipGroupLabel.getCheckedChipId();
        if (checkedId == R.id.chipOffice) label = "Văn phòng";
        else if (checkedId == R.id.chipOther) label = "Khác";

        Address addr = editingAddress != null ? editingAddress : new Address();
        addr.setFullName(name);
        addr.setPhone(phone);
        addr.setAddress(detail);
        addr.setLabel(label);
        addr.setDefault(switchDefault.isChecked());

        btnSave.setEnabled(false);
        new FirebaseAddressHelper().saveAddress(addr)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getText(TextInputEditText edt) {
        return edt.getText() != null ? edt.getText().toString().trim() : "";
    }
}