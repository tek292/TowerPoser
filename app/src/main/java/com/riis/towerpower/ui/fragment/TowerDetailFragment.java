package com.riis.towerpower.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.riis.towerpower.R;
import com.riis.towerpower.models.TowerContract;

/**
 * @author tkocikjr
 */
public class TowerDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String DETAIL_URI = "detailUri";

    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS =
            {
//                    TowerContract.DbTower.TABLE_NAME + "." + TowerContract.DbLocationTower._ID,
                    TowerContract.DbTower.COLUMN_AVERAGE_RSSI_ASU,
                    TowerContract.DbTower.COLUMN_AVERAGE_RSSI_DB,
                    TowerContract.DbTower.COLUMN_DOWNLOAD_SPEED,
                    TowerContract.DbTower.COLUMN_NAME,
                    TowerContract.DbTower.COLUMN_NETWORK_TYPE,
                    TowerContract.DbTower.COLUMN_PING_TIME,
                    TowerContract.DbTower.COLUMN_RELIABILITY,
                    TowerContract.DbTower.COLUMN_SAMPLE_SIZE_RSSI,
                    TowerContract.DbTower.COLUMN_UPLOAD_SPEED
            };

    private TextView mNetworkNameTextView;
    private Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_tower_details, container, false);
        mNetworkNameTextView = (TextView) rootView.findViewById(R.id.network_name_text_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if (mUri != null)
        {
            return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(data != null && data.moveToFirst())
        {
            mNetworkNameTextView.setText(data.getString(data.getColumnIndex(DETAIL_COLUMNS[3])));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }
}
