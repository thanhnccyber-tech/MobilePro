package com.example.mobilepro;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Address implements Serializable {

    @Exclude
    private String documentId;

    private String fullName;
    private String phone;
    private String address;
    private String label;
    private boolean isDefault;

    public Address() {
    }

    public Address(String fullName, String phone, String address, String label, boolean isDefault) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.label = label;
        this.isDefault = isDefault;
    }

    @Exclude
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @PropertyName("isDefault")
    public boolean isDefault() { return isDefault; }

    @PropertyName("isDefault")
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}