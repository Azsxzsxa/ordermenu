package com.example.ordermenu.Models;

public class Table {
    private String documentID;
    private Integer number;

    public Table(Integer number) {
        this.number = number;
    }

    public Table() {
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

}
