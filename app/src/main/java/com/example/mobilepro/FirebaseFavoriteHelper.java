package com.example.mobilepro;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.Set;

public class FirebaseFavoriteHelper {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference favRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.collection("users").document(uid).collection("favorites");
    }

    public static String getProductKey(Product product) {
        if (product.getDocumentId() != null && !product.getDocumentId().isEmpty()) {
            return product.getDocumentId();
        }
        return product.getName().replace("/", "_");
    }

    public Task<QuerySnapshot> getFavorites() {
        return favRef().get();
    }

    public Task<Set<String>> getFavoriteIds() {
        return favRef().get().continueWith(task -> {
            Set<String> ids = new HashSet<>();
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult()) {
                    ids.add(doc.getId());
                }
            }
            return ids;
        });
    }

    public Task<Boolean> isFavorite(Product product) {
        return favRef().document(getProductKey(product)).get().continueWith(task ->
                task.isSuccessful() && task.getResult() != null && task.getResult().exists()
        );
    }

    public Task<Boolean> toggleFavorite(Product product) {
        DocumentReference docRef = favRef().document(getProductKey(product));
        return docRef.get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            if (task.getResult().exists()) {
                return docRef.delete().continueWith(t -> false);
            } else {
                return docRef.set(product).continueWith(t -> true);
            }
        });
    }
}