
package com.akabeera.lil_stock_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends ActionBarActivity {
    
    private static final String TAG = "LILSTOCKAPP";
    
    public final static String STOCK_SYMBOL = "com.akabeera.lilstockapp.STOCK";
    private final static String yahooURLFirst = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(";
    private final static String yahooURLSecond = ")&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&format=json";
    
    private ArrayList<StockInfo> StockInfoList;
    private ArrayList<StockInfo> TempStockInfoList;
    private StockInfoAdapter StockInfoListAdapter;
    
    private SharedPreferences stockSymbolsEntered;
    private EditText stockSymbolEditText;
    
    Button enterStockSymbolButton;
    Button deleteStocksButton;
    Button AddEditTransactionsButton;
    ImageButton refreshButton;
    
    private ProgressBar fetchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);
        
        // Initialize Components
        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
        enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);
        deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);
        AddEditTransactionsButton = (Button) findViewById(R.id.transactionButton);
        fetchProgress = (ProgressBar) findViewById(R.id.fetchProgressBar);
        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        fetchProgress.setVisibility(View.GONE);
        
        // Add ClickListeners to the buttons
        enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
        deleteStocksButton.setOnClickListener(deleteStocksButtonListener);
        refreshButton.setOnClickListener(refreshButtonListener);
        AddEditTransactionsButton.setOnClickListener(addEditTransactionButtonListener);
        
        StockInfoList =  new ArrayList<StockInfo>();
        TempStockInfoList =  new ArrayList<StockInfo>();
        StockInfoListAdapter = new StockInfoAdapter(this, StockInfoList);

        View Header = View.inflate(this, R.layout.stock_quote_row_header, null);
        ListView listView = (ListView)findViewById(R.id.listViewId);
        listView.addHeaderView(Header);
        listView.setAdapter(StockInfoListAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //minus 1 to count for the header.
                int real_pos = position-1;
                if (real_pos < 0 || real_pos >= TempStockInfoList.size())
                    return;
                StockInfo si = TempStockInfoList.get(real_pos);
                String ticker = si.getTicker();
                
                // The URL specific for the stock symbol
                String stockURL = getString(R.string.yahoo_stock_url) + ticker;
                Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
                startActivity(getStockWebPage);
            }
        });
        
        updateSavedStockList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public OnClickListener enterStockButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (stockSymbolEditText.getText().length() <= 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.invalid_stock_symbol);
                builder.setPositiveButton(R.string.ok, null);
                builder.setMessage(R.string.missing_stock_symbol);
                AlertDialog theAlertDialog = builder.create();
                theAlertDialog.show();
            } else {
                saveStockSymbol(stockSymbolEditText.getText().toString());
                stockSymbolEditText.setText("");
            
                InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
            }
        }
    };
    
    public OnClickListener deleteStocksButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            
            StockInfoListAdapter.clear();
            SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
            
            preferencesEditor.clear();
            preferencesEditor.apply();
        }
    };
    
    public OnClickListener refreshButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            updateSavedStockList();
        }
    };
    
    public OnClickListener addEditTransactionButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(MainActivity.this, AddEditTransactionDataActivity.class);
            
            String[] tickers = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
            Arrays.sort(tickers, String.CASE_INSENSITIVE_ORDER);
            
            myIntent.putExtra("tickersList", tickers);
            MainActivity.this.startActivity(myIntent);
        }
        
    };
    
    private void saveStockSymbol(String newStock){
        List<String> stocksList = new ArrayList<String>(Arrays.asList(newStock.split(",")));
        
        for(String s:stocksList){
            String isTheStockNew = stockSymbolsEntered.getString(s, null);

            if (isTheStockNew == null  && isValidTicker(s)){
                SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
                preferencesEditor.putString(s, s);
                preferencesEditor.apply();
            }
        }
        
        updateSavedStockList();
    }
    
    private boolean isValidTicker(String ticker){
        if (ticker.isEmpty())
            return false;
        
        for (int i=0; i<ticker.length(); ++i){
            char c = ticker.charAt(i);
            if (!((c > 'A' && c < 'Z') || 
                 (c > 'a' && c < 'z') ||
                 (c >= '0' && c <= '9')))
                return false;
        }
        
        return true;
    }
    
    private void updateSavedStockList(){
        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);
        
        fetchStockInfo(stocks);
    }
    
    private void fetchStockInfo(String[] stocks)
    {
        String request = "";
        for (int i=0; i<stocks.length; ++i){
            if (i > 0)
                request += "%2c";
            request += "%22" + stocks[i] + "%22";
        }
        
        if (request.length() > 0){
            final String yqlURL = yahooURLFirst + request + yahooURLSecond;
            new MyAsyncTask().execute(yqlURL);
        }
    }
    
    static final String KEY_QUERY = "query"; 
    static final String KEY_COUNT = "count"; 
    static final String KEY_RESULTS = "results"; 
    static final String KEY_QUOTE = "quote"; 
    static final String KEY_NAME = "Name";
    static final String KEY_YEAR_LOW = "YearLow";
    static final String KEY_YEAR_HIGH = "YearHigh";
    static final String KEY_DAYS_LOW = "DaysLow";
    static final String KEY_DAYS_HIGH = "DaysHigh";
    static final String KEY_LAST_TRADE_PRICE = "LastTradePriceOnly";
    static final String KEY_CHANGE = "Change";
    static final String KEY_DAYS_RANGE = "DaysRange";
    static final String KEY_SYMBOL = "symbol";
    
    private class MyAsyncTask extends AsyncTask<String, String, String>{

        protected void onPreExecute(){        
            fetchProgress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        
        @Override
        protected String doInBackground(String... params) {
            try {
                final URL url = new URL(params[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.connect();
                
                final int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK){
                    Log.d(TAG, "Error making request");
                    return null;
                }
                
                InputStream in = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JsonObject jsonObj = JsonObject.readFrom(responseStrBuilder.toString());
                JsonObject query = jsonObj.get(KEY_QUERY).asObject();
                JsonObject results = query.get(KEY_RESULTS).asObject();
                int count = query.get(KEY_COUNT).asInt();
                
                TempStockInfoList.clear();
                if (count > 1){
                    JsonArray quote = results.get(KEY_QUOTE).asArray();    
                    for (int i=0; i<quote.size(); ++i){
                        JsonObject info = quote.get(i).asObject();
                        StockInfo stockInfo = ParseStockInfoJson(info);
                        TempStockInfoList.add(stockInfo);
                    }
                } else {
                    JsonObject quote = results.get(KEY_QUOTE).asObject();    
                    StockInfo stockInfo = ParseStockInfoJson(quote);
                    TempStockInfoList.add(stockInfo);
                }
                
            } catch (MalformedURLException e) {
                Log.d(TAG, "MalformedURLException", e);             
            } catch (IOException e) {
                Log.d(TAG, "IOException", e);
            } finally {}
            return null;
        }
        
        private StockInfo ParseStockInfoJson(JsonObject info){
            String symbol = "";
            String name = "";
            String yearLow = "";
            String yearHigh = "";
            String daysLow = "";
            String daysHigh = "";
            String lastTradePriceOnly = "";
            String change = "";
            String daysRange = "";
            
            symbol = info.get(KEY_SYMBOL).asString();
            name = info.get(KEY_NAME).asString();

            if (info.get(KEY_CHANGE).asString() != "null"){
                change = info.get(KEY_CHANGE).asString();
                yearLow = info.get(KEY_YEAR_LOW).asString();
                yearHigh = info.get(KEY_YEAR_HIGH).asString();
                daysLow = info.get(KEY_DAYS_LOW).asString();
                daysHigh = info.get(KEY_DAYS_HIGH).asString();
                lastTradePriceOnly = info.get(KEY_LAST_TRADE_PRICE).asString();
            } else {
                String empty_stub = getString(R.string.empty_value);
                change =  "0";
                yearLow = empty_stub;
                yearHigh = empty_stub;
                daysLow = empty_stub;
                daysHigh = empty_stub;
                lastTradePriceOnly = empty_stub;
            }
                
            StockInfo stockInfo = new StockInfo(symbol, daysLow, daysHigh, yearLow, yearHigh,
                    name, lastTradePriceOnly, change, daysRange);
            
            return stockInfo;
        }
        
        protected void onPostExecute(String result){
            StockInfoListAdapter.clear();
            StockInfoListAdapter.addAll(TempStockInfoList);
            fetchProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
