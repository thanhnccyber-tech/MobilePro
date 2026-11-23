package com.example.mobilepro;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {

    public static void load(ImageView imageView, String imageUrl, int fallbackResId, int defaultResId) {
        int errorImage = fallbackResId != 0 ? fallbackResId : defaultResId;

        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.banhkem)
                .error(errorImage)
                .into(imageView);
    }

    // Hàm rút gọn riêng cho Product
    public static void loadProduct(ImageView imageView, Product product) {
        load(imageView, product.getImageUrl(), product.getImageResId(), R.drawable.banhkem);
    }

    // Hàm rút gọn riêng cho Category
    public static void loadCategory(ImageView imageView, Category category) {
        load(imageView, category.getImageUrl(), 0, R.drawable.banhkem);
    }
}