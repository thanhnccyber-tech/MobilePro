package com.example.mobilepro;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

public class Voucher implements Serializable {
    private String code;
    private String title;
    private String discountType;
    private int discountValue;
    private int maxDiscount;
    private int minOrderValue;
    private boolean isActive;

    public Voucher() {
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public int getDiscountValue() { return discountValue; }
    public void setDiscountValue(int discountValue) { this.discountValue = discountValue; }

    public int getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(int maxDiscount) { this.maxDiscount = maxDiscount; }

    public int getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(int minOrderValue) { this.minOrderValue = minOrderValue; }

    @PropertyName("isActive")
    public boolean isActive() { return isActive; }

    @PropertyName("isActive")
    public void setActive(boolean active) { isActive = active; }

    public int calculateDiscount(int subtotal, int shippingFee) {
        if (subtotal < minOrderValue) return 0;

        if ("FREESHIP".equalsIgnoreCase(discountType)) {
            return shippingFee;
        }
        if ("PERCENT".equalsIgnoreCase(discountType)) {
            int discount = (subtotal * discountValue) / 100;
            return maxDiscount > 0 ? Math.min(discount, maxDiscount) : discount;
        }
        return Math.min(discountValue, subtotal);
    }
}