package com.riis.towerpower.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;
import com.riis.towerpower.util.CellTowerPagerAdapter;
import com.riis.towerpower.util.GPSTrackerService;
import com.riis.towerpower.util.TowerPowerRetriever;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * @author tkocikjr
 */
public class MainActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
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

    private void setUpViews()
    {
        ViewPager cellTowerViewPager = (ViewPager) findViewById(R.id.cell_tower_pager);
        cellTowerViewPager.setAdapter(new CellTowerPagerAdapter(this, getSupportFragmentManager()));
    }
}
