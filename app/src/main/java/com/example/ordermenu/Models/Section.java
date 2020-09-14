package com.example.ordermenu.Models;

public class Section {
    private String documentID;
    private String name;
    private int tableCount;

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
}
