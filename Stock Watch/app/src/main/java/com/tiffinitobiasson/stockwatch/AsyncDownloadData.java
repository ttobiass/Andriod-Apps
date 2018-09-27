package com.tiffinitobiasson.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tiffi on 3/3/2018.
 */

public class AsyncDownloadData extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncDownloadData";
    
    private MainActivity mainAct;
    private final String dataURL = "https://api.iextrading.com/1.0/stock/";

    public AsyncDownloadData(MainActivity ma) {
        mainAct = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Finding stock data for "+strings[0]);
        String finalURL = dataURL + strings[0] + "/quote";
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            Log.d(TAG, "doInBackground: connecting to url "+url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            Log.d(TAG, "doInBackground: getInputStream successful");
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            Log.d(TAG, "doInBackground: reading downloaded content to string");
            String line = reader.readLine();
            sb.append(line);

        } catch (Exception e) {
            Log.d(TAG, "doInBackground: "+e.toString());
            return null;
        }
        Log.d(TAG, "doInBackground: JSON array: "+sb.toString());
        return "["+sb.toString()+"]";
    }

    @Override
    protected void onPostExecute(String s) {
        Stock stock= parseJSON(s);
        mainAct.addNewStock(stock);
    }

    private Stock parseJSON(String json){
        Stock s = null;
        if(json==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(mainAct);

            builder.setMessage("Stock data was not found");
            builder.setTitle("No Stock Data Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            try {
                JSONArray jObjMain = new JSONArray(json);
                if (jObjMain.length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainAct);

                    builder.setMessage("Stock data was not found");
                    builder.setTitle("No Stock Data Found");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    for (int i = 0; i < jObjMain.length(); i++) {
                        JSONObject jStock = (JSONObject) jObjMain.get(i);
                        String symbol = jStock.getString("symbol");
                        String name = jStock.getString("companyName");
                        double price = jStock.getDouble("latestPrice");
                        double change = jStock.getDouble("change");
                        double changePercent = jStock.getDouble("changePercent")*100;

                        s = new Stock(name, symbol, price, change, changePercent);
                    }
                }
                return s;
            } catch (Exception e) {
                Log.d(TAG, "parseJSON: " + e.getMessage());
            }
        }
        Log.d(TAG, "parseJSON: Error or no stock found");
        return null;
    }
}
