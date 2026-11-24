package com.example.mobilepro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private static final int MAX_VISIBLE_REVIEWS = 3;

    private int quantity = 1;
    private Product product;
    private boolean isReviewsExpanded = false;
    private final List<Review> allReviews = new ArrayList<>();
    private float totalRatingSum = 0;

    private TextView tvQuantity, tvRatingSummary, tvEmptyReviews, tvReviewsHeader, tvToggleReviews;
    private MaterialButton btnAddToCart, btnSubmitReview;
    private RatingBar ratingBarInput;
    private TextInputEditText edtComment;
    private LinearLayout layoutReviews;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        MaterialButton btnMinus = findViewById(R.id.btnMinus);
        MaterialButton btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);

        tvRatingSummary = findViewById(R.id.tvRatingSummary);
        tvReviewsHeader = findViewById(R.id.tvReviewsHeader);
        tvEmptyReviews = findViewById(R.id.tvEmptyReviews);
        tvToggleReviews = findViewById(R.id.tvToggleReviews);
        ratingBarInput = findViewById(R.id.ratingBarInput);
        edtComment = findViewById(R.id.edtComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        layoutReviews = findViewById(R.id.layoutReviews);

        topAppBar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.btnCart).setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
            finish();
        });

        tvToggleReviews.setOnClickListener(v -> {
            isReviewsExpanded = !isReviewsExpanded;
            renderReviews();
        });

        product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            ImageLoader.loadProduct(imgDetail, product);
            tvName.setText(product.getName());
            tvPrice.setText("Giá: " + Product.formatPrice(product.getPrice()));
            tvDescription.setText(product.getDescription());
            updateAddToCartButton();

            ImageButton btnDetailFavorite = findViewById(R.id.btnDetailFavorite);
            FirebaseFavoriteHelper favHelper = new FirebaseFavoriteHelper();

            favHelper.isFavorite(product).addOnSuccessListener(isFav ->
                    updateDetailFavoriteIcon(btnDetailFavorite, isFav));

            btnDetailFavorite.setOnClickListener(v -> {
                btnDetailFavorite.setEnabled(false);
                favHelper.toggleFavorite(product).addOnSuccessListener(nowFav -> {
                    btnDetailFavorite.setEnabled(true);
                    updateDetailFavoriteIcon(btnDetailFavorite, nowFav);
                    Toast.makeText(this, nowFav ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> btnDetailFavorite.setEnabled(true));
            });

            btnAddToCart.setOnClickListener(v ->
                    new FirebaseCartHelper().addToCart(product, quantity)
                            .addOnSuccessListener(unused -> {
                                startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Lỗi thêm giỏ: " + e.getMessage(), Toast.LENGTH_SHORT).show()));

            btnSubmitReview.setOnClickListener(v -> submitReview());
            loadReviews();
        }

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateAddToCartButton();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateAddToCartButton();
        });
    }

    private void updateDetailFavoriteIcon(ImageButton btn, boolean isFav) {
        if (isFav) {
            btn.setImageResource(R.drawable.ic_heart_filled);
            btn.clearColorFilter();
        } else {
            btn.setImageResource(R.drawable.ic_heart_outline);
            btn.setColorFilter(Color.WHITE);
        }
    }

    private void updateAddToCartButton() {
        if (product != null) {
            int total = product.getPrice() * quantity;
            btnAddToCart.setText("Thêm • " + Product.formatPrice(total));
        }
    }

    private CollectionReference reviewsRef() {
        String docId = (product.getDocumentId() != null && !product.getDocumentId().isEmpty())
                ? product.getDocumentId()
                : product.getName().replace("/", "_");
        return FirebaseFirestore.getInstance()
                .collection("products")
                .document(docId)
                .collection("reviews");
    }

    private void submitReview() {
        String comment = edtComment.getText() != null ? edtComment.getText().toString().trim() : "";
        float rating = ratingBarInput.getRating();

        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung nhận xét", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("username", "Khách hàng");
        String userAvatar = prefs.getString("avatarUrl", "");

        btnSubmitReview.setEnabled(false);
        Review review = new Review(uid, userName, userAvatar, rating, comment);

        reviewsRef().add(review)
                .addOnSuccessListener(ref -> {
                    btnSubmitReview.setEnabled(true);
                    edtComment.setText("");
                    ratingBarInput.setRating(5);
                    Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                    loadReviews();
                })
                .addOnFailureListener(e -> {
                    btnSubmitReview.setEnabled(true);
                    Toast.makeText(this, "Lỗi gửi đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadReviews() {
        reviewsRef().get().addOnSuccessListener(snap -> {
            allReviews.clear();
            totalRatingSum = 0;

            for (DocumentSnapshot doc : snap) {
                Review review = doc.toObject(Review.class);
                if (review != null) {
                    review.documentId = doc.getId();
                    allReviews.add(review);
                    totalRatingSum += review.rating;
                }
            }

            allReviews.sort(Comparator.comparing(r -> r.createdAt, Comparator.nullsLast(Comparator.reverseOrder())));
            renderReviews();
        });
    }

    private void renderReviews() {
        layoutReviews.removeAllViews();

        if (allReviews.isEmpty()) {
            tvRatingSummary.setText("Chưa có đánh giá");
            tvReviewsHeader.setText("Nhận xét từ khách hàng");
            tvEmptyReviews.setVisibility(View.VISIBLE);
            tvToggleReviews.setVisibility(View.GONE);
            return;
        }

        float avg = totalRatingSum / allReviews.size();
        tvRatingSummary.setText(String.format(Locale.getDefault(), "%.1f ★ (%d)", avg, allReviews.size()));
        tvReviewsHeader.setText("Nhận xét từ khách hàng (" + allReviews.size() + ")");
        tvEmptyReviews.setVisibility(View.GONE);

        int limit = isReviewsExpanded ? allReviews.size() : Math.min(MAX_VISIBLE_REVIEWS, allReviews.size());
        String currentUid = FirebaseAuth.getInstance().getUid();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < limit; i++) {
            Review r = allReviews.get(i);
            View row = inflater.inflate(R.layout.item_review, layoutReviews, false);

            ImageView imgAvatar = row.findViewById(R.id.imgReviewAvatar);
            TextView tvUserName = row.findViewById(R.id.tvReviewUserName);
            TextView tvRating = row.findViewById(R.id.tvReviewRating);
            TextView tvDate = row.findViewById(R.id.tvReviewDate);
            TextView tvComment = row.findViewById(R.id.tvReviewComment);
            TextView tvDelete = row.findViewById(R.id.tvDeleteReview);

            ImageLoader.loadAvatar(imgAvatar, r.userAvatar);
            tvUserName.setText(r.userName);
            tvRating.setText(String.format(Locale.getDefault(), "%.0f ★", r.rating));
            tvDate.setText(r.createdAt != null ? dateFormat.format(r.createdAt) : "");
            tvComment.setText(r.comment);

            if (currentUid != null && currentUid.equals(r.userId)) {
                tvDelete.setVisibility(View.VISIBLE);
                tvDelete.setOnClickListener(v ->
                        reviewsRef().document(r.documentId).delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();
                                    loadReviews();
                                }));
            }

            layoutReviews.addView(row);
        }

        if (allReviews.size() > MAX_VISIBLE_REVIEWS) {
            tvToggleReviews.setVisibility(View.VISIBLE);
            int hiddenCount = allReviews.size() - MAX_VISIBLE_REVIEWS;
            tvToggleReviews.setText(isReviewsExpanded
                    ? "Thu gọn bình luận ▲"
                    : "Xem thêm " + hiddenCount + " bình luận khác ▼");
        } else {
            tvToggleReviews.setVisibility(View.GONE);
        }
    }
}