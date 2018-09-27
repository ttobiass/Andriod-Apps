package com.tiffinitobiasson.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
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
 * Created by tiffi on 4/21/2018.
 */

public class NewsSourceDownloader extends AsyncTask<String, Void, String>{

    private static final String TAG = "NewsSourceDownloader";

    private MainActivity mainAct;
    private final String APIKey = "Insert API Key Here";
    private String sourceURL = "https://newsapi.org/v1/sources?language=en&country=us&category=";
    private String category;

    public NewsSourceDownloader(MainActivity ma, String c) {
        mainAct = ma;
        if(c.equals("All")){
            category = "";
        } else if (c.equals("")){
            category=c;
        } else {
            category=c;
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String finalURL = sourceURL+category+APIKey;
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();

        try{
            URL url = new URL(urlToUse);
            Log.d(TAG, "doInBackground: connecting to url "+url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            Log.d(TAG, "doInBackground: getInputStream successful");
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            Log.d(TAG, "doInBackground: reading downloaded content to string");
            String line;
            while((line=reader.readLine()) != null){
                sb.append(line);
            }
            Log.d(TAG, "doInBackground: "+sb.toString());
            return sb.toString();
        } catch (Exception e){
            Log.d(TAG, "doInBackground: "+e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Object[] parsed = parseJSON(s);
        ArrayList<Source> sources = (ArrayList<Source>) parsed[0];
        ArrayList<String> category = (ArrayList<String>) parsed[1];
        mainAct.setSources(sources, category);
    }

    private Object[] parseJSON(String json){
        ArrayList<Source> sourceList = new ArrayList<>();
        ArrayList<String> categoryList = new ArrayList<>();
        try{

            JSONObject highlevel = new JSONObject(json);
            JSONArray sources = highlevel.getJSONArray("sources");
            for(int i=0; i<sources.length(); i++){
                JSONObject source = sources.getJSONObject(i);
                String ID = source.getString("id");
                String name = source.getString("name");
                String url = source.getString("url");
                String category = source.getString("category");
                Source s = new Source(ID, name, url, category);
                sourceList.add(s);
                if(!categoryList.contains(category)){
                    categoryList.add(category);
                }
            }

        } catch (Exception e){
            Log.d(TAG, "parseJSON: excpetion: "+e.getMessage());
        }

        return new Object[]{sourceList,categoryList};
    }
}
