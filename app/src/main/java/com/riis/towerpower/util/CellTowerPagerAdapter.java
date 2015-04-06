package com.riis.towerpower.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Tower;
import com.riis.towerpower.models.eNetworkType;
import com.riis.towerpower.ui.fragment.TowerPageFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author tkocikjr
 */
public class CellTowerPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<Tower> mAttTowerList = new ArrayList<>();
    private ArrayList<Tower> mOtherTowerList = new ArrayList<>();
    private ArrayList<Tower> mSprintTowerList = new ArrayList<>();
    private ArrayList<Tower> mTMobileTowerList = new ArrayList<>();
    private ArrayList<Tower> mVerizonTowerList = new ArrayList<>();
    private Context mContext;
    private GPSTrackerService mGPSTrackerService;

    public CellTowerPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        mContext = context;
        mGPSTrackerService = new GPSTrackerService(mContext);
        new TowerRetrievalTask(mContext).execute();
    }

    @Override
    public Fragment getItem(int position)
    {
        TowerPageFragment towerPageFragment;
        switch (position)
        {
            case 0:
                towerPageFragment = new TowerPageFragment().newInstance(eNetworkType.ATT, mAttTowerList);
                break;
            case 1:
                towerPageFragment = new TowerPageFragment().newInstance(eNetworkType.SPRINT, mSprintTowerList);
                break;
            case 2:
                towerPageFragment = new TowerPageFragment().newInstance(eNetworkType.TMOBILE, mTMobileTowerList);
                break;
            case 3:
                towerPageFragment = new TowerPageFragment().newInstance(eNetworkType.VERIZON, mVerizonTowerList);
                break;
            default:
                towerPageFragment = new TowerPageFragment().newInstance(eNetworkType.OTHER, mOtherTowerList);
                break;
        }
        return towerPageFragment;
    }

    @Override
    public int getCount()
    {
        return 5;
    }

    private void sortTowerDataByNetworkName(ArrayList<Tower> fullTowerList)
    {
        for(Tower tower : fullTowerList)
        {
            if(tower.getNetworkName().equals(mContext.getString(R.string.at_t)))
            {
                mAttTowerList.add(tower);
            }
            else if(tower.getNetworkName().equals(mContext.getString(R.string.sprint)))
            {
                mSprintTowerList.add(tower);
            }
            else if(tower.getNetworkName().equals(mContext.getString(R.string.t_mobile)))
            {
                mTMobileTowerList.add(tower);
            }
            else if(tower.getNetworkName().equals(mContext.getString(R.string.verizon)))
            {
                mVerizonTowerList.add(tower);
            }
            else
            {
                mOtherTowerList.add(tower);
            }
        }
    }

    private void sortTowerDataByNetworkType()
    {
        Comparator<Tower> comparator = new Comparator<Tower>()
        {
            @Override
            public int compare(Tower lhs, Tower rhs)
            {
                return lhs.getNetworkType() - rhs.getNetworkType();
            }
        };

        Collections.sort(mAttTowerList, comparator);
        Collections.sort(mSprintTowerList, comparator);
        Collections.sort(mTMobileTowerList, comparator);
        Collections.sort(mVerizonTowerList, comparator);
        Collections.sort(mOtherTowerList, comparator);
    }

    private class TowerRetrievalTask extends AsyncTask<Void, Void, Void>
    {
        private ArrayList<Tower> mFullTowerList;
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public TowerRetrievalTask(Context context)
        {
            mContext = context;
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

            TowerPowerRetriever retriever = new TowerPowerRetriever();
            String response = retriever.send(latitude.toString(), longitude.toString(), distance);
            try
            {
                mFullTowerList = retriever.getTowerPower(response);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


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
            sortTowerDataByNetworkName(mFullTowerList);
            sortTowerDataByNetworkType();
            notifyDataSetChanged();
            mProgressDialog.dismiss();
        }
    }
}
