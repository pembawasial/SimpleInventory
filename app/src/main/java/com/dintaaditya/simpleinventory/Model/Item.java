package com.dintaaditya.simpleinventory.Model;

public class Item {
    private String name, image;
    private int stock;

    public Item() {
    }

    public Item(String name, String image, int stock) {
        this.name = name;
        this.image = image;
        this.stock = stock;
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
