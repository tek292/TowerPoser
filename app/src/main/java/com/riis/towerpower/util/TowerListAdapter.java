package com.riis.towerpower.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;
import com.riis.towerpower.models.eNetworkType;

import java.util.ArrayList;

/**
 * @author tkocikjr
 */
public class TowerListAdapter extends CursorAdapter
{
    private ArrayList<Tower> mTowerList;
    private eNetworkType mNetworkType;

    public TowerListAdapter(Context context, Cursor cursor, eNetworkType type)
    {
        super(context, cursor, false);
        mNetworkType = type;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        if(mNetworkType == eNetworkType.OTHER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_other_network, parent, false);
            TowerListViewHolder viewHolder = new TowerListViewHolder();
            viewHolder.networkName = (TextView) v.findViewById(R.id.network_name_text_view);
            viewHolder.reliability = (TextView) v.findViewById(R.id.reliability_text_view);
            v.setTag(viewHolder);
            return v;
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main_network, parent, false);
            TowerListViewHolder viewHolder = new TowerListViewHolder();
            viewHolder.networkName = null;
            viewHolder.reliability = (TextView) v.findViewById(R.id.reliability_text_view);
            v.setTag(viewHolder);
            return v;
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TowerListViewHolder viewHolder = (TowerListViewHolder) view.getTag();

//        if(mNetworkType == eNetworkType.OTHER)
//        {
//            viewHolder.networkName.setText(mTowerList.get(position).getNetworkName());
//        }
//
//        viewHolder.reliability.setText(Double.toString(mTowerList.get(position).getReliability()));
    }

    private static class TowerListViewHolder
    {
        TextView networkName;
        TextView reliability;
    }
}
