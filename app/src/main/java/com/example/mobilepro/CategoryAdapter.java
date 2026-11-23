package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private final Context context;
    private final List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() { return categoryList.size(); }

    @Override
    public Object getItem(int position) { return categoryList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        Category category = categoryList.get(position);

        ImageView imgCategory = convertView.findViewById(R.id.imgCategory);
        TextView tvName = convertView.findViewById(R.id.tvCategoryName);

        ImageLoader.loadCategory(imgCategory, category);
        tvName.setText(category.getName());

        return convertView;
    }
}