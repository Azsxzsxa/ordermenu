package com.example.ordermenu.Models;

public class MenuItem {
    String cathegory;
    String name;
    int price;
    int quantity = 0;

    public MenuItem(String cathegory, String name, int price) {
        this.cathegory = cathegory;
        this.name = name;
        this.price = price;
    }
    public MenuItem() {
    }

    public String getCathegory() {
        return cathegory;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setCathegory(String cathegory) {
        this.cathegory = cathegory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
