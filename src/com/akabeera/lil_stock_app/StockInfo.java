package com.akabeera.lil_stock_app;

public class StockInfo {
    private String ticker = "";
    private String daysLow = "";
    private String daysHigh = "";
    private String yearLow = "";
    private String yearHigh = "";
    private String name = "";
    private String lastTradePriceOnly = "";
    private String change = "";
    private String daysRange = "";

    public String getTicker () { return ticker; }
    public String getDaysLow() { return daysLow; }
    public String getDaysHigh() { return daysHigh; }
    public String getYearLow() { return yearLow; }
    public String getYearHigh() { return yearHigh; }
    public String getName() { return name; }
    public String getLastTradePriceOnly() { return lastTradePriceOnly; }
    public String getChange() { return change;}
    public String getDaysRange() {return daysRange;}
    

    public StockInfo(String ticker, String daysLow, String daysHigh, String yearLow,
            String yearHigh, String name, String lastTradePriceOnly,
            String change, String daysRange) {
        this.ticker = ticker;
        this.daysLow = daysLow;
        this.daysHigh = daysHigh;
        this.yearLow = yearLow;
        this.yearHigh = yearHigh;
        this.name = name;
        this.lastTradePriceOnly = lastTradePriceOnly;
        this.change = change;
        this.daysRange = daysRange;
    }
}
