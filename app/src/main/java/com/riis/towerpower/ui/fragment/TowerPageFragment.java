package com.riis.towerpower.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;
import com.riis.towerpower.models.eNetworkType;
import com.riis.towerpower.util.TowerListAdapter;

import java.util.ArrayList;

/**
 * @author tkocikjr
 */
public class TowerPageFragment extends Fragment
{
    private ArrayList<Tower> mTowerData;
    private eNetworkType mNetworkType;
    private RecyclerView mTowerList;

    public TowerPageFragment newInstance(eNetworkType type, ArrayList<Tower> towerList)
    {
        TowerPageFragment towerPageFragment = new TowerPageFragment();
        towerPageFragment.mTowerData = towerList;
        towerPageFragment.mNetworkType = type;
        return towerPageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_tower_page, container, false);

        setUpViews(rootView);

        return rootView;
    }

    private void setUpViews(View rootView)
    {
        mTowerList = (RecyclerView) rootView.findViewById(R.id.data_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTowerList.setAdapter(new TowerListAdapter(mTowerData, mNetworkType));

        if(mTowerData.size() == 0)
        {
            mTowerList.setVisibility(View.INVISIBLE);
        }
    }
}
