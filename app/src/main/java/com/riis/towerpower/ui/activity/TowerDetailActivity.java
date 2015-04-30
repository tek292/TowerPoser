package com.riis.towerpower.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.riis.towerpower.R;
import com.riis.towerpower.ui.fragment.TowerDetailFragment;

/**
 * @author tkocikjr
 */
public class TowerDetailActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_detail);

        if (savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            arguments.putParcelable(TowerDetailFragment.DETAIL_URI, getIntent().getData());

            TowerDetailFragment fragment = new TowerDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.detail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        int id = item.getItemId();
//        if (id == R.id.action_settings)
//        {
//            startActivity(new Intent(this, SettingsActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
