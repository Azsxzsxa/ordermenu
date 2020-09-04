package com.example.ordermenu.Models;

import java.util.ArrayList;
import java.util.Date;

public class Restaurant {
    private String name;
    private Date joinDate;
    private ArrayList<String> employees=new ArrayList<>();

    public Restaurant(String name, Date joinDate, ArrayList<String> employees) {
        this.name = name;
        this.joinDate = joinDate;
        this.employees = employees;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public void setEmployees(ArrayList<String> employees) {
        this.employees = employees;
    }
}