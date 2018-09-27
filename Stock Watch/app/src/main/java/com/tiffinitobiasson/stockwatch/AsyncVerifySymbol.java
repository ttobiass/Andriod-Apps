package com.tiffinitobiasson.stockwatch;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by tiffi on 3/3/2018.
 */

public class AsyncVerifySymbol extends AsyncTask<String,Void,String> {
    private static final String TAG = "AsyncVerifySymbol";

    private MainActivity mainAct;
    private final String dataURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=";
    private String input;

    public AsyncVerifySymbol(MainActivity ma) {
        mainAct = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        input = strings[0];
        String finalURL = dataURL + strings[0];
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();

        Log.d(TAG, "doInBackground: "+urlToUse);

        StringBuilder sb = new StringBuilder();
        String result = null;
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line=reader.readLine()) != null){
                sb.append(line).append('\n');
            }

            result = sb.toString();
            result = result.substring(result.indexOf("["),result.indexOf("]")+1);

            Log.d(TAG, "doInBackground: "+result);
        } catch (Exception e){
            Log.d(TAG, "doInBackground: "+e);
            return null;
        }

        Log.d(TAG, "doInBackground: returned string - "+result);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        final ArrayList<String[]> sList = parseJSON(s);
        if(sList == null || sList.size()==0){
            //no stock found error message
            AlertDialog.Builder builder = new AlertDialog.Builder(mainAct);

            builder.setMessage("No stocks match the symbol: "+input);
            builder.setTitle("No Stock Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (sList.size()==1){
            //only one stock so use that to send symbol back to main activity
            mainAct.processNewStock(sList.get(0)[0]);
        } else {
            //many stocks found so show dialog box for selection and then send selected symbol back to main activity
            final CharSequence[] sArray = new CharSequence[sList.size()];
            for (int i = 0; i < sList.size(); i++)
                sArray[i] = sList.get(i)[0] + '\n' + sList.get(i)[1];

            AlertDialog.Builder builder = new AlertDialog.Builder(mainAct);
            builder.setTitle("Make a selection");

            builder.setItems(sArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String symbol = sList.get(which)[0];
                    mainAct.processNewStock(symbol);
                }
            });

            builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }

    private ArrayList<String[]> parseJSON(String s){
        ArrayList<String[]> results = new ArrayList<>();
        try{
            JSONArray jObjMain = new JSONArray(s);

            for(int i=0; i<jObjMain.length(); i++){
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("symbol");
                String company = jStock.getString("name");
                String type = jStock.getString("type");

                if(!symbol.contains(".") && type.equals("S")){
                    String[] valid = {symbol,company};
                    results.add(valid);
                }
            }
            return results;

        } catch (Exception e){
            Log.d(TAG, "parseJSON: "+e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
