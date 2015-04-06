package com.riis.towerpower.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;
import com.riis.towerpower.models.eNetworkType;

import java.util.ArrayList;

/**
 * @author tkocikjr
 */
public class TowerListAdapter extends RecyclerView.Adapter<TowerListAdapter.TowerListViewHolder>
{
    private ArrayList<Tower> mTowerList;
    private Context mContext;
    private eNetworkType mNetworkType;

    public TowerListAdapter(Context context, ArrayList<Tower> towerList, eNetworkType type)
    {
        mContext = context;
        mTowerList = towerList;
        mNetworkType = type;
    }

    @Override
    public TowerListAdapter.TowerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(mNetworkType == eNetworkType.OTHER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_other_network, parent, false);
            return new TowerListViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main_network, parent, false);
            return new TowerListViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(TowerListAdapter.TowerListViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return mTowerList.size();
    }

    final static class TowerListViewHolder extends RecyclerView.ViewHolder
    {

        public TowerListViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
