/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.riis.towerpower.data;

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


public class TestProvider extends AndroidTestCase
{
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    private static final int BULK_INSERT_SIZE = 10;

    @Override
    protected void setUp() throws Exception {
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

            assertEquals("Error: WeatherProvider registered with authority: "
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
        String type = mContext.getContentResolver().getType(TowerContract.DbNetwork.CONTENT_URI);
        assertEquals("Error: the DbNetwork CONTENT_URI should return DbNetwork.CONTENT_TYPE",
                TowerContract.DbNetwork.CONTENT_TYPE, type);

        Double[] testCoordinates = {42.4, -147.2};
        type = mContext.getContentResolver().getType(
                TowerContract.DbLocation.buildLocationUri(testCoordinates[0], testCoordinates[1]));
        assertEquals("Error: the DbLocation CONTENT_URI with location should return DbLocation.CONTENT_TYPE",
                TowerContract.DbLocation.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbNetwork.CONTENT_URI);
        assertEquals("Error: the DbNetwork CONTENT_URI with location and date should return DbNetwork.CONTENT_TYPE",
                TowerContract.DbNetwork.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TowerContract.DbTower.CONTENT_URI);
        assertEquals("Error: the DbTower CONTENT_URI should return DbTower.CONTENT_TYPE",
                TowerContract.DbTower.CONTENT_TYPE, type);
    }

    public void testBasicLocationQuery()
    {
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
        assertTrue("Unable to Insert DbLocation into the Database", locationRowId != -1);

        Cursor locationCursor = mContext.getContentResolver().query(
                TowerContract.DbLocation.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testBasicLocationQuery", locationCursor, testValues);
    }

    public void testBasicTowerQuery()
    {
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues networkValues = TestUtilities.createNorthPoleNetworkValues();
        long networkRowId = db.insert(TowerContract.DbNetwork.TABLE_NAME, null, networkValues);
        assertTrue("Unable to Insert DbNetwork into the Database", networkRowId != -1);

        ContentValues testValues = TestUtilities.createNorthPoleTowerValues(networkRowId);
        long towerRowId = db.insert(TowerContract.DbTower.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert DbTower into the Database", towerRowId != -1);
        db.close();

        Cursor towerCursor = mContext.getContentResolver().query(
                TowerContract.DbTower.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testBasicTowerQuery", towerCursor, testValues);
    }

    public void testBasicNetworkQuery()
    {
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNorthPoleNetworkValues();
        long networkRowId = db.insert(TowerContract.DbNetwork.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert DbNetwork into the Database", networkRowId != -1);

        Cursor networkCursor = mContext.getContentResolver().query(
                TowerContract.DbNetwork.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testBasicNetworkQuery", networkCursor, testValues);
    }

    public void testUpdateLocation()
    {
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        Uri locationUri = mContext.getContentResolver().insert(TowerContract.DbLocation.CONTENT_URI,
                values);
        long locationRowId = ContentUris.parseId(locationUri);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(TowerContract.DbLocation._ID, locationRowId);
        updatedValues.put(TowerContract.DbLocation.COLUMN_TOWER_ID, 4);

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

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider()
    {
        ContentValues networkValues = TestUtilities.createNorthPoleNetworkValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TowerContract.DbNetwork.CONTENT_URI,
                true, tco);
        Uri locationUri = mContext.getContentResolver().insert(TowerContract.DbNetwork.CONTENT_URI,
                networkValues);

        //TODO Verify
        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long networkRowId = ContentUris.parseId(locationUri);
        assertTrue(networkRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(TowerContract.DbNetwork.CONTENT_URI,
                null, null, null, null);

        TestUtilities.validateCursor("testInsertReadProvider. Error validating DbNetwork.",
                cursor, networkValues);

        ContentValues towerValues = TestUtilities.createNorthPoleTowerValues(networkRowId);
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TowerContract.DbTower.CONTENT_URI,
                true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(TowerContract.DbTower.CONTENT_URI, towerValues);
        assertTrue(weatherInsertUri != null);

        //TODO Verify
        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
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

        TestUtilities.TestContentObserver networkObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TowerContract.DbNetwork.CONTENT_URI,
                true, networkObserver);

        TestUtilities.TestContentObserver towerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TowerContract.DbTower.CONTENT_URI,
                true, towerObserver);

        deleteAllRecordsFromProvider();

        //TODO Verify
        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        networkObserver.waitForNotificationOrFail();
        towerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(networkObserver);
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
        mContext.getContentResolver().delete(TowerContract.DbNetwork.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query( TowerContract.DbNetwork.CONTENT_URI, null,
                null, null,null);
        assertEquals("Error: Records not deleted from Network table during delete", 0, cursor.getCount());
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
