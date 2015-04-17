package com.riis.towerpower.models;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Assert;

public class TestTowerProvider extends AndroidTestCase
{
    public static final String LOG_TAG = TestTowerProvider.class.getSimpleName();

    private static final int BULK_INSERT_SIZE = 10;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void testProviderRegistry()
    {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                TowerProvider.class.getName());
        try
        {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            Assert.assertEquals("Error: WeatherProvider registered with authority: "
                            + providerInfo.authority + " instead of authority: "
                            + TowerContract.CONTENT_AUTHORITY,
                    providerInfo.authority, TowerContract.CONTENT_AUTHORITY);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType()
    {
        String type = mContext.getContentResolver().getType(TowerContract.DbLocation.CONTENT_URI);
        assertEquals("Error: the DbLocation CONTENT_URI with location should return DbLocation.CONTENT_TYPE",
                TowerContract.DbLocation.CONTENT_TYPE, type);

        Double[] testCoordinates = {42.4, -147.2};
        type = mContext.getContentResolver().getType(
                TowerContract.DbLocation.buildLocationUri(testCoordinates[0], testCoordinates[1]));
        assertEquals("Error: the DbLocation CONTENT_URI with location should return DbLocation.CONTENT_ITEM_TYPE",
                TowerContract.DbLocation.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbTower.CONTENT_URI);
        assertEquals("Error: the DbTower CONTENT_URI should return DbTower.CONTENT_TYPE",
                TowerContract.DbTower.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbTower.buildTowerUri(1));
        assertEquals("Error: the DbTower CONTENT_URI should return DbTower.CONTENT_ITEM_TYPE",
                TowerContract.DbTower.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbLocationTower.CONTENT_URI);
        assertEquals("Error: the DbLocationTower CONTENT_URI should return DbLocationTower.CONTENT_TYPE",
                TowerContract.DbLocationTower.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbLocationTower.buildLocationToTower(1));
        assertEquals("Error: the DbLocationTower CONTENT_URI with location and date should return"
                        + " DbLocationTower.CONTENT_ITEM_TYPE",
                TowerContract.DbLocationTower.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbLocationTower.buildLocationToTower(1, 1));
        assertEquals("Error: the DbLocationTower CONTENT_URI with location and date should return"
                        + " DbLocationTower.CONTENT_ITEM_TYPE",
                TowerContract.DbLocationTower.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicLocationQuery()
    {
        ContentValues testValues = TestUtilities.createSanFranciscoLocationValues();
        long locationRowId = TestUtilities.insertSanFranciscoLocationValues(mContext);
        assertTrue("Unable to Insert DbLocation into the Database", locationRowId != -1);

        Cursor locationCursor = mContext.getContentResolver().query(
                TowerContract.DbLocation.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testBasicLocationQuery", locationCursor, testValues);
    }

    public void testBasicTowerQuery()
    {
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createSanFranciscoTowerValues();
        long towerRowId = db.insert(TowerContract.DbTower.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert DbTower into the Database", towerRowId != -1);
        db.close();

        Cursor towerCursor = mContext.getContentResolver().query(
                TowerContract.DbTower.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testBasicTowerQuery", towerCursor, testValues);
    }

    public void testBasicTowerLocationQuery()
    {
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createSanFranciscoTowerLocationValues();
        long networkRowId = db.insert(TowerContract.DbLocationTower.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert DbLocationTower into the Database", networkRowId != -1);

        Cursor networkCursor = mContext.getContentResolver().query(
                TowerContract.DbLocationTower.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testBasicTowerLocationQuery", networkCursor, testValues);
    }

    public void testUpdateLocation()
    {
        ContentValues values = TestUtilities.createSanFranciscoLocationValues();

        Uri locationUri = mContext.getContentResolver().insert(TowerContract.DbLocation.CONTENT_URI,
                values);
        long locationRowId = ContentUris.parseId(locationUri);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(TowerContract.DbLocation.COLUMN_LATITUDE, 0);
        updatedValues.put(TowerContract.DbLocation.COLUMN_LONGITUDE, 0);

        Cursor locationCursor = mContext.getContentResolver().query(TowerContract.DbLocation.CONTENT_URI,
                null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                TowerContract.DbLocation.CONTENT_URI, updatedValues, TowerContract.DbLocation._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();
        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                TowerContract.DbLocation.CONTENT_URI, null,
                TowerContract.DbLocation._ID + " = " + locationRowId, null, null);

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider()
    {
        ContentValues towerValues = TestUtilities.createSanFranciscoTowerValues();
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TowerContract.DbTower.CONTENT_URI,
                true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(TowerContract.DbTower.CONTENT_URI, towerValues);
        assertTrue(weatherInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor towerCursor = mContext.getContentResolver().query(TowerContract.DbTower.CONTENT_URI,
                null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating DbTower insert.",
                towerCursor, towerValues);
    }

    public void testDeleteRecords()
    {
        testInsertReadProvider();

        TestUtilities.TestContentObserver towerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TowerContract.DbTower.CONTENT_URI,
                true, towerObserver);

        deleteAllRecordsFromProvider();

        towerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(towerObserver);
    }

    public void testBulkInsert()
    {
        ContentValues[] bulkInsertContentValues = createBulkInsertTowerValues();

        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TowerContract.DbTower.CONTENT_URI,
                true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(TowerContract.DbTower.CONTENT_URI,
                bulkInsertContentValues);

        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_SIZE);

        Cursor cursor = mContext.getContentResolver().query(TowerContract.DbTower.CONTENT_URI, null, null,
                null, TowerContract.DbTower.COLUMN_RELIABILITY + " ASC");
        assertEquals(cursor.getCount(), BULK_INSERT_SIZE);

        cursor.moveToFirst();
        for(int i = 0; i < BULK_INSERT_SIZE; i++, cursor.moveToNext())
        {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    private ContentValues[] createBulkInsertTowerValues()
    {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_SIZE];
        for(int i = 0; i < BULK_INSERT_SIZE; i++)
        {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(TowerContract.DbTower.COLUMN_RELIABILITY, 75.2 + i);
            weatherValues.put(TowerContract.DbTower.COLUMN_PING_TIME, 86.45 - i);
            weatherValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB, -87.32 + i);
            weatherValues.put(TowerContract.DbTower.COLUMN_UPLOAD_SPEED, 1.2 + 0.01 * (float) i);
            weatherValues.put(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI, 1.3 - 0.01 * (float) i);
            weatherValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU, 75 + i);
            weatherValues.put(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED, 65 - i);
            weatherValues.put(TowerContract.DbTower.COLUMN_NAME, "Asteroids");
            weatherValues.put(TowerContract.DbTower.COLUMN_NETWORK_TYPE, 1);
            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }

    private void deleteAllRecordsFromProvider()
    {
        mContext.getContentResolver().delete(TowerContract.DbLocation.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(TowerContract.DbTower.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(TowerContract.DbLocationTower.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query( TowerContract.DbLocationTower.CONTENT_URI,
                null, null, null,null);
        assertEquals("Error: Records not deleted from LocationTower table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(TowerContract.DbTower.CONTENT_URI, null, null,
                null, null);
        assertEquals("Error: Records not deleted from Tower table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(TowerContract.DbLocation.CONTENT_URI, null, null,
                null, null);
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }
}
