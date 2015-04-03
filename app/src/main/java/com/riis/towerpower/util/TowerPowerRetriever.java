package com.riis.towerpower.util;

import android.net.Uri;

import com.riis.towerpower.models.Consts;
import com.riis.towerpower.models.Tower;

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
        if(latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
        {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri uri = Uri.parse(mConsts.getTowerInformation(latitude, longitude));

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
    public ArrayList<Tower> getTowerPower(String json) throws JSONException
    {
        ArrayList<Tower> towerList = new ArrayList<>();
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

                Tower tower = new Tower(towerObject.getString(NETWORK_NAME), towerObject.getInt(NETWORK_TYPE));

                if(!towerObject.isNull(AVERAGE_RSSI_ASU))
                {
                    tower.setAverageRSSIAsu(towerObject.getDouble(AVERAGE_RSSI_ASU));
                }

                if(!towerObject.isNull(AVERAGE_RSSI_DB))
                {
                    tower.setAverageRSSIDb(towerObject.getDouble(AVERAGE_RSSI_DB));
                }

                if(!towerObject.isNull(SAMPLE_SIZE_RSSI))
                {
                    tower.setSampleSizeRSSI(towerObject.getDouble(SAMPLE_SIZE_RSSI));
                }

                if(!towerObject.isNull(DOWNLOAD_SPEED))
                {
                    tower.setDownloadSpeed(towerObject.getDouble(DOWNLOAD_SPEED));
                }

                if(!towerObject.isNull(UPLOAD_SPEED))
                {
                    tower.setUploadSpeed(towerObject.getDouble(UPLOAD_SPEED));
                }

                if(!towerObject.isNull(PING_TIME))
                {
                    tower.setPingTime(towerObject.getDouble(PING_TIME));
                }

                if(!towerObject.isNull(RELIABILITY))
                {
                    tower.setReliability(towerObject.getDouble(RELIABILITY));
                }

                towerList.add(tower);
            }
        }

        return towerList;
    }
}
