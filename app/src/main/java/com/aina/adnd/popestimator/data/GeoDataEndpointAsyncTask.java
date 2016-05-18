package com.aina.adnd.popestimator.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.util.Log;

import com.aina.adnd.popestimator.backend.popEstimatorAPI.PopEstimatorAPI;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;


public class GeoDataEndpointAsyncTask extends AsyncTask<Pair<Context, Double[]>, Void, String> {

    private static PopEstimatorAPI popEstimatorAPI = null;
    private Context context;
    public static final String LOG_TAG = GeoDataEndpointAsyncTask.class.getSimpleName();
    public static final String NO_RESULT = null;

    @Override
    protected String doInBackground(Pair<Context, Double[]>... params)  {

        if(popEstimatorAPI == null) {
//
//            // Only do this once
//            PopEstimatorAPI.Builder builder = new PopEstimatorAPI.Builder(
//                    AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                    // options for running against local devappserver
//                    // - 10.0.2.2 is localhost's IP address in Android emulator
//                    // - turn off compression when running against local devappserver
//                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                        @Override
//                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
//                                throws IOException {
//                            abstractGoogleClientRequest.setDisableGZipContent(true);
//                        }
//                    });
//            // end options for devappserver
            PopEstimatorAPI.Builder builder = new PopEstimatorAPI.Builder(
                    AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://nomadic-autumn-121504.appspot.com/_ah/api/");

            popEstimatorAPI = builder.build();
        }

        context = params[0].first;
        Double[] aoiDef = params[0].second;

        try {
            return String.valueOf(popEstimatorAPI.getGeoData(
                    aoiDef[0],
                    aoiDef[1],
                    aoiDef[2].intValue(),
                    aoiDef[3],
                    aoiDef[4].intValue()).execute().getGeoDemoData());
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        Intent intent = new Intent("GeoDataRetrieved");

        if(result.equals("null")) {
            intent.putExtra("GeoData", NO_RESULT);
        }
        else{
            intent.putExtra("GeoData", "{\"data\":" + result + "}");
            Log.d(LOG_TAG, "GeoData: " + result);
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
