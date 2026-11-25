package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnFavoriteChangedListener {
        void onFavoriteChanged(Product product, boolean isFavorite);
    }

    private final Context context;
    private final List<Product> productList;
    private final OnProductClickListener listener;
    private final FirebaseFavoriteHelper favHelper = new FirebaseFavoriteHelper();
    private final FirebaseCartHelper cartHelper = new FirebaseCartHelper();
    private Set<String> favoriteIds = new HashSet<>();
    private OnFavoriteChangedListener favoriteChangedListener;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void setFavoriteIds(Set<String> favoriteIds) {
        this.favoriteIds = favoriteIds != null ? favoriteIds : new HashSet<>();
        notifyDataSetChanged();
    }

    public void setOnFavoriteChangedListener(OnFavoriteChangedListener favoriteChangedListener) {
        this.favoriteChangedListener = favoriteChangedListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String key = FirebaseFavoriteHelper.getProductKey(product);

        ImageLoader.loadProduct(holder.imgProduct, product);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(Product.formatPrice(product.getPrice()));

        boolean isFav = favoriteIds.contains(key);
        holder.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.btnFavorite.setOnClickListener(v -> {
            holder.btnFavorite.setEnabled(false);
            favHelper.toggleFavorite(product).addOnSuccessListener(nowFav -> {
                holder.btnFavorite.setEnabled(true);
                if (nowFav) {
                    favoriteIds.add(key);
                    Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteIds.remove(key);
                    Toast.makeText(context, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                }
                holder.btnFavorite.setImageResource(nowFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                if (favoriteChangedListener != null) {
                    favoriteChangedListener.onFavoriteChanged(product, nowFav);
                }
            }).addOnFailureListener(e -> holder.btnFavorite.setEnabled(true));
        });

        holder.btnQuickAddCart.setOnClickListener(v -> {
            holder.btnQuickAddCart.setEnabled(false);
            cartHelper.addToCart(product, 1)
                    .addOnSuccessListener(unused -> {
                        holder.btnQuickAddCart.setEnabled(true);
                        Toast.makeText(context, "Đã thêm " + product.getName() + " vào giỏ", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        holder.btnQuickAddCart.setEnabled(true);
                        Toast.makeText(context, "Lỗi thêm giỏ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        ImageButton btnFavorite;
        TextView tvName, tvPrice;
        MaterialButton btnQuickAddCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnQuickAddCart = itemView.findViewById(R.id.btnQuickAddCart);
        }
    }
}