package com.riis.towerpower.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;


import com.riis.towerpower.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase
{
    static final long TEST_NETWORK_ID = 1L;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues)
    {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues)
    {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);

            Object expectedValue;
            Object actualValue;

            if(valueCursor.getType(idx) == Cursor.FIELD_TYPE_FLOAT)
            {
                expectedValue = Double.parseDouble(entry.getValue().toString());
                actualValue = valueCursor.getDouble(idx);
            }
            else
            {
                expectedValue = entry.getValue().toString();
                actualValue = valueCursor.getString(idx);
            }

            assertEquals("Value '" + actualValue.toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, actualValue);
        }
    }

    static ContentValues createNorthPoleLocationValues()
    {
        ContentValues testValues = new ContentValues();
        testValues.put(TowerContract.DbLocation.COLUMN_TOWER_ID, 1);
        testValues.put(TowerContract.DbLocation.COLUMN_LATITUDE, 64.7488);
        testValues.put(TowerContract.DbLocation.COLUMN_LONGITUDE, -147.353);
        return testValues;
    }

    // All values taken from http://developer.opensignal.com/networkrank/
    static ContentValues createNorthPoleTowerValues(long networkRowId)
    {
        ContentValues testValues = new ContentValues();
        testValues.put(TowerContract.DbTower.COLUMN_NETWORK_TYPE, networkRowId);
        testValues.put(TowerContract.DbTower.COLUMN_NAME, "T-Mobile");
        testValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU, 15.235661);
        testValues.put(TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB, -82.528677);
        testValues.put(TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED, 3291.3388);
        testValues.put(TowerContract.DbTower.COLUMN_PING_TIME, 419.3265);
        testValues.put(TowerContract.DbTower.COLUMN_RELIABILITY, 0.91815600);
        testValues.put(TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI, 231564);
        testValues.put(TowerContract.DbTower.COLUMN_UPLOAD_SPEED, 1237.3268);
        return testValues;
    }

    static ContentValues createNorthPoleNetworkValues()
    {
        ContentValues testValues = new ContentValues();
        testValues.put(TowerContract.DbNetwork.COLUMN_NAME, "4");
        return testValues;
    }

    static long insertNorthPoleLocationValues(Context context) {
        TowerDbHelper dbHelper = new TowerDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(TowerContract.DbLocation.TABLE_NAME, null, testValues);
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);
        db.close();

        return locationRowId;
    }

    static class TestContentObserver extends ContentObserver
    {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht)
        {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange)
        {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail()
        {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000)
            {
                @Override
                protected boolean check()
                {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver()
    {
        return TestContentObserver.getTestContentObserver();
    }
}
