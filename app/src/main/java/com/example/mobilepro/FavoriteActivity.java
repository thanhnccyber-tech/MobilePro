package com.example.mobilepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteActivity extends AppCompatActivity {

    private final List<Product> favoriteProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private TextView tvEmpty;
    private FirebaseFavoriteHelper favHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favHelper = new FirebaseFavoriteHelper();

        ((MaterialToolbar) findViewById(R.id.topAppBar)).setNavigationOnClickListener(v -> finish());
        tvEmpty = findViewById(R.id.tvEmptyFavorites);

        RecyclerView recyclerView = findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ProductAdapter(this, favoriteProducts, product -> {
            Intent intent = new Intent(FavoriteActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        adapter.setOnFavoriteChangedListener((product, isFavorite) -> {
            if (!isFavorite) {
                favoriteProducts.remove(product);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        favHelper.getFavorites().addOnSuccessListener(snap -> {
            favoriteProducts.clear();
            Set<String> ids = new HashSet<>();

            for (DocumentSnapshot doc : snap) {
                Product product = doc.toObject(Product.class);
                if (product != null) {
                    if (product.getDocumentId() == null || product.getDocumentId().isEmpty()) {
                        product.setDocumentId(doc.getId());
                    }
                    if (product.getImageResId() == 0) {
                        product.setImageResId(R.drawable.banhkem);
                    }
                    favoriteProducts.add(product);
                    ids.add(doc.getId());
                }
            }

            adapter.setFavoriteIds(ids);
            updateEmptyState();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi tải danh sách yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateEmptyState() {
        tvEmpty.setVisibility(favoriteProducts.isEmpty() ? View.VISIBLE : View.GONE);
    }
}