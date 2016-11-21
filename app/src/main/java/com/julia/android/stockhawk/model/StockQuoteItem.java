package com.julia.android.stockhawk.model;

public class StockQuoteItem {

    private String name;
    private float price;
    private float change;
    private float percentChange;
    private StringBuilder historyBuilder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getChange() {
        return change;
    }

    public void setChange(float change) {
        this.change = change;
    }

    public float getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(float percentChange) {
        this.percentChange = percentChange;
    }

    public void setHistoryBuilder(StringBuilder historyBuilder) {
        this.historyBuilder = historyBuilder;
    }

    public StringBuilder getHistoryBuilder() {
        return historyBuilder;
    }
}
