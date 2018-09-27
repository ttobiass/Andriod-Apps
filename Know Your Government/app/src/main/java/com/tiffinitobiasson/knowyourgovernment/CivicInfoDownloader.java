package com.tiffinitobiasson.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by tiffi on 4/15/2018.
 */

public class CivicInfoDownloader extends AsyncTask<String, Void, String>{

    private static final String TAG = "CivicInfoDownloader";
    
    private MainActivity mainAct;
    private final String API = "Insert API Key Here";
    private String civicURL = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private String address;

    public CivicInfoDownloader(MainActivity ma) {mainAct = ma;}

    @Override
    protected String doInBackground(String... strings) {
        address = strings[0];
        String finalURL = civicURL+API+"&address="+address;
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);
            Log.d(TAG, "doInBackground: connecting to url "+url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode()==400){
                Log.d(TAG, "doInBackground: Response code 400");
                return sb.toString();
            } else {
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
            }
        } catch (Exception e){
            Log.d(TAG, "doInBackground: "+e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: "+s);

        if(s==null){
            Toast.makeText(mainAct,"Civic Info Service is unavailable", Toast.LENGTH_SHORT).show();
            mainAct.setOfficialList(null);
        } else if (s.equals("")){
            Toast.makeText(mainAct,"No data available for "+address, Toast.LENGTH_SHORT).show();
            mainAct.setOfficialList(null);
        } else {
            Object[] parsed = parseJSON(s);
            mainAct.setOfficialList(parsed);
        }
    }

    private Object[] parseJSON(String json){
        ArrayList<Official> os = new ArrayList<>();
        String location=null;
        try{
            JSONObject highLevel = new JSONObject(json);
            JSONObject normalizedInput = highLevel.getJSONObject("normalizedInput");
            JSONArray offices = highLevel.getJSONArray("offices");
            JSONArray officials = highLevel.getJSONArray("officials");

            location = normalizedInput.getString("city")+", "+normalizedInput.getString("state")+" "+normalizedInput.getString("zip");

            for(int i = 0; i<=offices.length();i++){
                JSONObject office = offices.getJSONObject(i);
                String officeTitle = office.getString("name");
                JSONArray officeIndices = office.getJSONArray("officialIndices");
                for(int j=0; j<officeIndices.length();j++){
                    JSONObject official = officials.getJSONObject(officeIndices.getInt(j));
                    //Get officials name
                    String name = official.getString("name");

                    //Get officials address concatenating all of the lines into one string
                    String addressFinal = null;
                    if(official.has("address")) {
                        JSONObject add = official.getJSONArray("address").getJSONObject(0);
                        StringBuilder address = new StringBuilder();
                        if (add.has("line1")) {
                            address.append(add.getString("line1"));
                        }
                        if (add.has("line2")) {
                            address.append(", " + add.getString("line2"));
                        }
                        if (add.has("line3")) {
                            address.append(", " + add.getString("line3"));
                        }
                        address.append("\n");
                        if (add.has("city")) {
                            address.append(add.getString("city"));
                        }
                        if (add.has("state")) {
                            address.append(", " + add.getString("state") + " ");
                        }
                        if (add.has("zip")) {
                            address.append(add.getString("zip"));
                        }
                        if (address.length() != 0) {
                            addressFinal = address.toString();
                        }
                    }

                    //Check for official Party
                    String party=null;
                    if(official.has("party")) {
                        party = official.getString("party");
                    } else {party = "Unknown";}

                    //check for and get officials phone
                    String phone = null;
                    if(official.has("phones")){
                        phone = official.getJSONArray("phones").getString(0);
                    }

                    //check for and get officials website
                    String website = null;
                    if(official.has("urls")){
                        website = official.getJSONArray("urls").getString(0);
                    }

                    //check for and get officials email
                    String email = null;
                    if(official.has("emails")){
                        email = official.getJSONArray("emails").getString(0);
                    }

                    //check for and get officials photo
                    String photo = null;
                    if(official.has("photoUrl")){
                        photo = official.getString("photoUrl");
                    }

                    //check for and get officials social media
                    String facebook = null;
                    String twitter = null;
                    String google = null;
                    String youtube = null;
                    if(official.has("channels")) {
                        JSONArray socialMedia = official.getJSONArray("channels");
                        for (int k = 0; k < socialMedia.length(); k++) {
                            JSONObject media = socialMedia.getJSONObject(k);
                            if(media.getString("type").equals("Facebook")){
                                facebook = media.getString("id");
                            } else if(media.getString("type").equals("Twitter")){
                                twitter = media.getString("id");
                            } else if(media.getString("type").equals("GooglePlus")){
                                google = media.getString("id");
                            } else if(media.getString("type").equals("YouTube")){
                                youtube = media.getString("id");
                            }
                        }
                    }

                    Official o = new Official(name, party, officeTitle, addressFinal, phone, website, email, photo, google, facebook, twitter, youtube);
                    os.add(o);

                }
            }
        } catch (Exception e){
            Log.d(TAG, "parseJSON: excpetion: "+e.getMessage());
        }
        for(Official o : os){
            Log.d(TAG, "parseJSON: "+o.toString());
        }
        return new Object[]{location, os};
    }
}
