package com.riis.towerpower.models;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestTowerContract extends AndroidTestCase
{
    private static final Double[] coordinates = {64.7488, -147.353};

    public void testBuildLocation()
    {
        Uri locationUri = TowerContract.DbLocation.buildLocationUri(coordinates[0], coordinates[1]);
        assertNotNull("Error: Null Uri returned.", locationUri);
        assertEquals("Error: Location Latitude not properly appended to the Uri",
                coordinates[0].toString(), locationUri.getPathSegments().get(1));
        assertEquals("Error: Location Longitude not properly appended to the end of the Uri",
                coordinates[1].toString(), locationUri.getLastPathSegment());
        assertEquals("Error: Location Uri doesn't match our expected result",
                locationUri.toString(), "content://com.riis.towerpower/location/64.7488/-147.353");
    }

    public void testBuildTower()
    {
        Uri towerUri = TowerContract.DbTower.buildTowerUri(1);
        assertNotNull("Error: Null Uri returned.", towerUri);
        assertEquals("Error: Tower id not properly appended to the end of the Uri",
                "1", towerUri.getLastPathSegment());
        assertEquals("Error: Tower id Uri doesn't match our expected result",
                towerUri.toString(), "content://com.riis.towerpower/tower/1");
    }

    public void testBuildTowerLocation()
    {
        Uri towerLocationUri = TowerContract.DbLocationTower
                .buildLocationToTowerWithCoordinates(coordinates[0], coordinates[1]);
        assertNotNull("Error: Null Uri returned.", towerLocationUri);
        assertEquals("Error: Location Latitude not properly appended to the Uri",
                "64.7488", towerLocationUri.getPathSegments().get(1));
        assertEquals("Error: Location Longitude not properly appended to the end of the Uri",
                "-147.353", towerLocationUri.getLastPathSegment());
        assertEquals("Error: Location to Tower Uri doesn't match our expected result",
                towerLocationUri.toString(),
                "content://com.riis.towerpower/location_to_tower/64.7488/-147.353");
    }
}
