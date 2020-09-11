package com.example.ordermenu.Models;

import java.util.ArrayList;
import java.util.Date;

public class Restaurant {
    private String name;
    private Date joinDate;
    private ArrayList<String> employees = new ArrayList<>();
    private ArrayList<String> menuCategories = new ArrayList<>();

    public Restaurant(String name, Date joinDate, ArrayList<String> employees, ArrayList<String> menuCathegories) {
        this.name = name;
        this.joinDate = joinDate;
        this.employees = employees;
        this.menuCategories = menuCathegories;
    }

    public Restaurant() {
    }

    public String getName() {
        return name;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public ArrayList<String> getEmployees() {
        return employees;
    }

    public ArrayList<String> getMenuCategories() {
        return menuCategories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public void setEmployees(ArrayList<String> employees) {
        this.employees = employees;
    }

    public void setMenuCategories(ArrayList<String> menuCategories) {
        this.menuCategories = menuCategories;
    }
}
