package com.tiffinitobiasson.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerview;
    private List<Official> officialList;
    private OfficialAdapter oAdapter;
    private TextView location;
    private String cityZip;
    private Locator locator;
    public static int LOCATION_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = (TextView) findViewById(R.id.location);
        locator = new Locator(this);
        if(checkConnection()==false){
            noNetworkConnection();
        }

        officialList = new ArrayList<>();
        recyclerview = (RecyclerView) findViewById(R.id.OfficialRecycler);
        oAdapter = new OfficialAdapter(officialList, this);

        recyclerview.setAdapter(oAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View view) {
        //Will open the activity for the office selected
        int pos = recyclerview.getChildLayoutPosition(view);
        Official official = officialList.get(pos);
        Intent officialActivity = new Intent(this, OfficialActivity.class);
        officialActivity.putExtra("heading", location.getText());
        officialActivity.putExtra("official", official);
        startActivity(officialActivity);
    }

    @Override
    public boolean onLongClick(View view) {
        onClick(view);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.about:
                Intent abt = new Intent(this, About.class);
                startActivity(abt);
                return true;
            case R.id.zip:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                final MainActivity ma = this;
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                et.setGravity(Gravity.CENTER_HORIZONTAL);

                builder.setView(et);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cityZip = et.getText().toString();
                        //location.setText(cityZip);
                        //Call the Civic Info Downloader on the cityZip given
                        new CivicInfoDownloader(ma).execute(cityZip);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                builder.setMessage("Please enter a city & state or zip code");
                builder.setTitle("Search Location");

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locator.setUpLocationManager();
                        locator.determineLocation();
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public void doLocationWork(double latitude, double longitude) {
        List<Address> locations = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            locations = geocoder.getFromLocation(latitude, longitude, 1);
            cityZip = locations.get(0).getPostalCode();
            //Call the Civic Info Downloader on the cityZip obtained
            new CivicInfoDownloader(this).execute(cityZip);
        } catch (IOException e) {
            Toast.makeText(this, "Address can not be acquired from given Longitude/Latitude", Toast.LENGTH_SHORT).show();
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }

    public void setOfficialList(Object[] parsed) {
        if(parsed != null) {
            cityZip = (String) parsed[0];
            officialList.clear();
            ArrayList<Official> officialsParsed = (ArrayList<Official>) parsed[1];
            for(Official o : officialsParsed){
                officialList.add(o);
            }

            oAdapter.notifyDataSetChanged();
            location.setText(cityZip);
            Log.d(TAG, "setOfficialList: "+officialList.toString());
        } else {
            noNetworkConnection();
        }
    }

    private boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else {
            return false;
        }
    }

    private void noNetworkConnection(){
        location.setText("No Data For Location");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });


        builder.setMessage("Data cannot be accessed/loaded without a network connection.");
        builder.setTitle("No Network Connection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
