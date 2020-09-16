package com.example.ordermenu.Models;

import java.util.Objects;

public class Table {
    private String documentID;
    private Integer number;
    private Boolean occupied;

    public Table(Integer number, Boolean occupied) {
        this.number = number;
        this.occupied = occupied;
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

    public Boolean getOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(documentID, table.documentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentID);
    }

}
