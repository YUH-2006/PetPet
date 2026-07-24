package com.example.petparadise;

public class Product {
    private int id;
    private String name;
    private String category;
    private String price;
    private String image;
    private String description;
    private int quantity;

    public Product(int id, String name, String category, String price, String image, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
}
