package com.example.mobilepro;

import java.io.Serializable;

public class Category implements Serializable {
    private String name;
    private String imageUrl;

    public Category() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}