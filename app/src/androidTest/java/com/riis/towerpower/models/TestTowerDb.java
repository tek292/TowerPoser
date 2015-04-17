package com.riis.towerpower.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

public class TestTowerDb extends AndroidTestCase
{
    public void setUp()
    {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable
    {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(TowerContract.DbLocation.TABLE_NAME);
        tableNameHashSet.add(TowerContract.DbTower.TABLE_NAME);
        tableNameHashSet.add(TowerContract.DbLocationTower.TABLE_NAME);

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
        assertTrue("Error: Your database was created without necessary tables",
                tableNameHashSet.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + TowerContract.DbLocationTower.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> networkColumnHashSet = new HashSet<>();
        networkColumnHashSet.add(TowerContract.DbLocationTower._ID);
        networkColumnHashSet.add(TowerContract.DbLocationTower.COLUMN_LOCATION_ID);
        networkColumnHashSet.add(TowerContract.DbLocationTower.COLUMN_TOWER_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do
        {
            String columnName = c.getString(columnNameIndex);
            networkColumnHashSet.remove(columnName);
        }
        while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required tower_to_location columns",
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

        columnNameIndex = c.getColumnIndex("name");
        do
        {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
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

    public void testLocationTowerTable()
    {
        insertLocationToTower();
    }

    long insertLocation()
    {
        long towerRowId = insertTower();
        assertFalse("Error: Tower Not Inserted Correctly", towerRowId == -1L);

        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSanFranciscoLocationValues();

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
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues towerValues = TestUtilities.createSanFranciscoTowerValues();

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

    long insertLocationToTower()
    {
        TowerDbHelper dbHelper = new TowerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSanFranciscoTowerLocationValues();

        long locationRowId = db.insert(TowerContract.DbLocationTower.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(TowerContract.DbLocationTower.TABLE_NAME, null, null, null, null, null, null);
        assertTrue( "Error: No Records returned from location_to_tower query", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Location To Tower Query Validation Failed",
                cursor, testValues);
        assertFalse( "Error: More than one record returned from location_to_tower query",
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
