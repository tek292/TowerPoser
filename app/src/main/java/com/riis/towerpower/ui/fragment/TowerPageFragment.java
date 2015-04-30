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

    private ListView mTowerList;
    private TextView mNoDataTextView;
    private TowerListAdapter mTowerListAdapter;
    private Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(Consts.getLatitude())) {
            String latitude = (String) savedInstanceState.get(Consts.getLatitude());
            String longitude = (String) savedInstanceState.get(Consts.getLongitude());
            mUri = TowerContract.DbLocationTower.buildLocationToTowerWithCoordinates(
                    Double.parseDouble(latitude), Double.parseDouble(longitude));
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

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
    public void onSaveInstanceState(Bundle outState) {
        // When devices rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        String[] coordinates = TowerContract.DbLocationTower.getLocationToTowerFromUri(mUri);
        outState.putString(Consts.getLatitude(), coordinates[0]);
        outState.putString(Consts.getLongitude(), coordinates[1]);

//        if (mPosition != ListView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, mPosition);
//        }
        super.onSaveInstanceState(outState);
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
