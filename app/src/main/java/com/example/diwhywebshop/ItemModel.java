package com.example.diwhywebshop;

public class ItemModel {
    private String name;
    private String description;
    private String image;
    private Integer price;
    private String id;

    public ItemModel() {}

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public ItemModel(String name, String description, String image, Integer price, String id) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
