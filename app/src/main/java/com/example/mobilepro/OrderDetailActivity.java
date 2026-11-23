package com.example.mobilepro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private Order order;
    private TextView tvStatus;
    private MaterialButton btnCancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            finish();
            return;
        }

        TextView tvCode = findViewById(R.id.tvDetailOrderCode);
        tvStatus = findViewById(R.id.tvDetailStatus);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvReceiver = findViewById(R.id.tvDetailReceiver);
        TextView tvAddress = findViewById(R.id.tvDetailAddress);
        TextView tvNote = findViewById(R.id.tvDetailNote);
        TextView tvPayment = findViewById(R.id.tvDetailPayment);
        LinearLayout layoutItems = findViewById(R.id.layoutDetailItems);
        TextView tvSubtotal = findViewById(R.id.tvDetailSubtotal);
        TextView tvShipping = findViewById(R.id.tvDetailShipping);
        TextView tvTotal = findViewById(R.id.tvDetailTotal);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        tvCode.setText("Mã đơn: " + order.orderCode);
        tvDate.setText(order.createdAt != null ? "Ngày đặt: " + dateFormat.format(order.createdAt) : "");
        tvReceiver.setText(order.fullName + " • " + order.phone);
        tvAddress.setText("Địa chỉ: " + order.address);
        tvPayment.setText("Thanh toán: " + order.paymentMethod);

        if (order.note != null && !order.note.isEmpty()) {
            tvNote.setVisibility(View.VISIBLE);
            tvNote.setText("Ghi chú: " + order.note);
        }

        tvSubtotal.setText(Product.formatPrice(order.subtotal));
        tvShipping.setText(Product.formatPrice(order.shippingFee));
        tvTotal.setText(Product.formatPrice(order.total));

        updateStatusUI();

        if (order.items != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            for (Product p : order.items) {
                View row = inflater.inflate(R.layout.item_checkout_summary, layoutItems, false);
                TextView tvName = row.findViewById(R.id.tvItemName);
                TextView tvQty = row.findViewById(R.id.tvItemQty);
                TextView tvPrice = row.findViewById(R.id.tvItemPrice);

                tvName.setText(p.getName());
                tvQty.setText("x" + p.getQuantity());
                tvPrice.setText(Product.formatPrice(p.getPrice() * p.getQuantity()));
                layoutItems.addView(row);
            }
        }

        btnCancelOrder.setOnClickListener(v -> confirmCancelOrder());
    }

    private void updateStatusUI() {
        Order.bindStatusBadge(tvStatus, order.status);
        btnCancelOrder.setVisibility(Order.STATUS_PENDING.equals(order.status) ? View.VISIBLE : View.GONE);
    }

    private void confirmCancelOrder() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng " + order.orderCode + " không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> cancelOrderOnFirestore())
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelOrderOnFirestore() {
        if (order.documentId == null) return;
        btnCancelOrder.setEnabled(false);

        FirebaseFirestore.getInstance().collection("orders")
                .document(order.documentId)
                .update("status", Order.STATUS_CANCELLED)
                .addOnSuccessListener(unused -> {
                    order.status = Order.STATUS_CANCELLED;
                    updateStatusUI();
                    Toast.makeText(this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    btnCancelOrder.setEnabled(true);
                    Toast.makeText(this, "Lỗi hủy đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}