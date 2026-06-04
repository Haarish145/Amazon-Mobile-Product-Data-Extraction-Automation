package com.amazon.automation.model;

public class ProductData {
    private final String productName;
    private final String price;
    private final String rating;
    private final String reviewsCount;
    private final String primeBadge;

    public ProductData(String productName, String price, String rating, String reviewsCount, String primeBadge) {
        this.productName = productName;
        this.price = price;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
        this.primeBadge = primeBadge;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public String getReviewsCount() {
        return reviewsCount;
    }

    public String getPrimeBadge() {
        return primeBadge;
    }
}
