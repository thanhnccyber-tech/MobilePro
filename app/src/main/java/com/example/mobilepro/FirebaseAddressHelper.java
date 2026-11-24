package com.example.mobilepro;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAddressHelper {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference addressRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.collection("users").document(uid).collection("addresses");
    }

    public Task<List<Address>> getAddresses() {
        return addressRef().get().continueWith(task -> {
            List<Address> list = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Address addr = doc.toObject(Address.class);
                    if (addr != null) {
                        addr.setDocumentId(doc.getId());
                        list.add(addr);
                    }
                }
                list.sort((a, b) -> Boolean.compare(b.isDefault(), a.isDefault()));
            }
            return list;
        });
    }

    public Task<Address> getDefaultAddress() {
        return getAddresses().continueWith(task -> {
            List<Address> list = task.getResult();
            if (list == null || list.isEmpty()) return null;
            for (Address addr : list) {
                if (addr.isDefault()) return addr;
            }
            return list.get(0);
        });
    }

    public Task<Void> saveAddress(Address address) {
        return addressRef().get().continueWithTask(task -> {
            if (!task.isSuccessful()) throw task.getException();

            QuerySnapshot snap = task.getResult();
            if (snap.isEmpty()) {
                address.setDefault(true);
            }

            WriteBatch batch = db.batch();
            if (address.isDefault()) {
                for (DocumentSnapshot doc : snap) {
                    if (address.getDocumentId() == null || !doc.getId().equals(address.getDocumentId())) {
                        batch.update(doc.getReference(), "isDefault", false);
                    }
                }
            }

            DocumentReference targetDoc = (address.getDocumentId() != null && !address.getDocumentId().isEmpty())
                    ? addressRef().document(address.getDocumentId())
                    : addressRef().document();

            batch.set(targetDoc, address);
            return batch.commit();
        });
    }

    public Task<Void> setDefaultAddress(String documentId) {
        return addressRef().get().continueWithTask(task -> {
            if (!task.isSuccessful()) throw task.getException();
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : task.getResult()) {
                batch.update(doc.getReference(), "isDefault", doc.getId().equals(documentId));
            }
            return batch.commit();
        });
    }

    public Task<Void> deleteAddress(String documentId) {
        return addressRef().document(documentId).delete();
    }
}