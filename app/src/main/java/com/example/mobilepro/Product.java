package com.example.mobilepro;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String price;
    private String description;
    private int imageResId;

    public Product(String name, String price, String description, int imageResId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}