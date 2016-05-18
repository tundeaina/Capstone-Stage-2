package com.aina.adnd.popestimator;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aina.adnd.popestimator.data.DataExport;
import com.aina.adnd.popestimator.services.AOIDbService;
import com.aina.adnd.popestimator.data.GeoDataEndpointAsyncTask;
import com.aina.adnd.popestimator.data.PopEstimatorContract;
import com.aina.adnd.popestimator.services.ReverseGeocodeService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AOIDialogFragment.AOIDialogListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String sharedString;

    final double METERS = 1609.344;
    final double SECONDS = 60;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    final double DEFAULT_LNG = -97.936355;
    final double DEFAULT_LAT = 38.833925;
    final float DEFAULT_ZOOM = 21;
    final String SHOWING_AOILOG = "ShowingAoiLog";
    LatLngBounds BOUNDS_US = new LatLngBounds(
            new LatLng(18.15, -168.36),
            new LatLng(71.46, -62.22));


    GoogleMap mMap;
    boolean mapReady = false;
    LatLng mCurrentLatLng = null;
    boolean mTablet;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    Bundle estimates = new Bundle();
    int aoitype = -1;
    String aoi_desc = null;
    double reach = 0;
    double rate = 75.0;
    double mLatitude = 0;
    double mLongitude = 0;
    String place_name = null;
    String place_data = null;
    boolean mHistorical = false;
    boolean mSavedState = false;
    boolean mIsShowingAoiLog = false;
    ReverseGeocodeResultReceiver mResultReceiver;

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (!intent.getStringExtra("GeoData").equals(GeoDataEndpointAsyncTask.NO_RESULT)) {
                    place_data = intent.getStringExtra("GeoData");
                    List<Object> data = extractData(place_data);

                    setUserPreferences(mCurrentLatLng.latitude, mCurrentLatLng.longitude,
                            place_name, rate, aoitype, reach, (Bundle) data.get(0));

                    if(0==aoitype)
                        aoi_desc = (int)Math.round(reach/METERS) + " " + getString(R.string.mile_radius);
                    else
                        aoi_desc = (int)Math.round(reach/SECONDS)+ " " + getString(R.string.minute_drive_time);

                    Bundle shareBundle = (Bundle)data.get(0);
                    shareBundle.putString("AOI_DESC", aoi_desc);
                    shareBundle.putString("PLACE_NAME",place_name);
                    sharedString = DataExport.getSharedData(shareBundle);

                    loadMapView(data);
                    loadReportView(data);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.service_not_available),
                            Toast.LENGTH_SHORT).show();
                }

            }
            catch(Exception e){
                Toast.makeText(MainActivity.this, getString(R.string.service_not_available),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void loadMapView(List<Object> data){

        LatLngBounds bounds = (LatLngBounds) data.get(1);
        LatLng[] vertices = (LatLng[]) data.get(2);

        if(mapReady) {
            adjustMap(vertices, bounds);
        }
    }

    private void loadReportView(List<Object> data){
        if (mTablet && !mIsShowingAoiLog) {

            mIsShowingAoiLog = false;
            Bundle bundle = (Bundle)data.get(0);
            bundle.putInt("TABLET",1);

            if(0==aoitype)
                aoi_desc = (int)Math.round(reach/METERS) + " " + getString(R.string.mile_radius);
            else
                aoi_desc = (int)Math.round(reach/SECONDS)+ " " + getString(R.string.minute_drive_time);

            bundle.putString("AOI_DESC", aoi_desc);
            bundle.putString("PLACE_NAME",place_name);

            AOIReportFragment reportFragment = new AOIReportFragment();
            reportFragment.setArguments((Bundle)data.get(0));
            reportFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragments_container, reportFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void loadAoiLogView(){
        mIsShowingAoiLog = true;
        Bundle bundle = new Bundle();
        bundle.putInt("TABLET",1);
        AOILogFragment aoiLogFragment = new AOILogFragment();

        aoiLogFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragments_container, aoiLogFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putString(PopEstimatorContract.AOILogEntry.PLACE_NAME, place_name);
        savedState.putString(PopEstimatorContract.AOILogEntry.PLACE_DATA, place_data);
        savedState.putInt(PopEstimatorContract.AOILogEntry.AOITYPE, aoitype);
        savedState.putDouble(PopEstimatorContract.AOILogEntry.LATITUDE, mLatitude);
        savedState.putDouble(PopEstimatorContract.AOILogEntry.LONGITUDE, mLongitude);
        savedState.putDouble(PopEstimatorContract.AOILogEntry.REACH, reach);
        savedState.putDouble(PopEstimatorContract.AOILogEntry.RATE, rate);
        savedState.putBoolean(SHOWING_AOILOG, mIsShowingAoiLog);
        super.onSaveInstanceState(savedState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO persist states on Rotate and on Navigate Back
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mIsShowingAoiLog = savedInstanceState.getBoolean(SHOWING_AOILOG);

        mResultReceiver = new ReverseGeocodeResultReceiver(new Handler());

        mHistorical = savedInstanceState == null &&
                getIntent().getBooleanExtra(AOILogFragment.HISTORICAL, false);

        if (mHistorical)
        {
            Bundle extras = getIntent().getExtras();

            if (extras.containsKey(PopEstimatorContract.AOILogEntry.PLACE_NAME)) {
                place_name = extras.getString(PopEstimatorContract.AOILogEntry.PLACE_NAME);
                place_data = extras.getString(PopEstimatorContract.AOILogEntry.PLACE_DATA);
                aoitype = extras.getInt(PopEstimatorContract.AOILogEntry.AOITYPE);
                mLatitude = extras.getDouble(PopEstimatorContract.AOILogEntry.LATITUDE);
                mLongitude = extras.getDouble(PopEstimatorContract.AOILogEntry.LONGITUDE);
                reach = extras.getDouble(PopEstimatorContract.AOILogEntry.REACH);
                rate = extras.getDouble(PopEstimatorContract.AOILogEntry.RATE);
            }
        }
        else if (savedInstanceState != null) {
            mSavedState = true;
            place_name = savedInstanceState.getString(PopEstimatorContract.AOILogEntry.PLACE_NAME);
            place_data = savedInstanceState.getString(PopEstimatorContract.AOILogEntry.PLACE_DATA);
            aoitype = savedInstanceState.getInt(PopEstimatorContract.AOILogEntry.AOITYPE);
            mLatitude = savedInstanceState.getDouble(PopEstimatorContract.AOILogEntry.LATITUDE);
            mLongitude = savedInstanceState.getDouble(PopEstimatorContract.AOILogEntry.LONGITUDE);
            reach = savedInstanceState.getDouble(PopEstimatorContract.AOILogEntry.REACH);
            rate = savedInstanceState.getDouble(PopEstimatorContract.AOILogEntry.RATE);
        }
        else
        {
            aoitype = UserPreferences.getUserAoi(this);

            if (aoitype == 0)
                reach = UserPreferences.getUserMiles(this) * METERS;

            if (aoitype == 1)
                reach = UserPreferences.getUserMinutes(this) * SECONDS;

            rate = UserPreferences.getUserRate(this);
            mLatitude = UserPreferences.getUserLatitude(this);
            mLongitude = UserPreferences.getUserLongitude(this);
            place_name = UserPreferences.getUserPlaceName(this);
        }

        mCurrentLatLng = new LatLng(mLatitude, mLongitude);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTablet = findViewById(R.id.fragments_container) != null;

        ImageButton myLocationButton = (ImageButton) findViewById(R.id.myLocation);
        if (myLocationButton != null) {
            myLocationButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (null != mLocation) {
                        mCurrentLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        startReverseGeocoding();
                    }
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    AOIDialogFragment aoiDialogFragment = new AOIDialogFragment();
                    aoiDialogFragment.show(fm, "fragment_aoidialog");
                }
            });
        }

        FloatingActionButton fabReport = (FloatingActionButton) findViewById(R.id.fabReport);
        if(mTablet && fabReport != null){
            fabReport.setEnabled(true);
            fabReport.setVisibility(View.INVISIBLE);
        }
        else {
            fabReport.setVisibility(View.VISIBLE);
        }

        if (fabReport != null) {

            fabReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                mIsShowingAoiLog = false;

                List<Object> data = extractData(place_data);
                estimates = (Bundle)data.get(0);

                setUserPreferences(mCurrentLatLng.latitude, mCurrentLatLng.longitude,
                        place_name, rate, aoitype, reach, estimates);

                if (!mTablet && estimates != null) {
                    Intent intent = new Intent(MainActivity.this, AOIReportActivity.class);
                    intent.putExtras(estimates);
                    intent.putExtra("TABLET",0);

                    if(0==aoitype)
                        intent.putExtra("AOI_DESC",(int)Math.round(reach/METERS) + " " + getString(R.string.mile_radius));
                    else
                        intent.putExtra("AOI_DESC",(int)Math.round(reach/SECONDS)+ " " + getString(R.string.minute_drive_time));

                    intent.putExtra("PLACE_NAME",place_name);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this)
                                .toBundle();

                        startActivity(intent,b);
                    }
                    else
                    {
                        startActivity(intent);
                    }
                }
                else loadReportView(data);
                }
            });
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("GeoDataRetrieved"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (!mTablet) {
            MenuItem item = menu.findItem(R.id.menu_item_share);
            item.setVisible(false);
        }

        this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                try {
                    Intent intent =
                            new PlaceAutocomplete
                                    .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setBoundsBias(BOUNDS_US)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                return true;
            case R.id.menu_item_aoilog:

                if (mTablet) {
                    loadAoiLogView();
                }
                else{
                    Intent intent = new Intent(MainActivity.this, AOILogActivity.class);
                    intent.putExtra("TABLET",0);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this)
                                .toBundle();

                        startActivity(intent,b);
                    }
                    else
                    {
                        startActivity(intent);
                    }


                }
                return true;
            case R.id.menu_item_save:

                Intent AddToLogIntent = new Intent(MainActivity.this, AOIDbService.class);

                long place_id = new Date().getTime();

                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry._ID, place_id);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.PLACE_NAME,place_name);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.PLACE_DATA,place_data);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.LATITUDE,mCurrentLatLng.latitude);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.LONGITUDE,mCurrentLatLng.longitude);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.AOITYPE,aoitype);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.REACH,reach);
                AddToLogIntent.putExtra(PopEstimatorContract.AOILogEntry.RATE,rate);

                if(place_name!=null && place_data!= null) {

                    AddToLogIntent.setAction(AOIDbService.ADD_TO_LOG);
                    startService(AddToLogIntent);
                    Toast.makeText(this, getString(R.string.added) +" "+ place_name, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, getString(R.string.nothing_added), Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_item_share:
                shareData();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);

                if(place.getAddress().toString().length()> 0)
                    place_name = place.getAddress().toString();

                mCurrentLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                getEstimates(mCurrentLatLng, reach, rate, aoitype);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (null != mLocation && DEFAULT_LAT == mLatitude && DEFAULT_LNG == mLongitude) {
            mCurrentLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            //Reverse Geocode
            startReverseGeocoding();
        } else {
            if(!mHistorical & !mSavedState) {
                mCurrentLatLng = new LatLng(mLatitude, mLongitude);
                getEstimates(mCurrentLatLng, reach, rate, aoitype);
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapReady = true;
        mMap = map;
        CameraPosition target = CameraPosition.builder()
                .target(new LatLng(DEFAULT_LAT, DEFAULT_LNG))
                .zoom(DEFAULT_ZOOM)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

        if(mHistorical || mSavedState){
            List<Object> data = extractData(place_data);

            setUserPreferences(mCurrentLatLng.latitude, mCurrentLatLng.longitude,
                    place_name, rate, aoitype, reach, (Bundle)data.get(0));

            Bundle shareBundle = (Bundle)data.get(0);
            shareBundle.putString("AOI_DESC", aoi_desc);
            shareBundle.putString("PLACE_NAME",place_name);
            sharedString = DataExport.getSharedData(shareBundle);

            loadMapView(data);
            loadReportView(data);
        }
    }


    @Override
    public void onDialogPositiveClick(Bundle bundle) {

        aoitype = bundle.getInt("t");

        if (aoitype == 0) {
            reach = bundle.getInt("v") * METERS;
        } else {
            reach = bundle.getInt("v") * SECONDS;
            rate = getAvgSpeed(bundle.getInt("v"));
        }

        getEstimates(mCurrentLatLng, reach, rate, aoitype);
    }


    public void setUserPreferences(double latitude, double longitude, String placename,
                                   double rate, int aoitype, double reach, Bundle estimates){

        UserPreferences.setUserLongitude(this, longitude);
        UserPreferences.setUserLatitude(this, latitude);
        UserPreferences.setUserPlaceName(this, placename);
        UserPreferences.setUserRate(this,rate);
        UserPreferences.setUserAoiType(this,aoitype);

        if(0==aoitype) {
            UserPreferences.setUserMiles(this, (int)Math.round(reach/METERS));
            UserPreferences.setUserAoi_Desc(this, (int)Math.round(reach/METERS)+ " " + getString(R.string.mile_radius));
        }
        else {
            UserPreferences.setUserMinutes(this, (int)Math.round(reach/SECONDS));
            UserPreferences.setUserAoi_Desc(this,(int)Math.round(reach/SECONDS)+ " " + getString(R.string.minute_drive_time));
        }

        UserPreferences.setUserEstimates(this, getSummaryEstimates(estimates));
    }

    public String getSummaryEstimates(Bundle estimates){

        return Integer.toString(estimates.getInt("_Amerindian"))+"|"+
                Integer.toString(estimates.getInt("_Asian"))+"|"+
                Integer.toString(estimates.getInt("_Black"))+"|"+
                Integer.toString(estimates.getInt("_Hispanic"))+"|"+
                Integer.toString(estimates.getInt("_PacIslander"))+"|"+
                Integer.toString(estimates.getInt("_White"))+"|"+
                Integer.toString(estimates.getInt("_Other"))+"|"+
                Double.toString(estimates.getDouble("_Amerindian2"))+"|"+
                Double.toString(estimates.getDouble("_Asian2"))+"|"+
                Double.toString(estimates.getDouble("_Black2"))+"|"+
                Double.toString(estimates.getDouble("_Hispanic2"))+"|"+
                Double.toString(estimates.getDouble("_PacIslander2"))+"|"+
                Double.toString(estimates.getDouble("_White2"))+"|"+
                Double.toString(estimates.getDouble("_Other2"));
    }

    public void getEstimates(LatLng pt, double reach, double rate, int aoitype) {
        mHistorical = false;
        mSavedState = false;

        Log.d(LOG_TAG, "Inputs: ------------> "
                + pt.latitude + " " +
                + pt.longitude + " " +
                + reach + " " +
                + rate + " " +
                + aoitype );

        Double[] aoiparams = {
                pt.latitude,
                pt.longitude,
                reach,
                rate,
                (double) aoitype};

        Pair aoiPayload = new Pair(this, aoiparams);

        if (isNetworkAvailable()) {
        GeoDataEndpointAsyncTask geoDataEndpointAsyncTask = new GeoDataEndpointAsyncTask();
        geoDataEndpointAsyncTask.execute(aoiPayload);
        } else {
            Toast.makeText(this
                    , getResources().getString(R.string.prompt_connectivity)
                    , Toast.LENGTH_LONG).show();
        }
    }

    public double getAvgSpeed(int minutes) {
        if (minutes <= 5) {
            return 35;
        } else if (minutes > 5 && minutes <= 10) {
            return 45;
        } else if (minutes > 10 && minutes <= 15) {
            return 55;
        } else return 75;
    }

    public List<Object> extractData(String json) {

        List<Object> data = new ArrayList<>();
        Bundle estimates = new Bundle();

        try {
            JSONObject jo = new JSONObject(json);
            JSONArray elements = jo.getJSONArray("data");

            //Extract popEstimates
            JSONArray aoiEstimates = (JSONArray) elements.get(2);
            for (int i = 0; i < aoiEstimates.length(); i++) {

                JSONArray estimate = (JSONArray) aoiEstimates.get(i);
                estimates.putInt("_" + estimate.get(0).toString().replace(".", "")
                        ,(int) estimate.get(1));
                estimates.putDouble("_" + estimate.get(0).toString().replace(".", "") + "2"
                        ,(double) estimate.get(2));
            }
            data.add(estimates);

            //Extract Bounds
            JSONArray mbr = (JSONArray) elements.get(0);
            JSONArray sw = (JSONArray) mbr.get(0);
            JSONArray ne = (JSONArray) mbr.get(1);

            LatLng southwest = new LatLng((double) sw.get(1), (double) sw.get(0));
            LatLng northeast = new LatLng((double) ne.get(1), (double) ne.get(0));

            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
            data.add(bounds);

            //Extract Polygon
            JSONArray aoiVertices = (JSONArray) elements.get(1);
            LatLng[] vertices = new LatLng[aoiVertices.length()];

            for (int i = 0; i < aoiVertices.length(); i++) {
                JSONArray vertex = (JSONArray) aoiVertices.get(i);
                vertices[i] = new LatLng((double) vertex.get(2), (double) vertex.get(1));
            }

            //Smooth Polygon
            if (aoitype == 1)
            {
                LatLng[] extVertices = new LatLng[vertices.length+4];

                extVertices[0] = vertices[vertices.length-2];
                extVertices[1] = vertices[vertices.length-1];
                extVertices[extVertices.length-2] = vertices[0];
                extVertices[extVertices.length-1] = vertices[1];

                for (int i = 0; i < vertices.length; i++) {
                    extVertices[i+2] = vertices[i];
                }

                LatLng[] smoothVertices = bSpline(extVertices);

                data.add(smoothVertices);
            }
            else{
                data.add(vertices);
            }

        } catch (Exception e) {
        }

        return data;
    }

    public void adjustMap(LatLng[] vertices, LatLngBounds bounds) {

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.add(vertices)
                .strokeColor(Color.argb(100, 126, 87, 194))
                .strokeWidth(3)
                .fillColor(Color.argb(80, 179, 157, 219));

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        mMap.addPolygon(polygonOptions);

        mMap.addMarker(new MarkerOptions()
                        .position(mCurrentLatLng)
                        .title(mCurrentLatLng.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin))
                        .title(place_name)
                        .snippet(aoi_desc));

    }

    public LatLng[] bSpline(LatLng[] vertices) {

        List<LatLng> smoothVertices = new ArrayList<>();
        int i;
        double t;
        double ax, ay, bx, by, cx, cy, dx, dy, lat, lon;

        for (i = 2; i < vertices.length - 2; i++) {
            for (t = 0; t < 1; t += 0.2) {
                ax = (-vertices[i - 2].latitude
                        + 3 * vertices[i - 1].latitude - 3 * vertices[i].latitude
                        + vertices[i + 1].latitude) / 6;
                ay = (-vertices[i - 2].longitude
                        + 3 * vertices[i - 1].longitude - 3 * vertices[i].longitude
                        + vertices[i + 1].longitude) / 6;
                bx = (vertices[i - 2].latitude - 2 * vertices[i - 1].latitude
                        + vertices[i].latitude) / 2;
                by = (vertices[i - 2].longitude - 2 * vertices[i - 1].longitude
                        + vertices[i].longitude) / 2;
                cx = (-vertices[i - 2].latitude + vertices[i].latitude) / 2;
                cy = (-vertices[i - 2].longitude + vertices[i].longitude) / 2;
                dx = (vertices[i - 2].latitude + 4 * vertices[i - 1].latitude
                        + vertices[i].latitude) / 6;
                dy = (vertices[i - 2].longitude + 4 * vertices[i - 1].longitude
                        + vertices[i].longitude) / 6;
                lat = ax * Math.pow(t + 0.1, 3) + bx * Math.pow(t + 0.1, 2) + cx * (t + 0.1) + dx;
                lon = ay * Math.pow(t + 0.1, 3) + by * Math.pow(t + 0.1, 2) + cy * (t + 0.1) + dy;
                smoothVertices.add(new LatLng(lat, lon));
            }
        }

        LatLng[] v = new LatLng[smoothVertices.size()+1];
        try {

            for (i = 0; i < smoothVertices.size(); i++){
                v[i] = smoothVertices.get(i);
            }

            v[v.length-1] = v[0];
        }
        catch(Exception e){
            return vertices;
        }
        return v;
    }

    public void startReverseGeocoding(){
        //Reverse Geocode
        Intent intent = new Intent(this, ReverseGeocodeService.class);
        intent.putExtra(ReverseGeocodeService.RECEIVER, mResultReceiver);
        intent.putExtra(ReverseGeocodeService.INPUT, mCurrentLatLng);
        startService(intent);
    }

    @SuppressLint("ParcelCreator")
    class ReverseGeocodeResultReceiver extends ResultReceiver {

        public ReverseGeocodeResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultCode==ReverseGeocodeService.SUCCESS) {
                place_name = resultData.getString(ReverseGeocodeService.RESULTS);
            }
            else place_name = "Lat:" + mCurrentLatLng.latitude +", "+ "Lon:" + mCurrentLatLng.longitude;

            getEstimates(mCurrentLatLng, reach, rate, aoitype);

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Intent createShareIntent(String shared) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shared);
        intent.setType("text/plain");
        return intent;
    }

    private void shareData(){
        Intent shareIntent = createShareIntent(sharedString);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_using)));
    }

}
