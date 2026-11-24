package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private final List<Address> addressList = new ArrayList<>();
    private AddressAdapter adapter;
    private FirebaseAddressHelper addressHelper;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private boolean selectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        addressHelper = new FirebaseAddressHelper();
        selectMode = getIntent().getBooleanExtra("selectMode", false);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitle(selectMode ? "Chọn địa chỉ nhận hàng" : "Sổ địa chỉ");
        topAppBar.setNavigationOnClickListener(v -> finish());

        tvEmpty = findViewById(R.id.tvEmptyAddresses);
        recyclerView = findViewById(R.id.recyclerAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AddressAdapter(this, addressList, new AddressAdapter.OnAddressActionListener() {
            @Override
            public void onSelect(Address address) {
                if (selectMode) {
                    Intent result = new Intent();
                    result.putExtra("selectedAddress", address);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    openEditAddress(address);
                }
            }

            @Override
            public void onEdit(Address address) {
                openEditAddress(address);
            }

            @Override
            public void onSetDefault(Address address) {
                addressHelper.setDefaultAddress(address.getDocumentId())
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(AddressListActivity.this, "Đã đặt làm mặc định", Toast.LENGTH_SHORT).show();
                            loadAddresses();
                        });
            }

            @Override
            public void onDelete(Address address) {
                new AlertDialog.Builder(AddressListActivity.this)
                        .setTitle("Xóa địa chỉ")
                        .setMessage("Bạn muốn xóa địa chỉ này khỏi sổ địa chỉ?")
                        .setPositiveButton("Xóa", (d, w) ->
                                addressHelper.deleteAddress(address.getDocumentId())
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(AddressListActivity.this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                                            loadAddresses();
                                        }))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnAddNewAddress).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditAddressActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void openEditAddress(Address address) {
        Intent intent = new Intent(this, AddEditAddressActivity.class);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    private void loadAddresses() {
        addressHelper.getAddresses().addOnSuccessListener(list -> {
            addressList.clear();
            addressList.addAll(list);
            adapter.notifyDataSetChanged();

            boolean isEmpty = addressList.isEmpty();
            tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi tải địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}