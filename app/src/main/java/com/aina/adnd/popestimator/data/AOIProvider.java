package com.aina.adnd.popestimator.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Tunde Aina on 5/1/2016.
 */
public class AOIProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int AOILOG_ID = 100;
    private static final int AOILOG = 101;
    private DbUtils dbUtils;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopEstimatorContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PopEstimatorContract.PATH_AOILOG+"/#", AOILOG_ID);
        matcher.addURI(authority, PopEstimatorContract.PATH_AOILOG, AOILOG);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbUtils = new DbUtils(getContext());
        return true;

    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case AOILOG_ID:
                return PopEstimatorContract.AOILogEntry.CONTENT_ITEM_TYPE;
            case AOILOG:
                return PopEstimatorContract.AOILogEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case AOILOG:
                retCursor=dbUtils.getReadableDatabase().query(
                        PopEstimatorContract.AOILogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection==null? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case AOILOG_ID:
                retCursor=dbUtils.getReadableDatabase().query(
                        PopEstimatorContract.AOILogEntry.TABLE_NAME,
                        projection,
                        PopEstimatorContract.AOILogEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbUtils.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case AOILOG: {
                long _id = db.insert(PopEstimatorContract.AOILogEntry.TABLE_NAME, null, values);
                if ( _id > 0 ){
                    returnUri = PopEstimatorContract.AOILogEntry.buildAOILogUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbUtils.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case AOILOG_ID:
                rowsDeleted = db.delete(
                        PopEstimatorContract.AOILogEntry.TABLE_NAME,
                        PopEstimatorContract.AOILogEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
