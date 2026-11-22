package com.example.mobilepro;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private int price;
    private String description;
    private int imageResId;
    private int quantity;

    public Product() {
    }

    public Product(String name, int price, String description, int imageResId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
        this.quantity = 1;
    }

    public Product(String name, int price, String description, int imageResId, int quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public static String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}