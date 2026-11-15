package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private final Context context;
    private final List<Product> productList;
    private final DatabaseHelper dbHelper;
    private final Runnable onCartChanged;

    public CartAdapter(Context context, List<Product> productList,
                       DatabaseHelper dbHelper, Runnable onCartChanged) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = dbHelper;
        this.onCartChanged = onCartChanged;
    }

    @Override
    public int getCount() { return productList.size(); }

    @Override
    public Object getItem(int position) { return productList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_cart, parent, false);
        }

        Product product = productList.get(position);

        ImageView img = convertView.findViewById(R.id.imgCartProduct);
        TextView tvName = convertView.findViewById(R.id.tvCartName);
        TextView tvPrice = convertView.findViewById(R.id.tvCartPrice);
        TextView tvQty = convertView.findViewById(R.id.tvCartQuantity);
        Button btnMinus = convertView.findViewById(R.id.btnMinus);
        Button btnPlus = convertView.findViewById(R.id.btnPlus);
        Button btnDelete = convertView.findViewById(R.id.btnDeleteItem);

        img.setImageResource(product.getImageResId());
        tvName.setText(product.getName());
        tvPrice.setText(product.getPrice());
        tvQty.setText(String.valueOf(product.getQuantity()));

        btnPlus.setOnClickListener(v -> {
            dbHelper.increaseQuantity(product.getName());
            onCartChanged.run();
        });

        btnMinus.setOnClickListener(v -> {
            dbHelper.decreaseQuantity(product.getName());
            onCartChanged.run();
        });

        btnDelete.setOnClickListener(v -> {
            dbHelper.removeFromCart(product.getName());
            onCartChanged.run();
        });

        return convertView;
    }
}