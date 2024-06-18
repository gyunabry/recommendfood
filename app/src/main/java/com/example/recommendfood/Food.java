package com.example.recommendfood;

public class Food {
    private String name;
    private String imageUrl;

    public Food(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
