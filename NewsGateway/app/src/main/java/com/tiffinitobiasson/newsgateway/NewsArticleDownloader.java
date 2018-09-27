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

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NewsArticleDownloader";
    private NewsService newsServ;
    private String id;
    private final String APIKey = "Insert API Key Here";
    private final String articleURL = "https://newsapi.org/v1/articles?source=";

    public NewsArticleDownloader(NewsService ns, String id) {
        newsServ = ns;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... strings) {
        String finalURL = articleURL+id+APIKey;
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
        ArrayList<Article> articles = parseJSON(s);
        newsServ.setArticles(articles);
    }

    private ArrayList<Article> parseJSON(String json){
        ArrayList<Article> art = new ArrayList<>();
        try{
            JSONObject highlevel = new JSONObject(json);
            JSONArray articles = highlevel.getJSONArray("articles");
            for(int i=0; i<articles.length(); i++){
                JSONObject article = articles.getJSONObject(i);
                String author=null, title=null, description=null, url=null, urlToImage=null, publishedAt=null;
                if(article.has("author")){
                    author = article.getString("author");
                }
                if(article.has("title")){
                    title = article.getString("title");
                }
                if(article.has("description")){
                    description = article.getString("description");
                }
                if(article.has("url")){
                    url = article.getString("url");
                }
                if(article.has("urlToImage")){
                    urlToImage = article.getString("urlToImage");
                }
                if(article.has("publishedAt")){
                    publishedAt = article.getString("publishedAt");
                }
                Article a = new Article(author, title, description, url, urlToImage, publishedAt);
                //Log.d(TAG, "parseJSON: "+a.toString());
                art.add(a);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return art;
    }
}
