package com.riis.towerpower.util;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.riis.towerpower.models.Consts;
import com.riis.towerpower.models.Tower;

import org.json.JSONArray;
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
    private final String NETWORK_RANK = "networkRank";

    private Consts mConsts;

    public TowerPowerRetriever()
    {
        mConsts = new Consts();
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
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri uri = Uri.parse(mConsts.getTowerInformation(latitude, longitude));
        String responseString = null;

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
            urlConnection.disconnect();
            responseString = reader.readLine();
            reader.close();

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
    public String[] getTowerPower(String json) throws JSONException
    {
        ArrayList<Tower> towerList = new ArrayList<>();
        JSONObject networkRankJson = new JSONObject(NETWORK_RANK);

        //Reference: http://stackoverflow.com/questions/7304002/how-to-parse-a-dynamic-json-key-in-a-nested-json-result
        Iterator keys = networkRankJson.keys();

        while(keys.hasNext())
        {
            String network = (String) keys.next();
            JSONObject networkObject = networkRankJson.getJSONObject(network);
            Iterator networkKeys = networkObject.keys();

            //Dynamically grab network types, instead of hard-coding 2G, 3G, etc.
            while(keys.hasNext())
            {
                String networkType = (String) networkKeys.next();
                JSONObject towerObject = networkObject.getJSONObject(networkType);

                Tower tower = new Tower(towerObject.getString("networkName"), towerObject.getInt("networkType"));
                towerList.add(tower);
            }
        }

//        JSONArray weatherArray = networkRankJson.get;

//        String[] resultStrs = new String[numDays];

        // Data is fetched in Celsius by default.
        // If user prefers to see in Fahrenheit, convert the values here.
        // We do this rather than fetching in Fahrenheit so that the user can
        // change this option without us having to re-fetch the data once
        // we start storing the values in a database.
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//        String unitType = sharedPrefs.getString(mContext.getString(R.string.pref_units_key),
//                mContext.getString(R.string.pref_units_metric));
//
//        for(int i = 0; i < weatherArray.length(); i++)
//        {
//            // For now, using the format "Day, description, hi/low"
//            String day;
//            String description;
//            String highAndLow;
//
//            // Get the JSON object representing the day
//            JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//            // The date/time is returned as a long.  We need to convert that
//            // into something human-readable, since most people won't read "1400356800" as
//            // "this saturday".
//            long dateTime;
//            // Cheating to convert this to UTC time, which is what we want anyhow
//            dateTime = dayTime.setJulianDay(julianStartDay+i);
//            day = getReadableDateString(dateTime);
//
//            // description is in a child array called "weather", which is 1 element long.
//            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//            description = weatherObject.getString(OWM_DESCRIPTION);
//
//            // Temperatures are in a child object called "temp".  Try not to name variables
//            // "temp" when working with temperature.  It confuses everybody.
//            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//            double high = temperatureObject.getDouble(OWM_MAX);
//            double low = temperatureObject.getDouble(OWM_MIN);
//
//            highAndLow = formatHighLows(high, low, unitType);
//            resultStrs[i] = day + " - " + description + " - " + highAndLow;
//        }
//
//        return resultStrs;
        return null;
    }
}
