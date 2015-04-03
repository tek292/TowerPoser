package com.riis.towerpower.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author tkocikjr
 */
public class TowerListAdapter extends RecyclerView.Adapter<TowerListAdapter.TowerListViewHolder>
{
    @Override
    public TowerListAdapter.TowerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(TowerListAdapter.TowerListViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    final static class TowerListViewHolder extends RecyclerView.ViewHolder
    {

        public TowerListViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
