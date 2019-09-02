package com.dintaaditya.simpleinventory.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Item {
    @ServerTimestamp
    public Timestamp timestamp;
    private String name, image;
    private int stock;

    public Item() {
    }

    public Item(String name, String image, int stock) {
        this.name = name;
        this.image = image;
        this.stock = stock;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
