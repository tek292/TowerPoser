package com.riis.towerpower.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author tkocikjr
 */
public class CellTowerPagerAdapter extends FragmentPagerAdapter
{
    private Context mContext;

    public CellTowerPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
