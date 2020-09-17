package com.example.ordermenu.Models;

import java.util.Objects;

public class MenuItem {
    private String document_id;
    private String category;
    private String name;
    private int price;
    private int quantity;
    private Boolean available;

    public MenuItem(String document_id, String category, String name, int price, int quantity, Boolean available) {
        this.document_id = document_id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.available = available;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public MenuItem() {
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "document_id='" + document_id + '\'' +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return document_id.equals(menuItem.document_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document_id);
    }
}
