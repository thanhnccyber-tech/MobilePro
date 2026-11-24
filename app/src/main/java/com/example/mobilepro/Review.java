package com.example.mobilepro;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Review {
    @Exclude
    public String documentId;

    public String userId;
    public String userName;
    public String userAvatar;
    public float rating;
    public String comment;

    @ServerTimestamp
    public Date createdAt;

    public Review() {
    }

    public Review(String userId, String userName, String userAvatar, float rating, String comment) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.comment = comment;
    }
}