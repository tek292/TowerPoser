package com.riis.towerpower.models;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TowerProvider extends ContentProvider
{
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    public static final int LOCATION = 100;
    public static final int LOCATION_WITH_COORDINATES = 101;
    public static final int TOWER = 200;
    public static final int TOWER_WITH_ID = 201;
    public static final int LOCATION_TO_TOWER = 300;
    public static final int LOCATION_TO_TOWER_WITH_ID = 301;
    public static final int LOCATION_TO_TOWER_WITH_COORDINATES = 302;

    private TowerDbHelper mTowerDbHelper;

    SQLiteQueryBuilder mLocationQueryBuilder = new SQLiteQueryBuilder();

    @Override
    public boolean onCreate()
    {
        mTowerDbHelper = new TowerDbHelper(getContext());

        mLocationQueryBuilder.setTables(
                TowerContract.DbLocationTower.TABLE_NAME + " INNER JOIN " + TowerContract.DbTower.TABLE_NAME
                + " ON "
                + TowerContract.DbLocationTower.TABLE_NAME + "." + TowerContract.DbLocationTower.COLUMN_TOWER_ID
                + " = "
                + TowerContract.DbTower.TABLE_NAME + "." + TowerContract.DbTower._ID + " "
                + " INNER JOIN " + TowerContract.DbLocation.TABLE_NAME
                + " ON "
                + TowerContract.DbLocation.TABLE_NAME + "." + TowerContract.DbLocation._ID
                + " = "
                + TowerContract.DbLocationTower.TABLE_NAME + "."
                + TowerContract.DbLocationTower.COLUMN_LOCATION_ID);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        switch (mUriMatcher.match(uri))
        {
            case LOCATION:
                retCursor = mTowerDbHelper.getReadableDatabase().query(TowerContract.DbLocation.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LOCATION_WITH_COORDINATES:
                retCursor = getLocationByCoordinates(uri, projection, sortOrder);
                break;
            case TOWER:
                retCursor = mTowerDbHelper.getReadableDatabase().query(TowerContract.DbTower.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TOWER_WITH_ID:
                retCursor = getTowerById(uri, projection, sortOrder);
                break;
            case LOCATION_TO_TOWER:
                retCursor = mTowerDbHelper.getReadableDatabase().query(TowerContract.DbLocationTower.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LOCATION_TO_TOWER_WITH_ID:
                retCursor = getLocationToTowerById(uri, projection, sortOrder);
                break;
            case LOCATION_TO_TOWER_WITH_COORDINATES:
                retCursor = getLocationToTowerByCoordinates(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = mUriMatcher.match(uri);

        switch (match)
        {
            case LOCATION:
                return TowerContract.DbLocation.CONTENT_TYPE;
            case LOCATION_WITH_COORDINATES:
                return TowerContract.DbLocation.CONTENT_ITEM_TYPE;
            case TOWER:
                return TowerContract.DbTower.CONTENT_TYPE;
            case TOWER_WITH_ID:
                return TowerContract.DbTower.CONTENT_ITEM_TYPE;
            case LOCATION_TO_TOWER:
                return TowerContract.DbLocationTower.CONTENT_TYPE;
            case LOCATION_TO_TOWER_WITH_ID:
                return TowerContract.DbLocationTower.CONTENT_ITEM_TYPE;
            case LOCATION_TO_TOWER_WITH_COORDINATES:
                return TowerContract.DbLocationTower.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mTowerDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        long id;
        switch (match)
        {
            case LOCATION:
                id = db.insert(TowerContract.DbLocation.TABLE_NAME, null, values);
                if(id > 0)
                {
                    returnUri = TowerContract.DbLocation.buildLocationUri(id);
                }
                else
                {
                    String selection =
                            TowerContract.DbLocation.TABLE_NAME + "."
                                    + TowerContract.DbLocation.COLUMN_LATITUDE + " = ?"
                                    + " AND " + TowerContract.DbLocation.TABLE_NAME + "."
                                    + TowerContract.DbLocation.COLUMN_LONGITUDE + " = ?";

                    String[] selectionArgs = {values.getAsString(TowerContract.DbLocation.COLUMN_LATITUDE),
                            values.getAsString(TowerContract.DbLocation.COLUMN_LONGITUDE)};

                    Cursor cursor = query(uri, new String[] {TowerContract.DbLocation._ID},
                            selection, selectionArgs, null);
                    cursor.moveToFirst();
                    id = cursor.getLong(0);
                    cursor.close();
                    returnUri = TowerContract.DbLocation.buildLocationUri(id);
                }
                break;
            case TOWER:
                id = db.insert(TowerContract.DbTower.TABLE_NAME, null, values);
                if(id > 0)
                {
                    returnUri = TowerContract.DbTower.buildTowerUri(id);
                }
                else
                {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case LOCATION_TO_TOWER:
                id = db.insert(TowerContract.DbLocationTower.TABLE_NAME, null, values);
                if(id > 0)
                {
                    returnUri = TowerContract.DbLocationTower.buildLocationToTower(id);
                }
                else
                {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mTowerDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        int rowsDeleted;
        if(selection == null)
        {
            selection = "1";
        }

        switch (match)
        {
            case LOCATION:
                rowsDeleted = db.delete(TowerContract.DbLocation.TABLE_NAME, selection, selectionArgs);
                break;
            case TOWER:
                rowsDeleted = db.delete(TowerContract.DbTower.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION_TO_TOWER:
                rowsDeleted = db.delete(TowerContract.DbLocationTower.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mTowerDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        int rowsUpdated;
        switch (match)
        {
            case TOWER:
                rowsUpdated = db.update(TowerContract.DbTower.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(TowerContract.DbLocation.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION_TO_TOWER:
                rowsUpdated = db.update(TowerContract.DbLocationTower.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TowerContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TowerContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, TowerContract.PATH_LOCATION + "/*/*", LOCATION_WITH_COORDINATES);

        matcher.addURI(authority, TowerContract.PATH_TOWER, TOWER);
        matcher.addURI(authority, TowerContract.PATH_TOWER + "/*", TOWER_WITH_ID);

        matcher.addURI(authority, TowerContract.PATH_LOCATION_TO_TOWER, LOCATION_TO_TOWER);
        matcher.addURI(authority, TowerContract.PATH_LOCATION_TO_TOWER + "/*", LOCATION_TO_TOWER_WITH_ID);
        matcher.addURI(authority, TowerContract.PATH_LOCATION_TO_TOWER + "/*/*", LOCATION_TO_TOWER_WITH_COORDINATES);

        return matcher;
    }

    private Cursor getLocationByCoordinates(Uri uri, String[] projection, String sortOrder)
    {
        String selection =
                TowerContract.DbLocation.TABLE_NAME + "."
                        + TowerContract.DbLocation.COLUMN_LATITUDE + " = ?"
                        + " AND " + TowerContract.DbLocation.TABLE_NAME + "."
                        + TowerContract.DbLocation.COLUMN_LONGITUDE + " = ?";
        String[] coordinates = TowerContract.DbLocation.getLatitudeLongitudeFromUri(uri);

        return mTowerDbHelper.getReadableDatabase().query(TowerContract.DbLocation.TABLE_NAME,
                projection, selection, coordinates, null, null, sortOrder);
    }

    private Cursor getTowerById(Uri uri, String[] projection, String sortOrder)
    {
        String selection = TowerContract.DbTower.TABLE_NAME + "." + TowerContract.DbTower._ID + " = ?";
        String id = TowerContract.DbTower.getIdFromUri(uri);
        return mTowerDbHelper.getReadableDatabase().query(TowerContract.DbTower.TABLE_NAME,
                projection, selection, new String[] {id}, null, null, sortOrder);
    }

    private Cursor getLocationToTowerById(Uri uri, String[] projection, String sortOrder)
    {
        String selection = TowerContract.DbLocationTower.TABLE_NAME + "."
                + TowerContract.DbLocationTower._ID + " = ?";
        String id = TowerContract.DbLocationTower.getIdFromUri(uri);
        return mTowerDbHelper.getReadableDatabase().query(TowerContract.DbTower.TABLE_NAME,
                projection, selection, new String[] {id}, null, null, sortOrder);
    }

    private Cursor getLocationToTowerByCoordinates(Uri uri, String[] projection, String sortOrder)
    {
        String selection =
                TowerContract.DbLocation.TABLE_NAME + "."
                        + TowerContract.DbLocation.COLUMN_LATITUDE + "=?"
                        + " AND " + TowerContract.DbLocation.TABLE_NAME + "."
                        + TowerContract.DbLocation.COLUMN_LONGITUDE + "=?";
        String[] coordinates = TowerContract.DbLocationTower.getLocationToTowerFromUri(uri);

        return mLocationQueryBuilder.query(mTowerDbHelper.getReadableDatabase(), projection, selection,
                coordinates, null, null, sortOrder);
    }
}
