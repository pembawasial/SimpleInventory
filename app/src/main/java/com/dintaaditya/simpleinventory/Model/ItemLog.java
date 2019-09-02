package com.dintaaditya.simpleinventory.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class ItemLog {
    @ServerTimestamp
    public Timestamp timestamp;
    private int previous_stock, stock_movement, last_stock;
    private String status;

    public ItemLog() {
    }

    public ItemLog(int previous_stock, int stock_movement, int last_stock, String status) {
        this.previous_stock = previous_stock;
        this.stock_movement = stock_movement;
        this.last_stock = last_stock;
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrevious_stock() {
        return previous_stock;
    }

    public void setPrevious_stock(int previous_stock) {
        this.previous_stock = previous_stock;
    }

    public int getStock_movement() {
        return stock_movement;
    }

    public void setStock_movement(int stock_movement) {
        this.stock_movement = stock_movement;
    }

    public int getLast_stock() {
        return last_stock;
    }

    public void setLast_stock(int last_stock) {
        this.last_stock = last_stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
