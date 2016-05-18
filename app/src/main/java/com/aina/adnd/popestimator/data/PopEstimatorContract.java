package com.aina.adnd.popestimator.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tunde Aina on 5/1/2016.
 */
public class PopEstimatorContract {

    public static final String CONTENT_AUTHORITY = "com.aina.adnd.popestimator";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_AOILOG = "aoilog";

    public static final class AOILogEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AOILOG).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_AOILOG;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_AOILOG;

        public static final String TABLE_NAME = "aoilog";

        public static final String PLACE_NAME = "placename";
        public static final String PLACE_DATA = "placedata";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String AOITYPE = "aoitype";
        public static final String REACH = "reach";
        public static final String RATE = "rate";

        public static Uri buildAOILogUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
