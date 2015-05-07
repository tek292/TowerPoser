package com.riis.towerpower.ui.fragment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riis.towerpower.R;
import com.riis.towerpower.models.TowerContract;
import com.riis.towerpower.util.RowClickListener;

/**
 * @author tkocikjr
 */
public class TowerDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String DETAIL_URI = "detailUri";

    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS =
            {
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

    private ImageView mNetworkNameImageView;
    private LinearLayout mRSSIAsuRow;
    private LinearLayout mRSSIDbRow;
    private LinearLayout mDownloadRow;
    private LinearLayout mPingRow;
    private LinearLayout mReliabilityRow;
    private LinearLayout mSampleRow;
    private LinearLayout mUploadRow;
    private TextView mAverageRSSIAsuTextView;
    private TextView mAverageRSSIDbTextView;
    private TextView mDownloadSpeedTextView;
    private TextView mNetworkNameTextView;
    private TextView mNetworkTypeTextView;
    private TextView mPingTimeTextView;
    private TextView mReliabilityTextView;
    private TextView mSampleSizeRSSITextView;
    private TextView mUploadSpeedTextView;
    private Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_tower_details, container, false);
        setUpView(rootView);
        setUpListeners();
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
            setUpDataWithCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    private void setUpView(View view)
    {
        mNetworkNameImageView = (ImageView) view.findViewById(R.id.network_name_image_view);
        mNetworkNameTextView = (TextView) view.findViewById(R.id.network_name_text_view);
        mNetworkTypeTextView = (TextView) view.findViewById(R.id.network_type_text_view);

        mReliabilityRow = (LinearLayout) view.findViewById(R.id.reliability_row);
        mReliabilityTextView = (TextView) view.findViewById(R.id.reliability_text_view);

        mRSSIAsuRow = (LinearLayout) view.findViewById(R.id.rssi_asu_row);
        mAverageRSSIAsuTextView = (TextView) view.findViewById(R.id.average_rssi_asu_text_view);

        mRSSIDbRow = (LinearLayout) view.findViewById(R.id.rssi_db_row);
        mAverageRSSIDbTextView = (TextView) view.findViewById(R.id.average_rssi_db_text_view);

        mSampleRow = (LinearLayout) view.findViewById(R.id.sample_row);
        mSampleSizeRSSITextView = (TextView) view.findViewById(R.id.sample_rssi_text_view);

        mDownloadRow = (LinearLayout) view.findViewById(R.id.download_row);
        mDownloadSpeedTextView = (TextView) view.findViewById(R.id.download_speed_text_view);

        mUploadRow = (LinearLayout) view.findViewById(R.id.upload_row);
        mUploadSpeedTextView = (TextView) view.findViewById(R.id.upload_speed_text_view);

        mPingRow = (LinearLayout) view.findViewById(R.id.ping_row);
        mPingTimeTextView = (TextView) view.findViewById(R.id.ping_time_text_view);
    }

    private void setUpDataWithCursor(Cursor data)
    {
        String networkName = data.getString(data.getColumnIndex(DETAIL_COLUMNS[3]));
        String networkType = data.getString(data.getColumnIndex(DETAIL_COLUMNS[4]));

        if(networkName.equals(getString(R.string.at_t)))
        {
            mNetworkNameImageView.setImageDrawable(getResources().getDrawable(R.drawable.at_t));
        }
        else if(networkName.equals(getString(R.string.sprint)))
        {
            mNetworkNameImageView.setImageDrawable(getResources().getDrawable(R.drawable.sprint));
        }
        else if(networkName.equals(getString(R.string.t_mobile)))
        {
            mNetworkNameImageView.setImageDrawable(getResources().getDrawable(R.drawable.t_mobile));
        }
        else if(networkName.equals(getString(R.string.verizon))) {
            mNetworkNameImageView.setImageDrawable(getResources().getDrawable(R.drawable.verizon));
        }
        else
        {
            mNetworkNameImageView.setImageDrawable(getResources().getDrawable(R.drawable.other));
        }

        mNetworkNameTextView.setText(networkName);
        mNetworkTypeTextView.setText(getString(R.string.network_type_s, networkType));

        String rssiAsu = data.getString(data.getColumnIndex(DETAIL_COLUMNS[0]));
        if(rssiAsu.isEmpty())
        {
            mAverageRSSIAsuTextView.setText(getString(R.string.no_data));
        }
        else
        {
            mAverageRSSIAsuTextView.setText(getString(R.string.rssi_asu_s, rssiAsu));
        }

        String rssiDb = data.getString(data.getColumnIndex(DETAIL_COLUMNS[1]));
        if(rssiDb.isEmpty())
        {
            mAverageRSSIDbTextView.setText(getString(R.string.no_data));
        }
        else
        {
            mAverageRSSIDbTextView.setText(getString(R.string.rssi_db_s, rssiDb));
        }

        String downloadSpeed = data.getString(data.getColumnIndex(DETAIL_COLUMNS[2]));
        if(downloadSpeed.isEmpty())
        {
            mDownloadSpeedTextView.setText(getString(R.string.no_data));
        }
        else
        {
            mDownloadSpeedTextView.setText(getString(R.string.network_speed_s, downloadSpeed));
        }

        String uploadSpeed = data.getString(data.getColumnIndex(DETAIL_COLUMNS[8]));
        if(uploadSpeed.isEmpty())
        {
            mUploadSpeedTextView.setText(getString(R.string.no_data));
        }
        else
        {
            mUploadSpeedTextView.setText(getString(R.string.network_speed_s, uploadSpeed));
        }

        String pingTime = data.getString(data.getColumnIndex(DETAIL_COLUMNS[5]));
        if(pingTime.isEmpty())
        {
            mPingTimeTextView.setText(getString(R.string.no_data));
        }
        else
        {
            mPingTimeTextView.setText(getString(R.string.ping_time_s, pingTime));
        }

        String sampleSize = data.getString(data.getColumnIndex(DETAIL_COLUMNS[7]));
        if(sampleSize.isEmpty())
        {
            mSampleSizeRSSITextView.setText(getString(R.string.no_data));
        }
        else
        {
            mSampleSizeRSSITextView.setText(sampleSize);
        }

        String reliability = data.getString(data.getColumnIndex(DETAIL_COLUMNS[6]));
        if(reliability.isEmpty())
        {
            mReliabilityTextView.setText(getString(R.string.no_data));
        }
        else
        {
            mReliabilityTextView.setText(getString(R.string.reliability_s, reliability));
        }
    }

    private void setUpListeners()
    {
        mReliabilityRow.setOnClickListener(new RowClickListener(getActivity(),
                R.string.about_reliability_title, R.string.about_reliability_message));

        mRSSIAsuRow.setOnClickListener(new RowClickListener(getActivity(), R.string.about_rssi_asu_title,
                R.string.about_rssi_asu_message));

        mRSSIDbRow.setOnClickListener(new RowClickListener(getActivity(), R.string.about_rssi_db_title,
                R.string.about_rssi_db_message));

        mSampleRow.setOnClickListener(new RowClickListener(getActivity(), R.string.about_sample_title,
                R.string.about_sample_message));

        mDownloadRow.setOnClickListener(new RowClickListener(getActivity(), R.string.about_download_title,
                R.string.about_download_message));

        mUploadRow.setOnClickListener(new RowClickListener(getActivity(), R.string.about_upload_title,
                R.string.about_upload_message));

        mPingRow.setOnClickListener(new RowClickListener(getActivity(), R.string.about_ping_title,
                R.string.about_ping_message));
    }
}
