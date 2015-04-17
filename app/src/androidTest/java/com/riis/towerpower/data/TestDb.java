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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

public class TestDb extends AndroidTestCase
{
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void setUp()
    {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable
    {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(TowerContract.DbLocation.TABLE_NAME);
        tableNameHashSet.add(TowerContract.DbTower.TABLE_NAME);
        tableNameHashSet.add(TowerContract.DbNetwork.TABLE_NAME);

        mContext.deleteDatabase(TowerDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new TowerDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        while(c.moveToNext())
        {
            tableNameHashSet.remove(c.getString(0));
        }
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + TowerContract.DbNetwork.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> networkColumnHashSet = new HashSet<>();
        networkColumnHashSet.add(TowerContract.DbNetwork._ID);
        networkColumnHashSet.add(TowerContract.DbNetwork.COLUMN_NAME);

        int columnNameIndex = c.getColumnIndex("name");
        do
        {
            String columnName = c.getString(columnNameIndex);
            networkColumnHashSet.remove(columnName);
            Log.e(LOG_TAG, columnName);
        }
        while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required network columns",
                networkColumnHashSet.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + TowerContract.DbTower.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> towerColumnHashSet = new HashSet<>();
        towerColumnHashSet.add(TowerContract.DbTower._ID);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_NAME);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_NETWORK_TYPE);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_PING_TIME);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_RELIABILITY);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI);
        towerColumnHashSet.add(TowerContract.DbTower.COLUMN_UPLOAD_SPEED);

        columnNameIndex = c.getColumnIndex("name");
        do
        {
            String columnName = c.getString(columnNameIndex);
            towerColumnHashSet.remove(columnName);
            Log.e(LOG_TAG, columnName);
        }
        while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required tower columns",
                towerColumnHashSet.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + TowerContract.DbLocation.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(TowerContract.DbLocation._ID);
        locationColumnHashSet.add(TowerContract.DbLocation.COLUMN_LATITUDE);
        locationColumnHashSet.add(TowerContract.DbLocation.COLUMN_LONGITUDE);
        locationColumnHashSet.add(TowerContract.DbLocation.COLUMN_TOWER_ID);

        columnNameIndex = c.getColumnIndex("name");
        do
        {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
            Log.e(LOG_TAG, columnName);
        }
        while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location columns",
                locationColumnHashSet.isEmpty());
        c.close();
        db.close();
    }

    public void testLocationTable()
    {
        insertLocation();
    }

    public void testTowerTable()
    {
        insertTower();
    }

    public void testNetworkTable()
    {
        insertNetwork();
    }

    long insertLocation()
    {
        long towerRowId = insertTower();
        assertFalse("Error: Tower Not Inserted Correctly", towerRowId == -1L);

        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId = db.insert(TowerContract.DbLocation.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(TowerContract.DbLocation.TABLE_NAME, null, null, null, null, null,
                null);
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return locationRowId;
    }

    long insertTower()
    {
        long networkRowId = insertNetwork();
        assertFalse("Error: Network Not Inserted Correctly", networkRowId == -1L);

        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues towerValues = TestUtilities.createNorthPoleTowerValues(networkRowId);

        long towerRowId = db.insert(TowerContract.DbTower.TABLE_NAME, null, towerValues);
        assertTrue(towerRowId != -1);

        Cursor towerCursor = db.query(TowerContract.DbTower.TABLE_NAME, null, null, null, null,
                null, null);
        assertTrue("Error: No Records returned from location query", towerCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb DbTower failed to validate",
                towerCursor, towerValues);

        assertFalse("Error: More than one record returned from tower query",
                towerCursor.moveToNext());

        towerCursor.close();
        dbHelper.close();
        return towerRowId;
    }

    long insertNetwork()
    {
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleNetworkValues();

        long locationRowId = db.insert(TowerContract.DbNetwork.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(TowerContract.DbNetwork.TABLE_NAME, null, null, null, null, null, null);
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return locationRowId;
    }

    void deleteTheDatabase()
    {
        mContext.deleteDatabase(TowerDbHelper.DATABASE_NAME);
    }
}
