package com.riis.towerpower.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.riis.towerpower.models.TowerContract;
import com.riis.towerpower.models.Consts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
     * @param json
     *      Web-Service Json response as a String
     * @return String[]
     *      An array Strings from the Json response
     * @throws JSONException
     */
    public void getTowerPower(String json) throws JSONException
    {
        ArrayList<ContentValues> bulkTowerValues = new ArrayList<>();

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
                ContentValues towerValues = new ContentValues();

                towerValues.put(TowerContract.DbTower.COLUMN_NAME, towerObject.getString(NETWORK_NAME));
//                towerValues.put(TowerContract.DbTower.COLUMN_NETWORK_TYPE, towerObject.getString(NETWORK_TYPE));
                towerValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU, towerObject.getString(AVERAGE_RSSI_ASU));
                towerValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB, towerObject.getString(AVERAGE_RSSI_DB));
                towerValues.put(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI, towerObject.getString(SAMPLE_SIZE_RSSI));
                towerValues.put(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED, towerObject.getString(DOWNLOAD_SPEED));
                towerValues.put(TowerContract.DbTower.COLUMN_UPLOAD_SPEED, towerObject.getString(UPLOAD_SPEED));
                towerValues.put(TowerContract.DbTower.COLUMN_PING_TIME, towerObject.getString(PING_TIME));
                towerValues.put(TowerContract.DbTower.COLUMN_RELIABILITY, towerObject.getString(RELIABILITY));

                bulkTowerValues.add(towerValues);
            }
        }

        if(bulkTowerValues.size() > 0)
        {
            ContentValues[] cvArray = new ContentValues[bulkTowerValues.size()];
            bulkTowerValues.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(TowerContract.DbTower.CONTENT_URI, cvArray);
        }
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param latitude the latitude of the city
     * @param longitude the longitude of the city
     * @return the row ID of the added location.
     */
    long addLocation(double latitude, double longitude)
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
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            locationValues.put(TowerContract.DbLocation.COLUMN_LONGITUDE, longitude);
            locationValues.put(TowerContract.DbLocation.COLUMN_LATITUDE, latitude);

            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    TowerContract.DbLocation.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        return locationId;
    }
}
