package com.riis.towerpower.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tower, parent, false);
        TowerListViewHolder viewHolder = new TowerListViewHolder();
        viewHolder.networkNameImage = (ImageView) v.findViewById(R.id.network_name_image_view);
        viewHolder.networkName = (TextView) v.findViewById(R.id.network_name_text_view);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TowerListViewHolder viewHolder = (TowerListViewHolder) view.getTag();

        String networkName = cursor.getString(cursor.getColumnIndex(TowerContract.DbTower.COLUMN_NAME));

        if(networkName.equals(context.getString(R.string.at_t)))
        {
            viewHolder.networkNameImage.setImageDrawable(context.getResources().getDrawable(R.drawable.at_t));
        }
        else if(networkName.equals(context.getString(R.string.sprint)))
        {
            viewHolder.networkNameImage.setImageDrawable(context.getResources().getDrawable(R.drawable.sprint));
        }
        else if(networkName.equals(context.getString(R.string.t_mobile)))
        {
            viewHolder.networkNameImage.setImageDrawable(context.getResources().getDrawable(R.drawable.t_mobile));
        }
        else if(networkName.equals(context.getString(R.string.verizon)))
        {
            viewHolder.networkNameImage.setImageDrawable(context.getResources().getDrawable(R.drawable.verizon));
        }
        else
        {
            viewHolder.networkNameImage.setImageDrawable(context.getResources().getDrawable(R.drawable.other));
        }

        viewHolder.networkName.setText(networkName);

//        viewHolder.reliability.setText(cursor.getString(cursor.getColumnIndex(TowerContract.DbTower.COLUMN_RELIABILITY)));
    }

    private static class TowerListViewHolder
    {
        ImageView networkNameImage;
        TextView networkName;
    }
}
