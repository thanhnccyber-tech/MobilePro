package com.example.mobilepro;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Order {
    public String userId, fullName, phone, address, note, paymentMethod;
    public List<Product> items;
    public int subtotal, shippingFee, total;
    @ServerTimestamp
    public Date createdAt;

    public Order() {}

    public Order(String userId, String fullName, String phone, String address,
                 String note, String paymentMethod, List<Product> items,
                 int subtotal, int shippingFee) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = subtotal + shippingFee;
    }
}