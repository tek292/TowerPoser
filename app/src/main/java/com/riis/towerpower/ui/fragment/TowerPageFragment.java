package com.riis.towerpower.ui.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Consts;
import com.riis.towerpower.models.TowerContract;
import com.riis.towerpower.util.OnLocationChangedListener;
import com.riis.towerpower.util.TowerListAdapter;
import com.riis.towerpower.util.sync.TowerSyncAdapter;

/**
 * @author tkocikjr
 */
public class TowerPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnLocationChangedListener
{
    private static final int TOWER_LOADER = 0;

    private static final String[] TOWER_COLUMNS = {
            TowerContract.DbTower.TABLE_NAME + "." + TowerContract.DbLocationTower._ID,
//            TowerContract.DbLocation.COLUMN_LATITUDE,
//            TowerContract.DbLocation.COLUMN_LONGITUDE,
            TowerContract.DbTower.COLUMN_NAME
    };

    private ListView mTowerList;
    private TextView mNoDataTextView;
    private TowerListAdapter mTowerListAdapter;
    private Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_tower_page, container, false);

        setUpViews(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(TOWER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mUri = TowerContract.DbLocationTower.buildLocationToTowerWithCoordinates(
                Double.parseDouble(prefs.getString(Consts.getLatitude(), "37.7907")),
                Double.parseDouble(prefs.getString(Consts.getLongitude(), "-122.4058")));

        String sortOrder = TowerContract.DbTower.COLUMN_NETWORK_TYPE + " ASC";

        return new CursorLoader(getActivity(), mUri, TOWER_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        String[] coordinates = TowerContract.DbLocationTower.getLocationToTowerFromUri(mUri);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Double latitude = Double.parseDouble(prefs.getString(Consts.getLatitude(), "37.7907"));
        Double longitude = Double.parseDouble(prefs.getString(Consts.getLongitude(), "-122.4058"));

        if(Double.parseDouble(coordinates[0]) != latitude || Double.parseDouble(coordinates[1]) != longitude)
        {
            getLoaderManager().restartLoader(TOWER_LOADER, null, this);
        }
        else
        {
            mTowerListAdapter.swapCursor(data);
        }

        refreshListLayout();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mTowerListAdapter.swapCursor(null);
    }

    @Override
    public void onLocationChanged()
    {
        TowerSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(TOWER_LOADER, null, this);
    }

    private void setUpViews(View rootView)
    {
        mTowerList = (ListView) rootView.findViewById(R.id.tower_list);
        mNoDataTextView = (TextView) rootView.findViewById(R.id.no_data_textview);

        mTowerListAdapter = new TowerListAdapter(getActivity(), null);
        mTowerList.setAdapter(mTowerListAdapter);

        refreshListLayout();
    }

    private void refreshListLayout()
    {
        if(mTowerListAdapter.getCount() == 0)
        {
            mTowerList.setVisibility(View.GONE);
            mNoDataTextView.findViewById(R.id.no_data_textview).setVisibility(View.VISIBLE);
        }
        else
        {
            mTowerList.setVisibility(View.VISIBLE);
            mNoDataTextView.findViewById(R.id.no_data_textview).setVisibility(View.GONE);
        }
    }
}
