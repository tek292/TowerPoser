package com.riis.towerpower.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TowerDbHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "tower.db";

    public TowerDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(TowerContract.DbTower.createTable());
        db.execSQL(TowerContract.DbLocation.createTable());
        db.execSQL(TowerContract.DbLocationTower.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TowerContract.DbLocationTower.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TowerContract.DbTower.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TowerContract.DbLocation.TABLE_NAME);
        onCreate(db);
    }
}
