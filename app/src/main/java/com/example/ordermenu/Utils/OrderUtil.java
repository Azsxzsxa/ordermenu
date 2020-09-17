package com.example.ordermenu.Utils;

import android.util.Log;

import com.example.ordermenu.Models.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrderUtil {
    private static OrderUtil INSTANCE;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private List<MenuItem> orderedList = new ArrayList<>();
    private String tableDocID;
    private int tableNumber;
    private String sectionDocID;
    private String sectionName;
    private Date startOrderDate;
    private Date endOrderDate;

    private OrderUtil() {
    }

    public static OrderUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderUtil();
        }
        return INSTANCE;
    }

    public List<MenuItem> getMenuItemList() {
        return menuItemList;
    }

    public void clearMenuItemList() {
        menuItemList = new ArrayList<>();
    }

    private void addItem(MenuItem menuItem) {
        menuItem.setQuantity(1);
        menuItemList.add(menuItem);
    }

    private void removeItem(MenuItem menuItem) {
        menuItem.setQuantity(0);
        menuItemList.remove(menuItem);
    }

    public void increaseQuantity(MenuItem menuItem) {
        if (menuItemList.contains(menuItem)) {
            int position = menuItemList.indexOf(menuItem);
            menuItemList.get(position).setQuantity(menuItemList.get(position).getQuantity() + 1);
        } else {
            addItem(menuItem);
        }
    }

    public void decreaseQuantity(MenuItem menuItem) {
        if (menuItemList.contains(menuItem)) {
            int position = menuItemList.indexOf(menuItem);
            if (menuItemList.get(position).getQuantity() == 1) {
                removeItem(menuItem);
            } else {
                menuItemList.get(position).setQuantity(menuItemList.get(position).getQuantity() - 1);
            }
        }
    }

    public String getTableDocID() {
        return tableDocID;
    }

    public String getSectionDocID() {
        return sectionDocID;
    }

    private void setTableDocID(String tableDocID) {
        this.tableDocID = tableDocID;
    }

    private void setSectionDocID(String sectionDocID) {
        this.sectionDocID = sectionDocID;
    }

    public List<MenuItem> getOrderedList() {
        return orderedList;
    }

    public void setOrderedList(List<MenuItem> orderedList) {
        this.orderedList = orderedList;
    }

    public void setMenuItemList(List<MenuItem> menuItemList) {
        this.menuItemList = menuItemList;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    private void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getSectionName() {
        return sectionName;
    }

    private void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Date getStartOrderDate() {
        return startOrderDate;
    }

    public void setStartOrderDate(Date startOrderDate) {
        this.startOrderDate = startOrderDate;
    }

    public Date getEndOrderDate() {
        return endOrderDate;
    }

    public void setEndOrderDate(Date endOrderDate) {
        this.endOrderDate = endOrderDate;
    }

    public void setTableSwitched(String sectionDocID, String tableDocID, int tableNumber,
                                 String sectionName, Date startOrderDate, Date endOrderDate) {
        setSectionDocID(sectionDocID);
        setTableDocID(tableDocID);
        setTableNumber(tableNumber);
        setSectionName(sectionName);
        setStartOrderDate(startOrderDate);
        setEndOrderDate(endOrderDate);
        clearMenuItemList();
    }
}
