package com.example.shop;

public class Slide {
    private int id;
    private String imageUrl;

    public Slide(int id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
