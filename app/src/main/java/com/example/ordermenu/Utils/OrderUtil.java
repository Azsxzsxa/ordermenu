package com.example.ordermenu.Utils;

import android.util.Log;

import com.example.ordermenu.Models.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.ordermenu.Utils.StrUtil.DB_MENUITEM_STATUS_DEFAULT;

public class OrderUtil {
    private static OrderUtil INSTANCE;
    private List<MenuItem> currentOrderList = new ArrayList<>();
    private List<MenuItem> alreadyOrderedList = new ArrayList<>();
    private List<MenuItem> allMenuItemsList = new ArrayList<>();
    private List<MenuItem> searchMenuItemsList = new ArrayList<>();
    private String tableDocID;
    private int tableNumber;
    private String sectionDocID;
    private String sectionName;
    private Date startOrderDate;
    private Date endOrderDate;
    private String tableStatus;

    private OrderUtil() {
    }

    public static OrderUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderUtil();
        }
        return INSTANCE;
    }

    public List<MenuItem> getCurrentOrderList() {
        return currentOrderList;
    }

    public void clearMenuItemList() {
        currentOrderList = new ArrayList<>();
    }

    private void addItem(MenuItem menuItem) {
        menuItem.setQuantity(1);
        currentOrderList.add(menuItem);
    }

    private void removeItem(MenuItem menuItem) {
        menuItem.setQuantity(0);
        currentOrderList.remove(menuItem);
    }

    public void increaseQuantity(MenuItem menuItem) {
        if (currentOrderList.contains(menuItem)) {
            int position = currentOrderList.indexOf(menuItem);
            currentOrderList.get(position).setQuantity(currentOrderList.get(position).getQuantity() + 1);
        } else {
            addItem(menuItem);
        }
    }

    public void decreaseQuantity(MenuItem menuItem) {
        if (currentOrderList.contains(menuItem)) {
            int position = currentOrderList.indexOf(menuItem);
            if (currentOrderList.get(position).getQuantity() == 1) {
                removeItem(menuItem);
            } else {
                currentOrderList.get(position).setQuantity(currentOrderList.get(position).getQuantity() - 1);
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

    public List<MenuItem> getAlreadyOrderedList() {
        return alreadyOrderedList;
    }

    public void setAlreadyOrderedList(List<MenuItem> alreadyOrderedList) {
        this.alreadyOrderedList = alreadyOrderedList;
    }

    public void setCurrentOrderList(List<MenuItem> currentOrderList) {
        this.currentOrderList = currentOrderList;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
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

    public List<MenuItem> getAllMenuItemsList() {
        return allMenuItemsList;
    }

    public void setAllMenuItemsList(List<MenuItem> allMenuItemsList) {
//        this.allMenuItemsList = allMenuItemsList;
        for (MenuItem menuItem : allMenuItemsList) {

            searchMenuItemsList.add(new MenuItem(menuItem.getDocument_id(), menuItem.getCategory(), menuItem.getName(),
                    menuItem.getPrice(), menuItem.getQuantity(), menuItem.getAvailable(),DB_MENUITEM_STATUS_DEFAULT));
            this.allMenuItemsList.add(new MenuItem(menuItem.getDocument_id(), menuItem.getCategory(), menuItem.getName(),
                    menuItem.getPrice(), menuItem.getQuantity(), menuItem.getAvailable(),DB_MENUITEM_STATUS_DEFAULT));
        }
//        setSearchMenuItemsList(allMenuItemsList);
        updateSearchItemList();
    }

    public void updateSearchItemList() {
        if (!currentOrderList.isEmpty()) {
            for (MenuItem menuItem : currentOrderList) {
                int position = getSearchMenuItemsList().indexOf(menuItem);
                getSearchMenuItemsList().get(position).setQuantity(menuItem.getQuantity());
            }
        }
    }


    public List<MenuItem> getSearchMenuItemsList() {
        return searchMenuItemsList;
    }

    public void setSearchMenuItemsList(List<MenuItem> searchMenuItemsList) {
        this.searchMenuItemsList = searchMenuItemsList;
    }

    public void setTableSwitched(String sectionDocID, String tableDocID, int tableNumber,
                                 String sectionName, Date startOrderDate, Date endOrderDate, String tableStatus) {
        setSectionDocID(sectionDocID);
        setTableDocID(tableDocID);
        setTableNumber(tableNumber);
        setSectionName(sectionName);
        setStartOrderDate(startOrderDate);
        setEndOrderDate(endOrderDate);
        setTableStatus(tableStatus);
        resetSearchMenuItemList();
        clearMenuItemList();
    }

    private void resetSearchMenuItemList() {
        searchMenuItemsList = new ArrayList<>();
//        searchMenuItemsList.addAll(allMenuItemsList);
        for (MenuItem menuItem : allMenuItemsList) {
            searchMenuItemsList.add(new MenuItem(menuItem.getDocument_id(), menuItem.getCategory(), menuItem.getName(),
                    menuItem.getPrice(), menuItem.getQuantity(), menuItem.getAvailable(),DB_MENUITEM_STATUS_DEFAULT));
        }
    }
}
