package com.example.ordermenu.Models;

import java.util.Date;
import java.util.Objects;

public class Table {
    private String documentID;
    private Integer number;
    private String occupied;
    private Date startOrderDate;
    private Date endOrderDate;

    public Table(Integer number, String occupied, Date startOrderDate, Date endOrderDate) {
        this.number = number;
        this.occupied = occupied;
        this.startOrderDate = startOrderDate;
        this.endOrderDate = endOrderDate;
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

    public String getOccupied() {
        return occupied;
    }

    public void setOccupied(String occupied) {
        this.occupied = occupied;
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
