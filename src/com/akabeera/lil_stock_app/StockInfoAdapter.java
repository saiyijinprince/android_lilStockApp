package com.akabeera.lil_stock_app;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StockInfoAdapter extends ArrayAdapter<StockInfo> {
    
    private Context context;
    
    public StockInfoAdapter(Context context, ArrayList<StockInfo> stocks){
        super(context, R.layout.stock_quote_row);
        
        this.context = context;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    
        StockInfo si = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_quote_row, parent, false);
        }
        
        double priceChange = Double.parseDouble(si.getChange());
        int c = 0;
        if (priceChange < 0)
            c = context.getResources().getColor(R.color.negative);
        else c = context.getResources().getColor(R.color.positive);
        
        TextView tickerView = (TextView) convertView.findViewById(R.id.stockListTickerView);
        tickerView.setText(si.getTicker());
        
        TextView priceView = (TextView) convertView.findViewById(R.id.stockListPriceView);
        priceView.setText(si.getLastTradePriceOnly());
        priceView.setTextColor(c);
        
        TextView pctChangeView = (TextView) convertView.findViewById(R.id.stockListPctChangeView);
        pctChangeView.setText(si.getChange());
        pctChangeView.setTextColor(c);
        
        TextView dyHighView = (TextView) convertView.findViewById(R.id.stockListDayHighView);
        dyHighView.setText(si.getDaysHigh());
        dyHighView.setTextColor(c);
        
        TextView dyLowView = (TextView) convertView.findViewById(R.id.stockListDayLowView);
        dyLowView.setText(si.getDaysHigh());
        dyLowView.setTextColor(c);
        
        TextView yrHighView = (TextView) convertView.findViewById(R.id.stockListYearHighView);
        yrHighView.setText(si.getYearHigh());
        yrHighView.setTextColor(c);
        
        TextView yrLowView = (TextView) convertView.findViewById(R.id.stockListYearLowView);
        yrLowView.setText(si.getYearLow());
        yrLowView.setTextColor(c);
        
        TextView companyView = (TextView) convertView.findViewById(R.id.stockListCompanyView);
        companyView.setText(si.getName());
        
        return convertView;
    }
}
