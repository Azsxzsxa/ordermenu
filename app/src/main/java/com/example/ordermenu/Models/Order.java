package com.example.ordermenu.Models;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order {
    private String userId;
    private int tableNumber;
    private String tableSection;
    private Date startTime;
    private Date endTime;
    private String documentId;
    private List<MenuItem> menuItems;

    public Order(String documentId, String userId, int tableNumber, String tableSection, Date startTime, Date endTime, List<MenuItem> menuItems) {
        this.documentId = documentId;
        this.userId = userId;
        this.tableNumber = tableNumber;
        this.tableSection = tableSection;
        this.startTime = startTime;
        this.endTime = endTime;
        this.menuItems = menuItems;
    }

    public Order() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableSection() {
        return tableSection;
    }

    public void setTableSection(String tableSection) {
        this.tableSection = tableSection;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return documentId.equals(order.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId);
    }
}
