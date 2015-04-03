package com.riis.towerpower.ui.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;
import com.riis.towerpower.ui.fragment.MainFragment;
import com.riis.towerpower.ui.fragment.SettingsFragment;
import com.riis.towerpower.util.GPSTrackerService;
import com.riis.towerpower.util.TowerPowerRetriever;

import org.json.JSONException;

import java.util.ArrayList;

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

        setUpViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mGPSTrackerService.canGetLocation())
        {
            mGPSTrackerService.showSettingsAlert();
        }
        else
        {
            new TowerRetrievalTask(this).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment(), "settings").commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag("settings");
        if(fragment instanceof SettingsFragment)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void setUpViews()
    {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new MainFragment())
                .commit();
    }

    private class TowerRetrievalTask extends AsyncTask<Void, Void, Void>
    {
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public TowerRetrievalTask(Context context)
        {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.retrieving_tower_info));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Double latitude = mGPSTrackerService.getLatitude();
            Double longitude = mGPSTrackerService.getLongitude();

            TowerPowerRetriever retriever = new TowerPowerRetriever();
            String response = retriever.send(latitude.toString(), longitude.toString(), null);
            try
            {
                ArrayList<Tower> list = retriever.getTowerPower(response);

                for(Tower t : list)
                {
                    Log.w("TOWER NAME", t.getNetworkName());
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mProgressDialog.dismiss();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
        }
    }
}
