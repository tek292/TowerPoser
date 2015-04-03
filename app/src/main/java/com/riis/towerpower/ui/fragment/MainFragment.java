package com.riis.towerpower.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riis.towerpower.R;

/**
 * @author tkocikjr
 */
public class MainFragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setUpViews(rootView);
        return rootView;
    }

    private void setUpViews(View view)
    {
//        mCellTowerViewPager = (ViewPager) findViewById(R.id.cell_tower_pager);

    }
}
