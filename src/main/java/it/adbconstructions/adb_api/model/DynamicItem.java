package it.adbconstructions.adb_api.model;

import java.io.Serializable;

public class DynamicItem implements Serializable {
    private String itemName;
    private int quantity;
    private double unitPrice;
    private double total;

    public DynamicItem() {
    }

    public DynamicItem(String itemName, int quantity, double unitPrice, double total) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
