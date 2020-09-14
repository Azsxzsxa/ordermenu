package com.example.ordermenu.Utils;

import com.example.ordermenu.Models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class OrderUtil {
    private static OrderUtil INSTANCE;
    private List<MenuItem> menuItemList = new ArrayList<>();

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
        menuItemList.add(menuItem);
    }

    private void removeItem(MenuItem menuItem) {
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

}
