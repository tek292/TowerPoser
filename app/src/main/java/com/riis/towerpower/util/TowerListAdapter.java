package com.riis.towerpower.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private eNetworkType mNetworkType;

    public TowerListAdapter(ArrayList<Tower> towerList, eNetworkType type)
    {
        mTowerList = towerList;
        mNetworkType = type;
    }

    @Override
    public TowerListAdapter.TowerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(mNetworkType == eNetworkType.OTHER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_other_network, parent, false);
            return new TowerListViewHolder(v, mNetworkType);
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main_network, parent, false);
            return new TowerListViewHolder(v, mNetworkType);
        }
    }

    @Override
    public void onBindViewHolder(TowerListAdapter.TowerListViewHolder holder, int position)
    {
        if(mNetworkType == eNetworkType.OTHER)
        {
            holder.networkName.setText(mTowerList.get(position).getNetworkName());
        }

        holder.reliability.setText(Double.toString(mTowerList.get(position).getReliability()));
    }

    @Override
    public int getItemCount()
    {
        return mTowerList.size();
    }

    final static class TowerListViewHolder extends RecyclerView.ViewHolder
    {
        TextView networkName;
        TextView reliability;

        public TowerListViewHolder(View itemView, eNetworkType networkType)
        {
            super(itemView);

            reliability = (TextView) itemView.findViewById(R.id.reliability_text_view);

            if(networkType == eNetworkType.OTHER)
            {
                networkName = (TextView) itemView.findViewById(R.id.network_name_text_view);
            }
        }
    }
}
