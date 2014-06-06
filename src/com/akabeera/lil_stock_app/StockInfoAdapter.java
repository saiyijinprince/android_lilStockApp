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
    
    static class ViewHolder {
        public TextView tickerView;
        public TextView priceView;
        public TextView pctChangeView;
        public TextView dyHighView;
        public TextView dyLowView;
        public TextView yrHighView;
        public TextView yrLowView;
        public TextView companyView;
    }
    
    public StockInfoAdapter(Context context, ArrayList<StockInfo> stocks){
        super(context, R.layout.stock_quote_row);
        
        this.context = context;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    
        StockInfo si = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_quote_row, parent, false);
            //configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tickerView = (TextView) convertView.findViewById(R.id.stockListTickerView);
            viewHolder.priceView = (TextView) convertView.findViewById(R.id.stockListPriceView);
            viewHolder.pctChangeView = (TextView) convertView.findViewById(R.id.stockListPctChangeView);
            viewHolder.dyHighView = (TextView) convertView.findViewById(R.id.stockListDayHighView);
            viewHolder.dyLowView = (TextView) convertView.findViewById(R.id.stockListDayLowView);
            viewHolder.yrHighView = (TextView) convertView.findViewById(R.id.stockListYearHighView);            
            viewHolder.yrLowView = (TextView) convertView.findViewById(R.id.stockListYearLowView);
            viewHolder.companyView = (TextView) convertView.findViewById(R.id.stockListCompanyView);            
            convertView.setTag(viewHolder);
        }
        
        double priceChange = Double.parseDouble(si.getChange());
        int c = 0;
        
        if (priceChange < 0)
            c = context.getResources().getColor(R.color.negative);
        else if (priceChange == 0)
            c = context.getResources().getColor(R.color.white);
        else c = context.getResources().getColor(R.color.positive);
        
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.tickerView.setText(si.getTicker());

        holder.priceView.setText(si.getLastTradePriceOnly());
        holder.priceView.setTextColor(c);
        
        holder.pctChangeView.setText(si.getChange());
        holder.pctChangeView.setTextColor(c);
        
        holder.dyHighView.setText(si.getDaysHigh());
        holder.dyHighView.setTextColor(c);

        holder.dyLowView.setText(si.getDaysLow());
        holder.dyLowView.setTextColor(c);
        
        holder.yrHighView.setText(si.getYearHigh());
        holder.yrHighView.setTextColor(c);
        
        holder.yrLowView.setText(si.getYearLow());
        holder.yrLowView.setTextColor(c);
        
        holder.companyView.setText(si.getName());
        
        return convertView;
    }
}
