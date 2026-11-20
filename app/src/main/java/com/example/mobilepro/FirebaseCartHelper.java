package com.example.mobilepro;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class FirebaseCartHelper {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference cartRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.collection("users").document(uid).collection("cart");
    }

    private DocumentReference cartDoc(String productName) {
        return cartRef().document(productName.replace("/", "_"));
    }

    public Task<QuerySnapshot> getCart() {
        return cartRef().get();
    }

    public Task<Void> addToCart(Product product, int quantity) {
        DocumentReference docRef = cartDoc(product.getName());

        return docRef.get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            if (task.getResult().exists()) {
                return docRef.update("quantity", FieldValue.increment(quantity));
            }
            product.setQuantity(quantity);
            return docRef.set(product);
        });
    }

    public Task<Void> increaseQuantity(String productName) {
        return cartDoc(productName).update("quantity", FieldValue.increment(1));
    }

    public Task<Void> decreaseQuantity(String productName) {
        return cartDoc(productName).update("quantity", FieldValue.increment(-1));
    }

    public Task<Void> removeFromCart(String productName) {
        return cartDoc(productName).delete();
    }

    public Task<Void> clearCart() {
        return cartRef().get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                batch.delete(doc.getReference());
            }
            return batch.commit();
        });
    }
}