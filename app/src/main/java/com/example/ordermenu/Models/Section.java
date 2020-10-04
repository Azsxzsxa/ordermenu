package com.example.ordermenu.Models;

import java.util.List;

public class Section {
    private String documentID;
    private String name;
    private int tableCount;
    private List<Table> tableList;

    public Section(String name, int tableCount) {
        this.name = name;
        this.tableCount = tableCount;
    }

    public Section() {
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public int getTableCount() {
        return tableCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }
}
