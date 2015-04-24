package com.riis.towerpower.util.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author tkocikjr
 */
public class TowerAuthenticatorService extends Service
{
    private TowerAuthenticator mTowerAuthenticator;

    @Override
    public void onCreate()
    {
        mTowerAuthenticator = new TowerAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mTowerAuthenticator.getIBinder();
    }
}
