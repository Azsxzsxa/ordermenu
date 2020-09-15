package com.example.ordermenu.Utils;

import android.util.Log;

import com.example.ordermenu.Models.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrderUtil {
    private static OrderUtil INSTANCE;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private String tableDocID;
    private String sectionDocID;

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

    public void setTableDocID(String tableDocID) {
        this.tableDocID = tableDocID;
    }

    public void setSectionDocID(String sectionDocID) {
        this.sectionDocID = sectionDocID;
    }
}
