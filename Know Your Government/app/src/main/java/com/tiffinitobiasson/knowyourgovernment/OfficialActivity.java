package com.tiffinitobiasson.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "OfficialActivity";

    private Official official;
    private String location;
    private TextView office, name, party, address, phone, email, website, header;
    private ImageButton picture, facebook, twitter, youtube, googleplus;
    private ConstraintLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        header = (TextView) findViewById(R.id.location);
        office = (TextView) findViewById(R.id.Office);
        name = (TextView) findViewById(R.id.Name);
        party = (TextView) findViewById(R.id.Party);
        address = (TextView) findViewById(R.id.Address);
        phone = (TextView) findViewById(R.id.Phone);
        email = (TextView) findViewById(R.id.Email);
        website = (TextView) findViewById(R.id.Website);
        picture = (ImageButton) findViewById(R.id.Picture);
        facebook = (ImageButton) findViewById(R.id.Facebook);
        twitter = (ImageButton) findViewById(R.id.Twitter);
        youtube = (ImageButton) findViewById(R.id.Youtube);
        googleplus = (ImageButton) findViewById(R.id.GooglePlus);
        background = (ConstraintLayout) findViewById(R.id.Official_Background);

        Intent intent = getIntent();
        if(intent.hasExtra("heading")){
            location = intent.getStringExtra("heading");
            header.setText(location);
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");
            Log.d(TAG, "onCreate: "+official.toString());
            setData(official);
            loadImage();
        }
    }

    private void setData(Official o){
        office.setText(o.getOffice());
        name.setText(o.getName());
        party.setText("("+o.getParty()+")");
        if(o.getAddress()==null){
            address.setText("No Address Provided");
        } else {
            address.setText(o.getAddress());
            Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
            address.setLinkTextColor(Color.WHITE);
        }
        if(o.getPhone()==null){
            phone.setText("No Phone Provided");
        } else {
            phone.setText(o.getPhone());
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            phone.setLinkTextColor(Color.WHITE);
        }
        if(o.getEmail()==null){
            email.setText("No Email Provided");
        } else {
            email.setText(o.getEmail());
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            email.setLinkTextColor(Color.WHITE);
        }
        if(o.getWebsite()==null){
            website.setText("No Website Provided");
        } else {
            website.setText(o.getWebsite());
            Linkify.addLinks(website, Linkify.WEB_URLS);
            website.setLinkTextColor(Color.WHITE);
        }

        if(o.getFacebook()==null){
            facebook.setVisibility(View.INVISIBLE);
            facebook.setClickable(false);
        }
        if(o.getTwitter()==null){
            twitter.setVisibility(View.INVISIBLE);
            twitter.setClickable(false);
        }
        if(o.getGooglePlus()==null){
            googleplus.setVisibility(View.INVISIBLE);
            googleplus.setClickable(false);
        }
        if(o.getYoutube()==null){
            youtube.setVisibility(View.INVISIBLE);
            youtube.setClickable(false);
        }

        if(o.getParty().contains("Republican")){
            background.setBackgroundColor(Color.RED);
        } else if(o.getParty().contains("Democrat") || o.getParty().contains("Democratic")){
            background.setBackgroundColor(Color.BLUE);
        } else {
            background.setBackgroundColor(Color.BLACK);
        }
    }

    private void loadImage(){
        if (official.getPhoto() != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = official.getPhoto().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerInside()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(picture);
                }
            }).build();
            picasso.load(official.getPhoto())
                    .fit()
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(picture);
        } else {
            Picasso.get().load(R.drawable.missingimage)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(picture);
        }

    }

    public void openPhotoActivity(View v){
        if(official.getPhoto()==null){
            //Do nothing
        } else {
            Intent photointent = new Intent(this, PhotoActivity.class);
            photointent.putExtra("official",official);
            photointent.putExtra("header",header.getText().toString());
            startActivity(photointent);
        }
    }

    public void facebookClick(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClick(View v){
        Intent intent = null;
        String name = official.getTwitter();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);

    }

    public void googleClick(View v){
        String name = official.getGooglePlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youtubeClick(View v){
        String name = official.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }

    }
}
