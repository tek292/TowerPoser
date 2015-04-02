package com.riis.towerpower.util;

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
import android.util.Log;

import com.riis.towerpower.R;

/**
 * Reference: http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 * Edits made by tkocikjr
 */
public class GPSTrackerService extends Service implements LocationListener
{
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000; // 10 kilometers

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private boolean mCanGetLocation = false;
    private Context mContext;
    private double mLatitude;
    private double mLongitude;
    private Location mLocation;
    private LocationManager mLocationManager;

    public GPSTrackerService(Context context)
    {
        mContext = context;
        getLocation();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
            {
                showSettingsAlert();
            }
            else
            {
                this.mCanGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled)
                {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (mLocationManager != null)
                    {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled)
                {
                    if (mLocation == null)
                    {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null)
                        {
                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return mLocation;
    }

    /**
     * Function to get latitude
     * @return double
     */
    public double getLatitude()
    {
        if(mLocation != null)
        {
            mLatitude = mLocation.getLatitude();
        }

        return mLatitude;
    }

    /**
     * Function to get longitude
     * @return double
     */
    public double getLongitude()
    {
        if(mLocation != null)
        {
            mLongitude = mLocation.getLongitude();
        }

        return mLongitude;
    }

    /**
     * Function to check if best network provider
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.mCanGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(mContext.getString(R.string.gps_not_enabled_title));
        alertDialog.setMessage(mContext.getString(R.string.gps_not_enabled_message));

        alertDialog.setPositiveButton(mContext.getString(R.string.action_settings),
                new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(mContext.getString(android.R.string.cancel), null);

        alertDialog.show();
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS()
    {
        if(mLocationManager != null){
            mLocationManager.removeUpdates(this);
        }
    }
}
