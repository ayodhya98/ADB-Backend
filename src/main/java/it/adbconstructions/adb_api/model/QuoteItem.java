package it.adbconstructions.adb_api.model;

import java.io.Serializable;

public class QuoteItem implements Serializable {
    private String materialName;
    private int quantity;
    private double unitPrice;
    private double total;

    public QuoteItem() {
    }

    public QuoteItem(String materialName, int quantity, double unitPrice, double total) {
        this.materialName = materialName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
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
