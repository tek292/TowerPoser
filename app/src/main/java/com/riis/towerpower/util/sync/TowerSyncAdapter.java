package com.riis.towerpower.util.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Consts;
import com.riis.towerpower.models.TowerContract;
import com.riis.towerpower.ui.activity.MainActivity;
import com.riis.towerpower.util.TowerPowerRetriever;

import org.json.JSONException;

/**
 * @author tkocikjr
 */
public class TowerSyncAdapter extends AbstractThreadedSyncAdapter implements LocationListener
{
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private static final int TOWER_NOTIFICATION_ID = 1992;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private Context mContext;
    private Double mLatitude;
    private Double mLongitude;
    private Location mLocation;
    private LocationManager mLocationManager;

    public TowerSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        try
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
        catch (Exception e)
        {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult)
    {
        if(mLocation == null)
        {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String units = prefs.getString(mContext.getString(R.string.pref_units_key),
                mContext.getString(R.string.pref_units_kilometer));

        String distance = prefs.getString(mContext.getString(R.string.pref_distance_key),
                mContext.getString(R.string.pref_distance_default));

        if(units.equals(mContext.getString(R.string.pref_units_mile)))
        {
            distance = Double.toString(Consts.convertMilesToKilometers(Double.parseDouble(distance)));
        }

        Uri uri = TowerContract.DbLocation.buildLocationUri(mLatitude, mLongitude);

        Cursor cursor = mContext.getContentResolver().query(uri,
                null, null, null, null);

        cursor.moveToFirst();

        TowerPowerRetriever retriever = new TowerPowerRetriever(mContext);
        String response = retriever.send(mLatitude.toString(), mLongitude.toString(), distance);
        try
        {
            retriever.getTowerPower(response, cursor.getLong(0));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        retriever.deleteOldTowers();

        cursor.close();
        notifyTower();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        try {
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
            {
                showSettingsAlert();
            }
            else
            {
                if (isNetworkEnabled)
                {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (mLocation != null) {
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();

                        new TowerPowerRetriever(mContext).addLocation(mLatitude, mLongitude);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Consts.getLatitude(), Double.toString(mLatitude));
                        editor.putString(Consts.getLongitude(), Double.toString(mLongitude));
                        editor.apply();
                    }
                }

                if (isGPSEnabled)
                {
                    if (mLocation == null)
                    {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();

                            new TowerPowerRetriever(mContext).addLocation(mLatitude, mLongitude);

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Consts.getLatitude(), Double.toString(mLatitude));
                            editor.putString(Consts.getLongitude(), Double.toString(mLongitude));
                            editor.apply();
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        syncImmediately(mContext);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime)
    {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }
        else
        {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context)
    {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        if(null == accountManager.getPassword(newAccount))
        {
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
            {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Activity context)
    {
        getSyncAccount(context);
    }

    private static void onAccountCreated(Account newAccount, Context context)
    {
        /*
         * Since we've created an account
         */
        TowerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    private void notifyTower()
    {
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications)
        {
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS)
            {
                Uri uri = TowerContract.DbLocation.buildLocationUri(
                        Double.parseDouble(prefs.getString(Consts.getLatitude(), "37.7907")),
                        Double.parseDouble(prefs.getString(Consts.getLongitude(), "-122.4058")));

                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

                if (cursor.moveToFirst()) {
                    Resources resources = context.getResources();
                    String title = context.getString(R.string.app_name);

                    String contentText = mContext.getString(R.string.new_towers);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setColor(resources.getColor(android.R.color.holo_blue_light))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    Intent resultIntent = new Intent(context, MainActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                            0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(TOWER_NOTIFICATION_ID, mBuilder.build());

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.apply();
                }
                cursor.close();
            }
        }
    }

    /**
     * Function to show settings alert dialog
     */
    private void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(mContext.getString(R.string.gps_not_enabled_title));
        alertDialog.setMessage(mContext.getString(R.string.gps_not_enabled_message));

        alertDialog.setPositiveButton(mContext.getString(R.string.action_settings),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int which)
                    {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(mContext.getString(android.R.string.cancel), null);

        alertDialog.show();
    }
}
