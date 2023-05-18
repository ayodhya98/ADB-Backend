package it.adbconstructions.adb_api.model;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Quotation implements Serializable {

    private List<QuoteItem> quoteItems;
    private List<DynamicItem> dynamicItems;
    private String employee;
    private double totalPrice;
    private Date createdOn;

    public Quotation() {

    }

    public Quotation(List<QuoteItem> quoteItems, List<DynamicItem> dynamicItems, double totalPrice, Date createdOn, String employee) {
        this.quoteItems = quoteItems;
        this.dynamicItems = dynamicItems;
        this.totalPrice = totalPrice;
        this.createdOn = createdOn;
        this.employee = employee;
    }

    public List<QuoteItem> getQuoteItems() {
        return quoteItems;
    }

    public void setQuoteItems(List<QuoteItem> quoteItems) {
        this.quoteItems = quoteItems;
    }

    public List<DynamicItem> getDynamicItems() {
        return dynamicItems;
    }

    public void setDynamicItems(List<DynamicItem> dynamicItems) {
        this.dynamicItems = dynamicItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }
}
