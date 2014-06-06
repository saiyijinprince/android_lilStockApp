package com.akabeera.lil_stock_app;

import java.util.HashMap;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class TransactionData {

    private String ticker = "";
    private int shares = 0;
    private double price = 0;
    
    String GetTicker() { return this.ticker; }
    int GetShares() { return this.shares; }
    double GetPrice() { return this.price; }
    
    void SetShares(int shares) { this.shares = shares; }
    void SetPrice(double d) { this.price = d; }
    
    public TransactionData(String ticker, int shares, double price) {
        this.ticker = ticker;
        this.shares = shares;
        this.price = price;
    }
    
    public static String ToJson(TransactionData td){
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("ticker", td.GetTicker())
        .add("shares", td.GetShares())
        .add("price", td.GetPrice());
        
        return jsonObject.toString();
    }
    
    public static TransactionData FromJson(String json)
    {
        JsonObject jsonObject = JsonObject.readFrom(json);
        String ticker = jsonObject.get("ticker").asString();
        int shares = jsonObject.get("shares").asInt();
        double price = jsonObject.get("price").asDouble();
        
        return new TransactionData(ticker, shares, price);
    }
    
    public static HashMap<String, TransactionData> FromJsonArray(String json)
    {
        HashMap<String, TransactionData> tdList = new HashMap<String, TransactionData>();
        JsonArray jarr = JsonArray.readFrom(json);
        for (int i=0; i<jarr.size(); ++i){
            String obj = jarr.get(i).asString();
            TransactionData td = FromJson(obj);
            tdList.put(td.GetTicker(), td);
        }
        
        return tdList;
    }
}
