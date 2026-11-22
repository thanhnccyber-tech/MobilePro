package com.example.mobilepro;

import java.io.Serializable;

public class Category implements Serializable {
    private String name;
    private String description;
    private int imageResId;

    public Category() {
    }

    public Category(String name, String description, int imageResId) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}