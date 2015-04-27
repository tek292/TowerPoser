package com.riis.towerpower.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.riis.towerpower.R;
import com.riis.towerpower.models.TowerContract;

/**
 * @author tkocikjr
 */
public class TowerListAdapter extends CursorAdapter
{
    public TowerListAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_other_network, parent, false);
        TowerListViewHolder viewHolder = new TowerListViewHolder();
        viewHolder.networkName = (TextView) v.findViewById(R.id.network_name_text_view);
        viewHolder.reliability = (TextView) v.findViewById(R.id.reliability_text_view);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TowerListViewHolder viewHolder = (TowerListViewHolder) view.getTag();

        viewHolder.networkName.setText(cursor.getString(
                cursor.getColumnIndex(TowerContract.DbTower.COLUMN_NAME)));

//        viewHolder.reliability.setText(cursor.getString(cursor.getColumnIndex(TowerContract.DbTower.COLUMN_RELIABILITY)));
    }

    private static class TowerListViewHolder
    {
        TextView networkName;
        TextView reliability;
    }
}
