package com.aina.adnd.popestimator.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.aina.adnd.popestimator.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ReverseGeocodeService extends IntentService {

    private final String LOG_TAG = ReverseGeocodeService.class.getSimpleName();
    private static final String SERVICE_TAG = "ReverseGeocoder";
    public static final int FAILURE = 1;
    public static final int SUCCESS = 0;
    public static final String RECEIVER = SERVICE_TAG + ".Receiver";
    public static final String INPUT = SERVICE_TAG + ".Input";
    public static final String RESULTS = SERVICE_TAG + ".Result";

    protected ResultReceiver mReceiver;

    public ReverseGeocodeService() {
        super(SERVICE_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(RECEIVER);

        if (mReceiver == null) {
            return;
        }

        LatLng latLng = intent.getParcelableExtra(INPUT);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try
        {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(LOG_TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {

            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(LOG_TAG, errorMessage + ". " +
                    "Latitude = " + latLng.latitude +
                    ", Longitude = " + latLng.longitude, illegalArgumentException);
        }

        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(LOG_TAG, errorMessage);
            }
            deliverResultToReceiver(FAILURE, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(LOG_TAG, getString(R.string.address_found));
            deliverResultToReceiver(SUCCESS, TextUtils.join(", ", addressFragments));
        }
    }


    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULTS, message);
        mReceiver.send(resultCode, bundle);
    }

}
