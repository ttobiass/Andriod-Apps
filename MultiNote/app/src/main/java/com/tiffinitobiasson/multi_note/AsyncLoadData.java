package com.tiffinitobiasson.multi_note;

import android.os.AsyncTask;
import android.util.JsonReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class AsyncLoadData extends AsyncTask<String, Void, ArrayList<Note>>{
    private MainActivity mainAct;

    public AsyncLoadData(MainActivity ma) {
        mainAct = ma;
    }

    @Override
    protected ArrayList<Note> doInBackground(String... strings) {
        //Open the JSON file and read the contents to arraylist
        ArrayList<Note> nl;
        nl = loadNotes(strings[0]);
        return nl;
    }

    @Override
    protected void onPreExecute() {
        // This method is used infrequently
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Note> notes) {
        // This method is almost always used
        super.onPostExecute(notes);

        //call method to send arraylist to the
        mainAct.whenAsyncIsDone(notes);
    }

    private ArrayList<Note> loadNotes(String filename){
        ArrayList<Note> jsonContents = new ArrayList<Note>();
        try {
            InputStream is = mainAct.getApplicationContext().openFileInput(filename);
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

            reader.beginArray();
            while (reader.hasNext()) {
                String title = null;
                String date = null;
                String input = null;

                reader.beginObject();
                while(reader.hasNext()){
                    String name = reader.nextName();
                    if(name.equals("Title")){
                        title = reader.nextString();
                    }
                    else if (name.equals("Date")){
                        date = reader.nextString();
                    }
                    else if (name.equals("Input")){
                        input = reader.nextString();
                    }
                    else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                jsonContents.add(new Note(title, date, input));
            }
            reader.endArray();
            reader.close();
            is.close();
            return jsonContents;

        } catch (FileNotFoundException e) {
            return jsonContents;
        } catch (Exception e) {
            e.printStackTrace();
            return jsonContents;
        }
    }
}
