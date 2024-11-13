package com.example.shop;

import android.content.Intent;
import com.example.shop.ui.home.ProductAdapter;
public class Product {
    private int id;
    private String name;
    private double price;
    private double salePrice;
    private String thumbnail;

    public Product(int id, String name, double price, double salePrice, String thumbnail) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.salePrice = salePrice;
        this.thumbnail = thumbnail;
    }

    // Getter methods
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getSalePrice() { return salePrice; }
    public String getThumbnail() { return thumbnail; }
}
