package com.akabeera.lil_stock_app;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class TransactionDataAdapter extends ArrayAdapter<TransactionData>{

    private Context context;
    private ArrayList<TransactionData> transactionData;
    
    static class ViewHolder {
        public TextView tickerView;
        public EditText sharesEditText;
        public EditText pricesEditText;
    }
    
    private enum ViewType {
      SHARES,
      PRICES
    };
    
    private class MyTextWatcher implements TextWatcher {

        private ViewType _vt;
        private EditText _editText;
        
        public MyTextWatcher(ViewType vt, EditText editText){
            this._editText = editText;
            this._vt = vt;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub   
        }

        @Override
        public void afterTextChanged(Editable s) {

            String newText = s.toString();
            if (newText.isEmpty())
                return;
            
            int position = (Integer)this._editText.getTag();
            
            switch (_vt){
                case SHARES:
                    transactionData.get(position).SetShares(Integer.parseInt(this._editText.getText().toString()));
                    break;
                case PRICES:
                    transactionData.get(position).SetPrice(Double.parseDouble(this._editText.getText().toString()));
                    break;
            }
        }
    }
    
    public TransactionDataAdapter(Context context, ArrayList<TransactionData> tdArray){
        super(context, R.layout.transaction_data_row);
        this.context = context;
        this.transactionData = tdArray;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        TransactionData td = getItem(position);
        
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_data_row, parent, false);
        }
        
        TextView tickerView = (TextView) convertView.findViewById(R.id.transactionDataTickerView);
        tickerView.setText(td.GetTicker());
        
        EditText sharesEditText = (EditText) convertView.findViewById(R.id.NumSharesEditText);
        sharesEditText.setTag(position);
        sharesEditText.addTextChangedListener(new MyTextWatcher(ViewType.SHARES, sharesEditText));
        sharesEditText.setText(Integer.toString(td.GetShares()));
        
        EditText priceEditText = (EditText) convertView.findViewById(R.id.PriceEditText);
        priceEditText.setTag(position);
        priceEditText.addTextChangedListener(new MyTextWatcher(ViewType.PRICES, priceEditText));
        priceEditText.setText(Double.toString(td.GetPrice()));
        
        /*
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_data_row, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tickerView = (TextView) convertView.findViewById(R.id.transactionDataTickerView);
            viewHolder.pricesEditText = (EditText) convertView.findViewById(R.id.PriceEditText);
            viewHolder.sharesEditText = (EditText) convertView.findViewById(R.id.NumSharesEditText);
            convertView.setTag(viewHolder);
        }
        
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tickerView.setText(td.GetTicker());
        viewHolder.pricesEditText.setText(Double.toString(td.GetPrice()));
        viewHolder.pricesEditText.setTag(position);
        viewHolder.sharesEditText.setText(Integer.toString(td.GetShares()));
        viewHolder.sharesEditText.setTag(position);
        viewHolder.pricesEditText.addTextChangedListener(new MyTextWatcher(ViewType.PRICES, viewHolder.pricesEditText));
        viewHolder.sharesEditText.addTextChangedListener(new MyTextWatcher(ViewType.SHARES, viewHolder.sharesEditText));
        */
        
        return convertView;
    }
    
    public ArrayList<TransactionData> GetItems() { return transactionData; }
}
