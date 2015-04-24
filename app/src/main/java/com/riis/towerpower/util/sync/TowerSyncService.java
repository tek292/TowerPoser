package com.riis.towerpower.util.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author tkocikjr
 */
public class TowerSyncService extends Service
{
    private static final Object mSyncAdapterLock = new Object();
    private static TowerSyncAdapter mTowerSyncAdapter = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
        synchronized (mSyncAdapterLock)
        {
            if (mTowerSyncAdapter == null)
            {
                mTowerSyncAdapter = new TowerSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mTowerSyncAdapter.getSyncAdapterBinder();
    }
}
