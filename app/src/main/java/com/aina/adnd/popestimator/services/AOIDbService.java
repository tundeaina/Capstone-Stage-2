package com.aina.adnd.popestimator.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import com.aina.adnd.popestimator.data.PopEstimatorContract;

public class AOIDbService extends IntentService {
    private final String LOG_TAG = AOIDbService.class.getSimpleName();
    private static final String SERVICE_TAG = "popEstimator";
    public static final String ADD_TO_LOG = SERVICE_TAG + ".ADD_TO_LOG";
    public static final String DELETE_LOG_ENTRY = SERVICE_TAG + ".DELETE_LOG_ENTRY";

    public AOIDbService() {
        super(SERVICE_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ADD_TO_LOG.equals(action)) {
                final long placeId = intent.getLongExtra(
                        PopEstimatorContract.AOILogEntry._ID,0);
                final String placeName = intent.getStringExtra(
                        PopEstimatorContract.AOILogEntry.PLACE_NAME);
                final String placeData = intent.getStringExtra(
                        PopEstimatorContract.AOILogEntry.PLACE_DATA);
                final double latitude = intent.getDoubleExtra(
                        PopEstimatorContract.AOILogEntry.LATITUDE,38.732853);
                final double longitude = intent.getDoubleExtra(
                        PopEstimatorContract.AOILogEntry.LONGITUDE,-98.227576);
                final int aoiType = intent.getIntExtra(
                        PopEstimatorContract.AOILogEntry.AOITYPE,0);
                final double reach = intent.getDoubleExtra(
                        PopEstimatorContract.AOILogEntry.REACH,3);
                final double rate = intent.getDoubleExtra(
                        PopEstimatorContract.AOILogEntry.RATE,0.0);

                AddToLog(placeId, placeName,placeData,latitude, longitude, aoiType,reach,rate);
            } else if (DELETE_LOG_ENTRY.equals(action)) {
                final long placeId = intent.getLongExtra(
                        PopEstimatorContract.AOILogEntry._ID,0);
                deleteLogEntry(placeId);
            }
        }
    }

    private void AddToLog(long placeId, String placeName, String placeData,
                          double latitude, double longitude,
                          int aoiType, double reach,double rate) {

        Cursor AOILogEntry = getContentResolver().query(
                PopEstimatorContract.AOILogEntry.buildAOILogUri(placeId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if(AOILogEntry.getCount()>0){
            AOILogEntry.close();
            return;
        }

        AOILogEntry.close();

        insertLogEntry(placeId, placeName,placeData,latitude, longitude, aoiType,reach,rate);
    }

    private void deleteLogEntry(long placeid) {
        getContentResolver().delete(PopEstimatorContract.AOILogEntry.buildAOILogUri(placeid), null, null);
    }

    private void insertLogEntry(long placeId, String placeName, String placeData,
                               double latitude, double longitude,
                               int aoiType, double reach, double rate) {

        ContentValues values= new ContentValues();
        values.put(PopEstimatorContract.AOILogEntry._ID, placeId);
        values.put(PopEstimatorContract.AOILogEntry.PLACE_NAME, placeName);
        values.put(PopEstimatorContract.AOILogEntry.PLACE_DATA, placeData);
        values.put(PopEstimatorContract.AOILogEntry.LATITUDE, latitude);
        values.put(PopEstimatorContract.AOILogEntry.LONGITUDE, longitude);
        values.put(PopEstimatorContract.AOILogEntry.AOITYPE, aoiType);
        values.put(PopEstimatorContract.AOILogEntry.REACH, reach);
        values.put(PopEstimatorContract.AOILogEntry.RATE, rate);

        getContentResolver().insert(PopEstimatorContract.AOILogEntry.CONTENT_URI,values);

    }
}
