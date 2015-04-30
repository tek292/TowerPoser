package com.riis.towerpower.models;

/**
 * @author tkocikjr
 */
public class Consts
{
    private static final double KILOMETER_TO_MILE_CONVERSION = .621371;
    private static final double MILE_TO_KILOMETER_CONVERSION = 1.60934;
    private static final String API_KEY = "d2ad6d01cd5a26f9bff1f51f5be7affd";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private String getDefaultUrl()
    {
        return "http://api.opensignal.com/v2/networkstats.json?";
    }

    private String getApiKey()
    {
        return "&apikey=" + API_KEY;
    }

    /**
     * @param lat
     *  Latitude value as a String
     * @param lon
     *  Longitude value as a String
     * @param distance
     *  Distance Value as a String
     * @return
     *  URL as a string value to retrieve Cell Tower Information
     */
    public String getTowerInformation(String lat, String lon, String distance)
    {
        return getDefaultUrl() + "lat=" + lat + "&lng=" + lon + "&distance="+ distance + getApiKey();
    }

    public static double convertKilometersToMiles(Double kilometerValue)
    {
        return kilometerValue * KILOMETER_TO_MILE_CONVERSION;
    }

    public static double convertMilesToKilometers(Double mileValue)
    {
        return mileValue * MILE_TO_KILOMETER_CONVERSION;
    }

    public static String getLatitude()
    {
        return LATITUDE;
    }

    public static String getLongitude()
    {
        return LONGITUDE;
    }
}
