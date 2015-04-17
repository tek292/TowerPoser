package com.riis.towerpower.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * @author tkocikjr
 */
public class TowerRetrievalTask extends AsyncTask<Void, Void, Void>
{
    private final GPSTrackerService mGPSTrackerService;

    private ArrayList<Tower> mFullTowerList;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public TowerRetrievalTask(Context context)
    {
        mContext = context;
        mGPSTrackerService = new GPSTrackerService(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.retrieving_tower_info));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        Double latitude = mGPSTrackerService.getLatitude();
        Double longitude = mGPSTrackerService.getLongitude();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String distance = prefs.getString(mContext.getString(R.string.pref_distance_key),
                mContext.getString(R.string.pref_distance_default));

//        TowerPowerRetriever retriever = new TowerPowerRetriever();
//        String response = retriever.send(latitude.toString(), longitude.toString(), distance);
//        try
//        {
//            mFullTowerList = retriever.getTowerPower(response);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }


        // Reference: http://stackoverflow.com/questions/4152373/how-to-know-location-area-code-and-cell-id-in-android-phone
//            final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
//                final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
//                if (location != null) {
//                    Log.w("TESTING", "LAC: " + location.getLac() + " CID: " + location.getCid());
//                }
//            }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mProgressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        sortTowerDataByNetworkName(mFullTowerList);
//        sortTowerDataByNetworkType();
//        notifyDataSetChanged();
        mProgressDialog.dismiss();
    }
}
