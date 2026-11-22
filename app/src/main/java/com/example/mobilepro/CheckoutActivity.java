package com.example.mobilepro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private static final int SHIPPING_FEE = 20000;

    private final List<Product> cartProducts = new ArrayList<>();
    private FirebaseCartHelper cartHelper;

    private TextInputLayout tilFullName, tilPhone, tilAddress;
    private TextInputEditText edtFullName, edtPhone, edtAddress, edtNote;
    private LinearLayout layoutOrderItems;
    private RadioGroup rgPayment;
    private TextView tvSubtotal, tvShipping, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartHelper = new FirebaseCartHelper();

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtNote = findViewById(R.id.edtNote);
        layoutOrderItems = findViewById(R.id.layoutOrderItems);
        rgPayment = findViewById(R.id.rgPayment);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvTotal = findViewById(R.id.tvTotal);

        MaterialButton btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());

        loadOrderSummary();
    }

    private void loadOrderSummary() {
        cartHelper.getCart().addOnSuccessListener(snap -> {
            cartProducts.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Product product = doc.toObject(Product.class);
                if (product != null) {
                    cartProducts.add(product);
                }
            }
            renderOrderItems();
            updateTotals();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi tải giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void renderOrderItems() {
        layoutOrderItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Product p : cartProducts) {
            View row = inflater.inflate(R.layout.item_checkout_summary, layoutOrderItems, false);
            TextView tvName = row.findViewById(R.id.tvItemName);
            TextView tvQty = row.findViewById(R.id.tvItemQty);
            TextView tvPrice = row.findViewById(R.id.tvItemPrice);

            int lineTotal = parsePrice(p.getPrice()) * p.getQuantity();
            tvName.setText(p.getName());
            tvQty.setText("x" + p.getQuantity());
            tvPrice.setText(formatPrice(lineTotal));
            layoutOrderItems.addView(row);
        }
    }

    private void updateTotals() {
        int subtotal = getSubtotal();
        int shipping = subtotal > 0 ? SHIPPING_FEE : 0;
        tvSubtotal.setText(formatPrice(subtotal));
        tvShipping.setText(formatPrice(shipping));
        tvTotal.setText(formatPrice(subtotal + shipping));
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

        Order order = new Order(uid, fullName, phone, address, note,
                paymentMethod, cartProducts, getSubtotal(), SHIPPING_FEE);

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
            subtotal += parsePrice(p.getPrice()) * p.getQuantity();
        }
        return subtotal;
    }

    private int parsePrice(String price) {
        try {
            return Integer.parseInt(price.replace("đ", "").replace(".", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}