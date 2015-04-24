package com.riis.towerpower.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.riis.towerpower.models.Consts;
import com.riis.towerpower.models.TowerContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * @author tkocikjr
 */
public class TowerPowerRetriever
{
    private static final String AVERAGE_RSSI_ASU = "averageRssiAsu";
    private static final String AVERAGE_RSSI_DB = "averageRssiDb";
    private static final String DOWNLOAD_SPEED = "downloadSpeed";
    private static final String NETWORK_NAME = "networkName";
    private static final String NETWORK_RANK = "networkRank";
    private static final String NETWORK_TYPE = "networkType";
    private static final String PING_TIME = "pingTime";
    private static final String SAMPLE_SIZE_RSSI = "sampleSizeRSSI";
    private static final String RELIABILITY = "reliability";
    private static final String UPLOAD_SPEED = "uploadSpeed";

    private Consts mConsts;
    private Context mContext;

    public TowerPowerRetriever(Context context)
    {
        mConsts = new Consts();
        mContext = context;
    }

    /**
     * @param latitude
     *      Latitude coordinate as a String
     * @param longitude
     *      Longitude coordinate as a String
     * @param distance
     *      Distance coordinate as a String
     * @return String
     *      JSon response as a String
     */
    public String send(String latitude, String longitude, String distance)
    {
        if(latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
        {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri uri = Uri.parse(mConsts.getTowerInformation(latitude, longitude, distance));

        try {
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String responseString = reader.readLine();
            reader.close();
            urlConnection.disconnect();

            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Save tower information from API to the database.
     *
     * @param json
     *      Web-Service Json response as a String
     * @param locationId
     *      Location's unique id.
     * @throws JSONException
     */
    public void getTowerPower(String json, long locationId) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject networkRankJson = jsonObject.getJSONObject(NETWORK_RANK);

        //Reference: http://stackoverflow.com/questions/7304002/how-to-parse-a-dynamic-json-key-in-a-nested-json-result
        Iterator keys = networkRankJson.keys();
        while(keys.hasNext())
        {
            String network = (String) keys.next();
            JSONObject networkObject = networkRankJson.getJSONObject(network);
            Iterator networkKeys = networkObject.keys();

            //Dynamically grab network types, instead of hard-coding 2G, 3G, etc.
            while(networkKeys.hasNext())
            {
                String networkType = (String) networkKeys.next();
                JSONObject towerObject = networkObject.getJSONObject(networkType);

                long towerId = addTower(towerObject);

                addLocationTower(towerId, locationId);
            }
        }
    }

    /**
     * Helper method to handle insertion of a new location.
     *
     * @param latitude the current latitude
     * @param longitude the current longitude
     * @return the row ID of the added location.
     */
    public long addLocation(double latitude, double longitude)
    {
        long locationId;

        Cursor locationCursor = mContext.getContentResolver().query(
                TowerContract.DbLocation.buildLocationUri(latitude, longitude), null, null, null, null);

        if (locationCursor.moveToFirst())
        {
            int locationIdIndex = locationCursor.getColumnIndex(TowerContract.DbLocation._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        }
        else
        {
            ContentValues locationValues = new ContentValues();

            locationValues.put(TowerContract.DbLocation.COLUMN_LONGITUDE, longitude);
            locationValues.put(TowerContract.DbLocation.COLUMN_LATITUDE, latitude);

            Uri insertedUri = mContext.getContentResolver().insert(
                    TowerContract.DbLocation.CONTENT_URI, locationValues
            );

            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Consts.getLatitude(), Double.toString(latitude));
        editor.putString(Consts.getLongitude(), Double.toString(longitude));
        editor.apply();

        return locationId;
    }

    /**
     * Helper method to handle insertion of a new tower.
     *
     * @param towerObject
     *      The JSON tower object
     * @return the row ID of the added location.
     */
    long addTower(JSONObject towerObject) throws JSONException
    {
        long towerId;

        ContentValues towerValues = new ContentValues();

        towerValues.put(TowerContract.DbTower.COLUMN_NAME,
                towerObject.getString(NETWORK_NAME));
        towerValues.put(TowerContract.DbTower.COLUMN_NETWORK_TYPE,
                towerObject.getString(NETWORK_TYPE));

        if(towerObject.has(AVERAGE_RSSI_ASU))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU,
                    towerObject.getString(AVERAGE_RSSI_ASU));
        }
        else
        {
            towerValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU, "");
        }

        if(towerObject.has(AVERAGE_RSSI_DB))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB,
                    towerObject.getString(AVERAGE_RSSI_DB));
        }
        else
        {
            towerValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB, "");
        }

        if(towerObject.has(SAMPLE_SIZE_RSSI))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI,
                    towerObject.getString(SAMPLE_SIZE_RSSI));
        }
        else
        {
            towerValues.put(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI, "");
        }

        if(towerObject.has(DOWNLOAD_SPEED))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED,
                    towerObject.getString(DOWNLOAD_SPEED));
        }
        else
        {
            towerValues.put(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED, "");
        }

        if(towerObject.has(UPLOAD_SPEED))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_UPLOAD_SPEED,
                    towerObject.getString(UPLOAD_SPEED));
        }
        else
        {
            towerValues.put(TowerContract.DbTower.COLUMN_UPLOAD_SPEED, "");
        }

        if(towerObject.has(PING_TIME))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_PING_TIME,
                    towerObject.getString(PING_TIME));
        }
        else
        {
            towerValues.put(TowerContract.DbTower.COLUMN_PING_TIME, "");
        }

        if(towerObject.has(RELIABILITY))
        {
            towerValues.put(TowerContract.DbTower.COLUMN_RELIABILITY,
                    towerObject.getString(RELIABILITY));
        }
        else{
            towerValues.put(TowerContract.DbTower.COLUMN_RELIABILITY, "");
        }

        Cursor towerCursor = getTowerCursor(towerValues);
        if(towerCursor.moveToFirst())
        {
            towerId = towerCursor.getLong(towerCursor.getColumnIndex(TowerContract.DbTower._ID));
        }
        else
        {
            Uri towerUri = mContext.getContentResolver().insert(TowerContract.DbTower.CONTENT_URI,
                    towerValues);

            towerId = Long.parseLong(TowerContract.DbTower.getIdFromUri(towerUri));
        }

        towerCursor.close();
        return towerId;
    }

    /**
     * Helper method to handle insertion of a new tower.
     *
     * @param towerId
     *      The unique tower id.
     * @param locationId
     *      The unique location id.
     * @return the row ID of the added location.
     */
    long addLocationTower(long towerId, long locationId)
    {
        ContentValues locationTowerValues = new ContentValues();
        locationTowerValues.put(TowerContract.DbLocationTower.COLUMN_LOCATION_ID, locationId);
        locationTowerValues.put(TowerContract.DbLocationTower.COLUMN_TOWER_ID, towerId);

        return Long.parseLong(TowerContract.DbLocationTower.getIdFromUri(mContext.getContentResolver()
                .insert(TowerContract.DbLocationTower.CONTENT_URI,
                        locationTowerValues)));
    }

    private Cursor getTowerCursor(ContentValues towerValues)
    {
        String selection = TowerContract.DbTower.COLUMN_NAME + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_NETWORK_TYPE + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_UPLOAD_SPEED + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_PING_TIME + " = ?"
                + " AND " + TowerContract.DbTower.COLUMN_RELIABILITY + " = ?";

        String[] selectionArgs = {
                towerValues.getAsString(TowerContract.DbTower.COLUMN_NAME),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_NETWORK_TYPE),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_UPLOAD_SPEED),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_PING_TIME),
                towerValues.getAsString(TowerContract.DbTower.COLUMN_RELIABILITY),
        };


        return mContext.getContentResolver().query(TowerContract.DbTower.CONTENT_URI, null,
                selection, selectionArgs, null);
    }
}
