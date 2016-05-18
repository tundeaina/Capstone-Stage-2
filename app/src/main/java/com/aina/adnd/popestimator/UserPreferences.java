package com.aina.adnd.popestimator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Tunde Aina on 4/27/2016.
 */
public class UserPreferences {

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    static String DEFAULT_PLACE_NAME = "";
    static int DEFAULT_AOI_TYPE = 0;
    static int DEFAULT_MINUTES = 10;
    static int DEFAULT_MILES = 3;
    static long DEFAULT_RATE = Double.doubleToRawLongBits(75.0);
    static long DEFAULT_LNG = Double.doubleToRawLongBits(-97.936355);
    static long DEFAULT_LAT = Double.doubleToRawLongBits(38.833925);
    static String DEFAULT_ESTIMATES = "";
    static String DEFAULT_AOI_DESC = "";

    static final String PREF_USER_PLACE_NAME = "PlaceName";
    static final String PREF_USER_AOI_TYPE = "AoiType";
    static final String PREF_USER_AOI_DESC = "AoiDesc";
    static final String PREF_USER_MILES = "Miles";
    static final String PREF_USER_MINUTES = "Minutes";
    static final String PREF_USER_RATE = "Rate";
    static final String PREF_USER_LAT = "Latitude";
    static final String PREF_USER_LNG = "Longitude";
    static final String PREF_USER_ESTIMATES = "Estimates";


    public static void setUserPlaceName(Context context, String placename) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_PLACE_NAME, placename);
        editor.apply();
    }

    public static String getUserPlaceName(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_PLACE_NAME,DEFAULT_PLACE_NAME);
    }

    public static void setUserAoiType(Context context, int aoitype) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_USER_AOI_TYPE, aoitype);
        editor.apply();
    }

    public static int getUserAoi(Context context) {
        return getSharedPreferences(context).getInt(PREF_USER_AOI_TYPE,DEFAULT_AOI_TYPE);
    }

    public static void setUserMiles(Context context, int miles) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_USER_MILES, miles);
        editor.apply();
    }

    public static int getUserMiles(Context context) {
        return getSharedPreferences(context).getInt(PREF_USER_MILES,DEFAULT_MILES);
    }

    public static void setUserMinutes(Context context, int minutes) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_USER_MINUTES, minutes);
        editor.apply();
    }

    public static int getUserMinutes(Context context) {
        return getSharedPreferences(context).getInt(PREF_USER_MINUTES,DEFAULT_MINUTES);
    }

    public static void setUserRate(Context context, double rate) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(PREF_USER_RATE, Double.doubleToRawLongBits(rate));
        editor.apply();
    }

    public static double getUserRate(Context context) {
        return Double.longBitsToDouble(getSharedPreferences(context).getLong(
                PREF_USER_RATE,DEFAULT_RATE));
    }

    public static void setUserLatitude(Context context, double latitude) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(PREF_USER_LAT, Double.doubleToRawLongBits(latitude));
        editor.apply();
    }

    public static double getUserLatitude(Context context) {
        return Double.longBitsToDouble(getSharedPreferences(context).getLong(
                PREF_USER_LAT,DEFAULT_LAT));
    }

    public static void setUserLongitude(Context context, double longitude) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(PREF_USER_LNG, Double.doubleToRawLongBits(longitude));
        editor.apply();
    }

    public static double getUserLongitude(Context context) {
        return Double.longBitsToDouble(getSharedPreferences(context).getLong(
                PREF_USER_LNG,DEFAULT_LNG));
    }

    public static void setUserEstimates(Context context, String estimates) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_ESTIMATES, estimates);
        editor.apply();
    }

    public static String getUserEstimates(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_ESTIMATES,DEFAULT_ESTIMATES);
    }

    public static void setUserAoi_Desc(Context context, String aoi_desc) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_AOI_DESC, aoi_desc);
        editor.apply();
    }

    public static String getUserAoi_Desc(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_AOI_DESC,DEFAULT_AOI_DESC);
    }
}
