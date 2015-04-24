package com.riis.towerpower.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Consts;
import com.riis.towerpower.ui.fragment.TowerPageFragment;
import com.riis.towerpower.util.sync.TowerSyncAdapter;

/**
 * @author tkocikjr
 */
public class MainActivity extends ActionBarActivity
{
    private Double mLatitude;
    private Double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tower_list_fragment, new TowerPageFragment())
                    .commit();
        }

        TowerSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Double latitude = Double.parseDouble(prefs.getString(Consts.getLatitude(), "37.7907"));
        Double longitude = Double.parseDouble(prefs.getString(Consts.getLongitude(), "-122.4058"));

        // update the location in our second pane using the fragment manager
        if (!latitude.equals(mLatitude) || !longitude.equals(mLongitude)) {
            TowerPageFragment ff = (TowerPageFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.tower_list_fragment);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
//            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
//            if ( null != df ) {
//                df.onLocationChanged(location);
//            }
            mLatitude = latitude;
            mLongitude = longitude;
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
