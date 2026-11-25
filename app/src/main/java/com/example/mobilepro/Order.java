package com.example.mobilepro;

import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order implements Serializable {

    public static final String STATUS_ALL = "Tất cả";
    public static final String STATUS_PENDING = "Chờ xác nhận";
    public static final String STATUS_BAKING = "Đang làm bánh";
    public static final String STATUS_SHIPPING = "Đang giao hàng";
    public static final String STATUS_COMPLETED = "Hoàn thành";
    public static final String STATUS_CANCELLED = "Đã hủy";
    public static final String STATUS_REFUND = "Khiếu nại / Hoàn tiền";

    public static final String[] ALL_STATUSES = {
            STATUS_ALL, STATUS_PENDING, STATUS_BAKING, STATUS_SHIPPING,
            STATUS_COMPLETED, STATUS_CANCELLED, STATUS_REFUND
    };

    public static int getStatusColorRes(String status) {
        if (STATUS_PENDING.equals(status)) return R.color.caramel;
        if (STATUS_BAKING.equals(status)) return R.color.blue;
        if (STATUS_SHIPPING.equals(status)) return R.color.brown_soft;
        if (STATUS_COMPLETED.equals(status)) return R.color.green;
        if (STATUS_CANCELLED.equals(status)) return R.color.red;
        if (STATUS_REFUND.equals(status)) return R.color.gray;
        return R.color.brown_light;
    }

    @Exclude
    public String documentId;

    public String userId, fullName, phone, address, note, paymentMethod, orderCode, status, voucherCode;
    public List<Product> items;
    public int subtotal, shippingFee, discountAmount, total;
    @ServerTimestamp
    public Date createdAt;

    public Order() {}

    public Order(String userId, String fullName, String phone, String address,
                 String note, String paymentMethod, List<Product> items,
                 int subtotal, int shippingFee, String voucherCode, int discountAmount) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
        this.total = Math.max(0, subtotal + shippingFee - discountAmount);
        this.orderCode = "BKR_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.status = STATUS_PENDING;
    }

    public static void bindStatusBadge(TextView tv, String status) {
        int color = ContextCompat.getColor(tv.getContext(), getStatusColorRes(status));
        GradientDrawable badge = new GradientDrawable();
        badge.setCornerRadius(tv.getResources().getDisplayMetrics().density * 8);
        badge.setColor(ColorUtils.setAlphaComponent(color, 38));
        tv.setText(status);
        tv.setTextColor(color);
        tv.setBackground(badge);
    }
}