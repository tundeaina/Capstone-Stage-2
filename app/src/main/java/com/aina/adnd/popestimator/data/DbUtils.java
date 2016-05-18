package com.aina.adnd.popestimator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tunde Aina on 5/1/2016.
 */
public class DbUtils extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "popestimator.db";

    public DbUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_AOILOG_TABLE = "CREATE TABLE " +
                PopEstimatorContract.AOILogEntry.TABLE_NAME + " ("+
                PopEstimatorContract.AOILogEntry._ID + " INTEGER PRIMARY KEY," +
                PopEstimatorContract.AOILogEntry.PLACE_NAME + " TEXT NOT NULL," +
                PopEstimatorContract.AOILogEntry.PLACE_DATA + " TEXT NOT NULL," +
                PopEstimatorContract.AOILogEntry.LATITUDE + " REAL," +
                PopEstimatorContract.AOILogEntry.LONGITUDE + " REAL," +
                PopEstimatorContract.AOILogEntry.AOITYPE + " INTEGER," +
                PopEstimatorContract.AOILogEntry.REACH + " REAL," +
                PopEstimatorContract.AOILogEntry.RATE + " REAL )";

        Log.d("sql-statments",SQL_CREATE_AOILOG_TABLE);

        db.execSQL(SQL_CREATE_AOILOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
