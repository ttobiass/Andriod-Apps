package com.tiffinitobiasson.stockwatch;

/**
 * Created by tiffi on 3/3/2018.
 */

public class Stock{

    private String name;
    private String symbol;
    private double latestPrice;
    private double change;
    private double changePercent;

    public Stock(String name, String symbol, double price, double change, double changePercent){
        this.name = name;
        this.symbol = symbol;
        this.latestPrice = price;
        this.change = change;
        this.changePercent = changePercent;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

}
