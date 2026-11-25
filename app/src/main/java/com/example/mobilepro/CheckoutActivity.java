package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private static final int SHIPPING_FEE = 20000;

    private final List<Product> cartProducts = new ArrayList<>();
    private FirebaseCartHelper cartHelper;
    private FirebaseAddressHelper addressHelper;

    private TextInputLayout tilFullName, tilPhone, tilAddress, tilVoucherCode;
    private TextInputEditText edtFullName, edtPhone, edtAddress, edtNote, edtVoucherCode;
    private CheckBox cbSaveAddress;
    private LinearLayout layoutOrderItems, layoutAppliedVoucher, layoutCheckoutDiscount;
    private RadioGroup rgPayment;
    private TextView tvSubtotal, tvShipping, tvDiscount, tvTotal, tvAppliedVoucherInfo;

    private Voucher appliedVoucher = null;
    private int discountAmount = 0;

    private final ActivityResultLauncher<Intent> selectAddressLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Address selected = (Address) result.getData().getSerializableExtra("selectedAddress");
                    if (selected != null) {
                        fillAddressFields(selected);
                        cbSaveAddress.setVisibility(View.GONE);
                        cbSaveAddress.setChecked(false);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartHelper = new FirebaseCartHelper();
        addressHelper = new FirebaseAddressHelper();

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtNote = findViewById(R.id.edtNote);
        cbSaveAddress = findViewById(R.id.cbSaveAddress);
        layoutOrderItems = findViewById(R.id.layoutOrderItems);
        rgPayment = findViewById(R.id.rgPayment);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvTotal = findViewById(R.id.tvTotal);

        tilVoucherCode = findViewById(R.id.tilVoucherCode);
        edtVoucherCode = findViewById(R.id.edtVoucherCode);
        layoutAppliedVoucher = findViewById(R.id.layoutAppliedVoucher);
        layoutCheckoutDiscount = findViewById(R.id.layoutCheckoutDiscount);
        tvAppliedVoucherInfo = findViewById(R.id.tvAppliedVoucherInfo);
        tvDiscount = findViewById(R.id.tvDiscount);

        findViewById(R.id.tvChooseAddress).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressListActivity.class);
            intent.putExtra("selectMode", true);
            selectAddressLauncher.launch(intent);
        });

        findViewById(R.id.btnApplyVoucher).setOnClickListener(v -> applyManualVoucher());
        findViewById(R.id.tvChooseVoucher).setOnClickListener(v -> showVoucherDialog());
        findViewById(R.id.tvRemoveVoucher).setOnClickListener(v -> removeVoucher());

        MaterialButton btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());

        ArrayList<Product> list = (ArrayList<Product>) getIntent().getSerializableExtra("cartProducts");
        if (list != null) {
            cartProducts.addAll(list);
        }
        renderOrderItems();
        updateTotals();
        loadDefaultAddress();
    }

    private void applyManualVoucher() {
        tilVoucherCode.setError(null);
        String code = edtVoucherCode.getText() != null
                ? edtVoucherCode.getText().toString().trim().toUpperCase() : "";

        if (code.isEmpty()) {
            tilVoucherCode.setError("Vui lòng nhập mã giảm giá");
            return;
        }

        FirebaseFirestore.getInstance().collection("vouchers")
                .whereEqualTo("code", code)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) {
                        tilVoucherCode.setError("Mã giảm giá không tồn tại");
                        return;
                    }
                    Voucher voucher = snap.getDocuments().get(0).toObject(Voucher.class);
                    if (voucher == null || !voucher.isActive()) {
                        tilVoucherCode.setError("Mã giảm giá đã hết hạn");
                        return;
                    }
                    applyVoucher(voucher);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi kiểm tra mã: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showVoucherDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_voucher_list, null);
        dialog.setContentView(view);

        RecyclerView recyclerVouchers = view.findViewById(R.id.recyclerVouchers);
        TextView tvEmpty = view.findViewById(R.id.tvEmptyVouchers);
        view.findViewById(R.id.btnCloseVoucherDialog).setOnClickListener(v -> dialog.dismiss());

        recyclerVouchers.setLayoutManager(new LinearLayoutManager(this));
        List<Voucher> voucherList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("vouchers")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(snap -> {
                    for (DocumentSnapshot doc : snap) {
                        Voucher v = doc.toObject(Voucher.class);
                        if (v != null) voucherList.add(v);
                    }
                    tvEmpty.setVisibility(voucherList.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerVouchers.setAdapter(new VoucherAdapter(this, voucherList, getSubtotal(), selected -> {
                        applyVoucher(selected);
                        dialog.dismiss();
                    }));
                });

        dialog.show();
    }

    private void applyVoucher(Voucher voucher) {
        int subtotal = getSubtotal();
        if (subtotal < voucher.getMinOrderValue()) {
            tilVoucherCode.setError("Đơn tối thiểu " + Product.formatPrice(voucher.getMinOrderValue()));
            return;
        }

        int shipping = subtotal > 0 ? SHIPPING_FEE : 0;
        appliedVoucher = voucher;
        discountAmount = voucher.calculateDiscount(subtotal, shipping);

        tilVoucherCode.setError(null);
        edtVoucherCode.setText(voucher.getCode());
        layoutAppliedVoucher.setVisibility(View.VISIBLE);
        tvAppliedVoucherInfo.setText("Đã dùng mã " + voucher.getCode() + " (-" + Product.formatPrice(discountAmount) + ")");
        updateTotals();
    }

    private void removeVoucher() {
        appliedVoucher = null;
        discountAmount = 0;
        edtVoucherCode.setText("");
        layoutAppliedVoucher.setVisibility(View.GONE);
        updateTotals();
    }

    private void loadDefaultAddress() {
        addressHelper.getDefaultAddress().addOnSuccessListener(defaultAddr -> {
            if (defaultAddr != null) {
                fillAddressFields(defaultAddr);
            } else {
                cbSaveAddress.setVisibility(View.VISIBLE);
                cbSaveAddress.setChecked(true);
            }
        });
    }

    private void fillAddressFields(Address addr) {
        edtFullName.setText(addr.getFullName());
        edtPhone.setText(addr.getPhone());
        edtAddress.setText(addr.getAddress());
    }

    private void renderOrderItems() {
        layoutOrderItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Product p : cartProducts) {
            View row = inflater.inflate(R.layout.item_checkout_summary, layoutOrderItems, false);
            TextView tvName = row.findViewById(R.id.tvItemName);
            TextView tvQty = row.findViewById(R.id.tvItemQty);
            TextView tvPrice = row.findViewById(R.id.tvItemPrice);

            int lineTotal = p.getPrice() * p.getQuantity();
            tvName.setText(p.getName());
            tvQty.setText("x" + p.getQuantity());
            tvPrice.setText(Product.formatPrice(lineTotal));
            layoutOrderItems.addView(row);
        }
    }

    private void updateTotals() {
        int subtotal = getSubtotal();
        int shipping = subtotal > 0 ? SHIPPING_FEE : 0;
        int total = Math.max(0, subtotal + shipping - discountAmount);

        tvSubtotal.setText(Product.formatPrice(subtotal));
        tvShipping.setText(Product.formatPrice(shipping));
        tvTotal.setText(Product.formatPrice(total));

        if (discountAmount > 0) {
            layoutCheckoutDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-" + Product.formatPrice(discountAmount));
        } else {
            layoutCheckoutDiscount.setVisibility(View.GONE);
        }
    }

    private void handlePlaceOrder() {
        tilFullName.setError(null);
        tilPhone.setError(null);
        tilAddress.setError(null);

        String fullName = edtFullName.getText() != null ? edtFullName.getText().toString().trim() : "";
        String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
        String address = edtAddress.getText() != null ? edtAddress.getText().toString().trim() : "";
        String note = edtNote.getText() != null ? edtNote.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            tilFullName.setError("Vui lòng nhập họ và tên");
            return;
        }
        if (phone.isEmpty()) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (address.isEmpty()) {
            tilAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            return;
        }
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cbSaveAddress.getVisibility() == View.VISIBLE && cbSaveAddress.isChecked()) {
            addressHelper.saveAddress(new Address(fullName, phone, address, "Nhà riêng", true));
        }

        submitOrder(fullName, phone, address, note, getPaymentMethod());
    }

    private String getPaymentMethod() {
        int checked = rgPayment.getCheckedRadioButtonId();
        if (checked == R.id.rbBank) return "Chuyển khoản ngân hàng";
        if (checked == R.id.rbEwallet) return "Ví điện tử";
        return "Tiền mặt khi nhận hàng";
    }

    private void submitOrder(String fullName, String phone, String address,
                             String note, String paymentMethod) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        String voucherCode = appliedVoucher != null ? appliedVoucher.getCode() : "";

        Order order = new Order(uid, fullName, phone, address, note,
                paymentMethod, cartProducts, getSubtotal(), SHIPPING_FEE, voucherCode, discountAmount);

        FirebaseFirestore.getInstance().collection("orders").add(order)
                .addOnSuccessListener(ref -> cartHelper.clearCart().addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    finish();
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Đặt hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private int getSubtotal() {
        int subtotal = 0;
        for (Product p : cartProducts) {
            subtotal += p.getPrice() * p.getQuantity();
        }
        return subtotal;
    }
}