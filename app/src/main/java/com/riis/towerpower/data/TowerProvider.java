package com.riis.towerpower.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TowerProvider extends ContentProvider
{
    private static final String mLocationSelection =
            TowerContract.DbLocation.TABLE_NAME + "." + TowerContract.DbLocation.COLUMN_LATITUDE + " = ?"
                    + " AND " + TowerContract.DbLocation.TABLE_NAME + "."
                    + TowerContract.DbLocation.COLUMN_LONGITUDE + " = ?";
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    public static final int LOCATION = 100;
    public static final int LOCATION_WITH_TOWER = 101;
//    public static final int LOCATION_WITH_TOWER_AND_NETWORK = 102;
    public static final int NETWORK = 200;
    public static final int TOWER = 300;

    private TowerDbHelper mTowerDbHelper;

    SQLiteQueryBuilder mLocationQueryBuilder = new SQLiteQueryBuilder();
    SQLiteQueryBuilder mLocationTowerNetworkQueryBuilder = new SQLiteQueryBuilder();

    @Override
    public boolean onCreate()
    {
        mTowerDbHelper = new TowerDbHelper(getContext());

        mLocationQueryBuilder.setTables(
                TowerContract.DbLocation.TABLE_NAME + " INNER JOIN " + TowerContract.DbTower.TABLE_NAME
                + " ON "
                + TowerContract.DbLocation.TABLE_NAME + "." + TowerContract.DbLocation.COLUMN_TOWER_ID
                + " = "
                + TowerContract.DbTower.TABLE_NAME + "." + TowerContract.DbTower._ID);

        mLocationTowerNetworkQueryBuilder = mLocationQueryBuilder;

        mLocationTowerNetworkQueryBuilder.setTables(
                TowerContract.DbTower.TABLE_NAME + " INNER JOIN " + TowerContract.DbNetwork.TABLE_NAME
                        + " ON "
                        + TowerContract.DbTower.TABLE_NAME + "." + TowerContract.DbTower.COLUMN_NETWORK_TYPE
                        + " = "
                        + TowerContract.DbNetwork.TABLE_NAME + "." + TowerContract.DbNetwork._ID);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        switch (mUriMatcher.match(uri)) {
            case TOWER:
                retCursor = mTowerDbHelper.getReadableDatabase().query(TowerContract.DbTower.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NETWORK:
                retCursor = mTowerDbHelper.getReadableDatabase().query(TowerContract.DbNetwork.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LOCATION:
                retCursor = mTowerDbHelper.getReadableDatabase().query(TowerContract.DbLocation.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LOCATION_WITH_TOWER:
                retCursor = getTowerByLocation(uri, projection, sortOrder);
                break;
//            case LOCATION_WITH_TOWER_AND_NETWORK:
//                retCursor = mLocationTowerNetworkQueryBuilder.query(mTowerDbHelper.getReadableDatabase(),
//                        projection, mLocationSelection, selectionArgs, null, null, sortOrder);
//                break;
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
            case TOWER:
                return TowerContract.DbTower.CONTENT_TYPE;
            case NETWORK:
                return TowerContract.DbNetwork.CONTENT_TYPE;
            case LOCATION:
                return TowerContract.DbLocation.CONTENT_TYPE;
            case LOCATION_WITH_TOWER:
                return TowerContract.DbLocation.CONTENT_TYPE;
//            case LOCATION_WITH_TOWER_AND_NETWORK:
//                return TowerContract.DbLocation.CONTENT_TYPE;
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

        long _id;
        switch (match)
        {
            case TOWER:
                _id = db.insert(TowerContract.DbTower.TABLE_NAME, null, values);
                if(_id > 0)
                {
                    returnUri = TowerContract.DbTower.buildTowerUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case LOCATION:
                _id = db.insert(TowerContract.DbLocation.TABLE_NAME, null, values);
                if(_id > 0)
                {
                    returnUri = TowerContract.DbLocation.buildLocationUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case NETWORK:
                _id = db.insert(TowerContract.DbNetwork.TABLE_NAME, null, values);
                if(_id > 0)
                {
                    returnUri = TowerContract.DbNetwork.buildNetworkUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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
            case TOWER:
                rowsDeleted = db.delete(TowerContract.DbTower.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(TowerContract.DbLocation.TABLE_NAME, selection, selectionArgs);
                break;
            case NETWORK:
                rowsDeleted = db.delete(TowerContract.DbNetwork.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
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
            case NETWORK:
                rowsUpdated = db.update(TowerContract.DbNetwork.TABLE_NAME, values, selection,
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
        matcher.addURI(authority, TowerContract.PATH_LOCATION + "/*/*", LOCATION_WITH_TOWER);
        matcher.addURI(authority, TowerContract.PATH_TOWER, TOWER);
        matcher.addURI(authority, TowerContract.PATH_NETWORK, NETWORK);
        return matcher;
    }

    private Cursor getTowerByLocation(Uri uri, String[] projection, String sortOrder)
    {
        String[] coordinates = TowerContract.DbLocation.getLatitudeLongitudeFromUri(uri);

        return mLocationQueryBuilder.query(mTowerDbHelper.getReadableDatabase(),
                projection, mLocationSelection, coordinates, null, null, sortOrder);
    }
}
