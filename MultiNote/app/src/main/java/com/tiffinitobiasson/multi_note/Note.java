package com.tiffinitobiasson.multi_note;

import java.io.Serializable;

public class Note implements Serializable {
    private String title="";
    private String date="";
    private String input="";

    public Note(String t, String d, String i){
        title = t;
        date = d;
        input = i;
    }
    public Note (){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getPreview() {
        if(input.length()>80){
            return (input.substring(0, 80)+"...");
        }
        else {
            return input;
        }

    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", input='" + input + '\'' +
                '}';
    }
}
