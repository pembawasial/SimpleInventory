package com.dintaaditya.simpleinventory.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Shipment {
    @ServerTimestamp
    public Timestamp timestamp;
    private String SKU, address, receiver_name;
    private int quantity;

    public Shipment() {
    }

    public Shipment(String SKU, String address, String receiver_name, int quantity) {
        this.SKU = SKU;
        this.address = address;
        this.receiver_name = receiver_name;
        this.quantity = quantity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
