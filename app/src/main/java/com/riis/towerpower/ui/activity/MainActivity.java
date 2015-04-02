package com.riis.towerpower.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.riis.towerpower.R;
import com.riis.towerpower.util.GPSTrackerService;

/**
 * @author tkocikjr
 */
public class MainActivity extends ActionBarActivity
{
    private GPSTrackerService mGPSTrackerService;
//    private ViewPager mCellTowerViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGPSTrackerService = new GPSTrackerService(this);

//        setUpViews();

        if(!mGPSTrackerService.canGetLocation())
        {
            mGPSTrackerService.showSettingsAlert();
        }
        else
        {
            new Test().execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void setUpViews()
//    {
//        mCellTowerViewPager = (ViewPager) findViewById(R.id.cell_tower_pager);
//
//    }

    private class Test extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            Double latitude = mGPSTrackerService.getLatitude();
            Double longitude = mGPSTrackerService.getLongitude();

            Log.w("GPS LAT", latitude.toString());
            Log.w("GPS LONG", longitude.toString());


            // Reference: http://stackoverflow.com/questions/4152373/how-to-know-location-area-code-and-cell-id-in-android-phone
//            final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
//                final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
//                if (location != null) {
//                    Log.w("TESTING", "LAC: " + location.getLac() + " CID: " + location.getCid());
//                }
//            }

            return null;
        }
    }
}
