package com.riis.towerpower.models;

/**
 * @author tkocikjr
 */
public class Consts {
    private static final String API_KEY = "d2ad6d01cd5a26f9bff1f51f5be7affd";

    private String getDefaultUrl() {
        return "http://api.opensignal.com/v2/networkstats.json?";
    }

    private String getApiKey() {
        return "&apikey=" + API_KEY;
    }

    public String getTowerInformation(String lat, String lon) {
        return getDefaultUrl() + "lat=" + lat + "&lng=" + lon + "&distance=10" + getApiKey();
    }
}
