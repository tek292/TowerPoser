package com.riis.towerpower.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TowerContract
{
    public static final String CONTENT_AUTHORITY = "com.riis.towerpower";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TOWER = "tower";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_LOCATION_TO_TOWER = "location_to_tower";

    public static class DbTower implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOWER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOWER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOWER;

        public static final String TABLE_NAME = "tower";

        public static final String COLUMN_AVERAGE_RSSI_ASU = "_average_rssi_asu";
        public static final String COLUMN_AVERAGE_RSSI_DB = "_average_rssi_db";
        public static final String COLUMN_DOWNLOAD_SPEED = "_download_speed";
        public static final String COLUMN_NAME = "_name";
        public static final String COLUMN_NETWORK_TYPE = "_network_type_id";
        public static final String COLUMN_PING_TIME = "_ping_time";
        public static final String COLUMN_RELIABILITY = "_reliability";
        public static final String COLUMN_SAMPLE_SIZE_RSSI = "_sample_size_rssi";
        public static final String COLUMN_UPLOAD_SPEED = "_upload_speed";

        public static Uri buildTowerUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        protected static String createTable()
        {
            return "CREATE TABLE "+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_RELIABILITY + " REAL, "
                    + COLUMN_PING_TIME + " REAL, " + COLUMN_UPLOAD_SPEED + " REAL, "
                    + COLUMN_DOWNLOAD_SPEED + " REAL, " + COLUMN_SAMPLE_SIZE_RSSI + " INTEGER, "
                    + COLUMN_AVERAGE_RSSI_ASU + " REAL, " + COLUMN_AVERAGE_RSSI_DB + " REAL, "
                    + COLUMN_NETWORK_TYPE + " INTEGER);";
        }
    }

    public static class DbLocation implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LATITUDE = "_latitude";
        public static final String COLUMN_LONGITUDE = "_longitude";

        public static Uri buildLocationUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildLocationUri(double latitude, double longitude)
        {
            return CONTENT_URI.buildUpon().appendPath(Double.toString(latitude))
                    .appendPath(Double.toString(longitude)).build();
        }

        public static String[] getLatitudeLongitudeFromUri(Uri uri)
        {
            String[] coordinates = new String[2];
            coordinates[0] = uri.getPathSegments().get(1);
            coordinates[1] = uri.getPathSegments().get(2);
            return coordinates;
        }

        protected static String createTable()
        {
            return "CREATE TABLE "+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_LATITUDE + " REAL, " + COLUMN_LONGITUDE + " REAL, "
                    + "UNIQUE (" + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE
                    + ") ON CONFLICT IGNORE);";
        }
    }

    public static class DbLocationTower implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_TO_TOWER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION_TO_TOWER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION_TO_TOWER;

        public static final String TABLE_NAME = "location_to_tower";

        public static final String COLUMN_LOCATION_ID = "_location_id";
        public static final String COLUMN_TOWER_ID = "_tower_id";

        public static Uri buildLocationToTower(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildLocationToTower(long towerId, long locationid)
        {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(locationid))
                    .appendPath(Long.toString(towerId)).build();
        }

        public static String getIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static String[] getLocationToTowerFromUri(Uri uri)
        {
            String[] ids = new String[2];
            ids[0] = uri.getPathSegments().get(1);
            ids[1] = uri.getPathSegments().get(2);
            return ids;
        }

        protected static String createTable()
        {
            return "CREATE TABLE "+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_LOCATION_ID + " INTEGER NOT NULL, "
                    + COLUMN_TOWER_ID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + COLUMN_LOCATION_ID + ") REFERENCES "
                    + DbLocation.TABLE_NAME + " (" + DbLocation._ID + "), "
                    + "FOREIGN KEY (" + COLUMN_TOWER_ID + ") REFERENCES "
                    + DbTower.TABLE_NAME + " (" + DbTower._ID + "), "
                    + "UNIQUE (" + COLUMN_LOCATION_ID + ", " + COLUMN_TOWER_ID
                    + ") ON CONFLICT REPLACE);";
        }
    }
}
