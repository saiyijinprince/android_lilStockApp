
package com.akabeera.lil_stock_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import com.eclipsesource.json.JsonArray;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class AddEditTransactionDataActivity extends Activity {
    
    private ListView transactionDataListView;
    private TransactionDataAdapter transactionDataAdapter;
    private ArrayList<TransactionData> transactionData;
    
    private static String prefsKey = "com.akabeera.lilStockApp";
    private static String tdKey = "com.akabeera.lilStockApp.tdJson";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction_data);
        
        transactionData = new ArrayList<TransactionData>();
        preferences = getSharedPreferences(prefsKey, MODE_PRIVATE);
        
        Intent intent = getIntent();
        String[] tickers = intent.getStringArrayExtra("tickersList");
        
        transactionDataListView = (ListView) findViewById(R.id.TransactionDatalistView);
        transactionDataAdapter = new TransactionDataAdapter(this, transactionData);
        transactionDataListView.setAdapter(transactionDataAdapter);
        
        FetchTransactionDataTask fetch = new FetchTransactionDataTask();
        fetch.execute(tickers);
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        
        JsonArray jarr = new JsonArray();
        for(TransactionData td:transactionData){
            jarr.add(TransactionData.ToJson(td));
        }
        
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        String jsonTd = jarr.toString();
        editor.putString(tdKey, jsonTd);
        editor.commit();
    }
    /*
    @Override
    protected void onResume(){
        Intent intent = getIntent();
        String[] tickers = intent.getStringArrayExtra("tickersList");
        
        FetchTransactionDataTask fetch = new FetchTransactionDataTask();
        fetch.execute(tickers);
        
        super.onResume();
    }
    */
    
    @Override
    protected void onRestart(){
        super.onRestart();
    }
    
    private class FetchTransactionDataTask extends AsyncTask<String[], String, String> {

        @Override
        protected String doInBackground(String[]... params) 
        {    
            String tdJson = preferences.getString(tdKey, null);

            HashMap<String, TransactionData> savedData = null;
            if (tdJson != ""){
                savedData = TransactionData.FromJsonArray(tdJson);
            }
            
            String[] tickers = params[0];
            for (int i=0; i<tickers.length; ++i){
                String ticker = tickers[i].toUpperCase(Locale.getDefault());
                TransactionData td = null;
                
                if (savedData != null && savedData.containsKey(ticker))
                    td = savedData.get(ticker);
                else
                    td = new TransactionData(ticker, 0, 0.0);
                transactionData.add(td);
            }
            
            return null;
        }
        
        protected void onPostExecute(String result){
            transactionDataAdapter.clear();
            transactionDataAdapter.addAll(transactionData);
        }
    }
}
