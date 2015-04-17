package com.riis.towerpower.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase
{
    private static final Uri TEST_TOWER_DIR = TowerContract.DbTower.CONTENT_URI;
    private static final Uri TEST_NETWORK_DIR = TowerContract.DbNetwork.CONTENT_URI;
    private static final Uri TEST_TOWER_WITH_LOCATION =
            TowerContract.DbLocation.buildLocationUri(42.4, -83.6);
    private static final Uri TEST_LOCATION_DIR = TowerContract.DbLocation.CONTENT_URI;

    public void testUriMatcher()
    {
        UriMatcher testMatcher = TowerProvider.buildUriMatcher();

        assertEquals("Error: The TOWER URI was matched incorrectly.",
                testMatcher.match(TEST_TOWER_DIR), TowerProvider.TOWER);
        assertEquals("Error: The NETWORK URI was matched incorrectly.",
                testMatcher.match(TEST_NETWORK_DIR), TowerProvider.NETWORK);
        assertEquals("Error: The LOCATION WITH TOWER URI was matched incorrectly.",
                testMatcher.match(TEST_TOWER_WITH_LOCATION), TowerProvider.LOCATION_WITH_TOWER);
        assertEquals("Error: The LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_DIR), TowerProvider.LOCATION);
    }
}
