package com.tiffinitobiasson.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;
import static com.tiffinitobiasson.knowyourgovernment.MainActivity.LOCATION_REQUEST_CODE;

/**
 * Created by tiffi on 4/14/2018.
 */

public class Locator {

    private MainActivity owner;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Locator(MainActivity activity) {
        owner = activity;

        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(owner, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(owner,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void setUpLocationManager() {
        if (!checkPermission())
            return;

        locationManager = (LocationManager) owner.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                owner.doLocationWork(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Nothing to do here
            }

            public void onProviderEnabled(String provider) {
                // Nothing to do here
            }

            public void onProviderDisabled(String provider) {
                // Nothing to do here
            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void determineLocation() {

        if (!checkPermission())
            return;

        if (locationManager == null)
            setUpLocationManager();

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(owner, "Network Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(owner, "Passive Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(owner, "GPS Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        owner.noLocationAvailable();
        return;
    }

    public void shutdown() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
}
