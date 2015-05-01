package com.riis.towerpower.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Consts;
import com.riis.towerpower.ui.fragment.TowerDetailFragment;
import com.riis.towerpower.ui.fragment.TowerListFragment;
import com.riis.towerpower.util.OnTowerSelectedListener;
import com.riis.towerpower.util.sync.TowerSyncAdapter;

/**
 * @author tkocikjr
 */
public class MainActivity extends ActionBarActivity implements OnTowerSelectedListener
{
    private static final String DETAIL_FRAGMENT_TAG = "detailFragment";
    private static final String LIST_FRAGMENT_TAG = "listFragment";

    private boolean mTwoPane;
    private Double mLatitude;
    private Double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null)
        {
            mTwoPane = true;
            if (savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new TowerDetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tower_list_fragment, new TowerListFragment(), LIST_FRAGMENT_TAG)
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

        if (!latitude.equals(mLatitude) || !longitude.equals(mLongitude))
        {
            TowerListFragment listFragment = (TowerListFragment) getSupportFragmentManager()
                    .findFragmentByTag(LIST_FRAGMENT_TAG);
            if (listFragment != null)
            {
                listFragment.onLocationChanged();
            }

            TowerDetailFragment detailFragment = (TowerDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (detailFragment != null)
            {
                onTowerSelected(null);
            }

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

    @Override
    public void onTowerSelected(Uri towerUri)
    {
        if (mTwoPane)
        {
            Bundle args = new Bundle();
            args.putParcelable(TowerDetailFragment.DETAIL_URI, towerUri);

            TowerDetailFragment fragment = new TowerDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else
        {
            Intent intent = new Intent(this, TowerDetailActivity.class).setData(towerUri);
            startActivity(intent);
        }
    }
}
