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
}
