package com.sourcey.materiallogindemo;

/**
 * Created by Nick on 11/5/2015.
 * A class which uses the user's GPS services to determine their location
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSTracker extends Service implements LocationListener {

    //the content being used
    private final Context context;

    //booleans for if the location can be found
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    //the user's location
    Location location;

    //the user's long/lat values
    double latitude;
    double longitude;

    //minimum time/distance changes before update to location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 60000;

    protected LocationManager locationManager;

    //create a new tracker and attempts to retrieve location
    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    //attempts to get and return the user's location
    public Location getLocation() {
        try {
            //check to see if the gps and network are available
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                //if neither is available then do nothing
            } else {
                //if one is available then the location can be determined
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    //if the network is available then use it first
                    //request location updates
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        //if the location manager is now null then use the last known location from the network
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            //if the location is not null then set the lat/long
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

                if(isGPSEnabled) {
                    if(location == null) {
                        //if the GPS is available then request location updates
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if(locationManager != null) {
                            //if the location manager is now null then use the last known location from the GPS
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(location != null) {
                                //if the location is not null then set the lat/long
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //if something fails then print the error
            e.printStackTrace();
        }

        return location;
    }

    //return the latitude if not null
    public double getLatitude() {
        if(location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //return the longitude if not null
    public double getLongitude() {
        if(location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    //return if the location can be determined
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    //show the alert option screen if the location can not be determined
    public void showSettingsAlert() {
        //create an alert with a title and options
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Location settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        //open the location settings if the user clicks the settings options
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        //close if the user clicks cancel
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location arg0) {

    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

