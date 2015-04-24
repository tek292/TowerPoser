package com.riis.towerpower.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Consts;
import com.riis.towerpower.models.TowerContract;

import org.json.JSONException;

/**
 * @author tkocikjr
 */
public class TowerRetrievalTask extends AsyncTask<Void, Void, Void>
{
    private Context mContext;
    private Location mLocation;
    private LocationManager mLocationManager;
    private ProgressDialog mProgressDialog;
//    public VeggsterLocationListener mVeggsterLocationListener;
    public double lati = 0.0;
    public double longi = 0.0;

    public TowerRetrievalTask(Context context)
    {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        mVeggsterLocationListener = new VeggsterLocationListener();
//        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
//
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
//                mVeggsterLocationListener);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.retrieving_tower_info));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String distance = prefs.getString(mContext.getString(R.string.pref_distance_key),
                mContext.getString(R.string.pref_distance_default));

        Uri uri = TowerContract.DbLocationTower.buildLocationToTowerWithCoordinates(
                Double.parseDouble(prefs.getString(Consts.getLatitude(), "37.7907")),
                Double.parseDouble(prefs.getString(Consts.getLongitude(), "-122.4058")));

        Cursor cursor = mContext.getContentResolver().query(uri,
                null, null, null, null);
        cursor.moveToFirst();

        Double latitude = cursor.getDouble(cursor.getColumnIndex(TowerContract.DbLocation.COLUMN_LATITUDE));
        Double longitude = cursor.getDouble(cursor.getColumnIndex(TowerContract.DbLocation.COLUMN_LONGITUDE));

        TowerPowerRetriever retriever = new TowerPowerRetriever(mContext);
        String response = retriever.send(latitude.toString(), longitude.toString(), distance);
        try
        {
                retriever.getTowerPower(response, cursor.getLong(0));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        cursor.close();


        // Reference: http://stackoverflow.com/questions/4152373/how-to-know-location-area-code-and-cell-id-in-android-phone
//            final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
//                final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
//                if (location != null) {
//                    Log.w("TESTING", "LAC: " + location.getLac() + " CID: " + location.getCid());
//                }
//            }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mProgressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressDialog.dismiss();
    }

//    public class VeggsterLocationListener implements LocationListener {
//
//        @Override
//        public void onLocationChanged(Location location) {
//            try {
////                mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//                boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//                if (!isGPSEnabled && !isNetworkEnabled)
//                {
////                    showSettingsAlert();
//                }
//                else
//                {
////                    this.mCanGetLocation = true;
//                    // First get location from Network Provider
//                    if (isNetworkEnabled)
//                    {
//                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                        if (mLocationManager != null)
//                        {
//                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                            if (mLocation != null) {
//                                mLatitude = mLocation.getLatitude();
//                                mLongitude = mLocation.getLongitude();
//
//                                new TowerPowerRetriever(mContext).addLocation(mLatitude, mLongitude);
//                            }
//                        }
//                    }
//
//                    // if GPS Enabled get lat/long using GPS Services
//                    if (isGPSEnabled)
//                    {
//                        if (mLocation == null)
//                        {
//                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                                    MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                            if (mLocationManager != null)
//                            {
//                                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                                if (mLocation != null) {
//                                    mLatitude = mLocation.getLatitude();
//                                    mLongitude = mLocation.getLongitude();
//
//                                    new TowerPowerRetriever(mContext).addLocation(mLatitude, mLongitude);
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//
//    }
}
