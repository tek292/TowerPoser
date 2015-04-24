package com.riis.towerpower.models;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase
{
    private static final Uri TEST_LOCATION_DIR = TowerContract.DbLocation.CONTENT_URI;
    private static final Uri TEST_LOCATION_WITH_COORDINATES =
            TowerContract.DbLocation.buildLocationUri(42.4, -83.6);

    private static final Uri TEST_TOWER_DIR = TowerContract.DbTower.CONTENT_URI;
    private static final Uri TEST_TOWER_ID = TowerContract.DbTower.buildTowerUri(1);

    private static final Uri TEST_LOCATION_TOWER_DIR = TowerContract.DbLocationTower.CONTENT_URI;
    private static final Uri TEST_LOCATION_TOWER_ID = TowerContract.DbLocationTower.buildLocationToTower(1);
    private static final Uri TEST_LOCATION_TOWER_IDS = TowerContract.DbLocationTower
            .buildLocationToTowerWithCoordinates(42.4, -83.6);


    public void testUriMatcher()
    {
        UriMatcher testMatcher = TowerProvider.buildUriMatcher();

        assertEquals("Error: The LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_DIR), TowerProvider.LOCATION);
        assertEquals("Error: The LOCATION_WITH_COORDINATES URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_WITH_COORDINATES), TowerProvider.LOCATION_WITH_COORDINATES);

        assertEquals("Error: The TOWER URI was matched incorrectly.",
                testMatcher.match(TEST_TOWER_DIR), TowerProvider.TOWER);
        assertEquals("Error: The TOWER_WITH_ID URI was matched incorrectly.",
                testMatcher.match(TEST_TOWER_ID), TowerProvider.TOWER_WITH_ID);

        assertEquals("Error: The LOCATION_TO_TOWER URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_TOWER_DIR), TowerProvider.LOCATION_TO_TOWER);
        assertEquals("Error: The LOCATION_TO_TOWER_WITH_ID URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_TOWER_ID), TowerProvider.LOCATION_TO_TOWER_WITH_ID);
        assertEquals("Error: The LOCATION_TO_TOWER_WITH_COORDINATES URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_TOWER_IDS), TowerProvider.LOCATION_TO_TOWER_WITH_COORDINATES);
    }
}
