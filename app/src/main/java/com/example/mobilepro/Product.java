package com.example.mobilepro;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private int price;
    private String description;
    private String category;
    private int imageResId;
    private int quantity;
    private String imageUrl;

    private String documentId;

    public Product() {
    }

    public Product(String name, int price, String description, String category, int imageResId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.imageResId = imageResId;
        this.quantity = 1;
    }

    public Product(String name, int price, String description, int imageResId) {
        this(name, price, description, "Khác", imageResId);
    }

    public Product(String name, int price, String description, int imageResId, int quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = "Khác";
        this.imageResId = imageResId;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public static String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}