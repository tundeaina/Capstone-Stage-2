package com.aina.adnd.popestimator;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.aina.adnd.popestimator.services.AOIDbService;
import com.aina.adnd.popestimator.data.AOILogAdapter;
import com.aina.adnd.popestimator.data.PopEstimatorContract;


public class AOILogFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private AOILogAdapter aoiLogAdapter;
    private ListView aoiList;
    private int position = ListView.INVALID_POSITION;
    private EditText searchText;
    private ActionMode mActionMode;

    private boolean mTablet;
    private long placeId;
    private String placeName;
    private String placeData;
    private double latitude;
    private double longitude;
    private int aoiType;
    private double reach;
    private double rate;
    public static String HISTORICAL = "History";

    public AOILogFragment() {}

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_aoilog, menu);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_delete:
                    deleteCurrentItem();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTablet = false;

        Bundle args = getArguments();
        if(args!= null && args.containsKey("TABLET"))
            mTablet = (args.getInt("TABLET") == 1);

        View rootView = inflater.inflate(R.layout.fragment_aoilog, container, false);

        Cursor cursor = getActivity().getContentResolver().query(
                PopEstimatorContract.AOILogEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        aoiLogAdapter = new AOILogAdapter(getActivity(),cursor, 0);

        aoiList = (ListView) rootView.findViewById(R.id.list_aoi);
        aoiList.setAdapter(aoiLogAdapter);

        aoiList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }

                Cursor cursor = aoiLogAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    placeId = cursor.getLong(cursor.getColumnIndex(PopEstimatorContract.AOILogEntry._ID));
                }

                view.setActivated(true);

                mActionMode = getActivity().startActionMode(mActionModeCallback);
                //view.setSelected(true);

                return true;
            }
        });

        aoiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = aoiLogAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {

                    getDataAtCursor(cursor);

                    Intent intent = new Intent(getActivity(),MainActivity.class);

                    intent.putExtra(PopEstimatorContract.AOILogEntry._ID, placeId);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.PLACE_NAME, placeName);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.PLACE_DATA, placeData);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.LATITUDE, latitude);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.LONGITUDE, longitude);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.AOITYPE, aoiType);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.REACH, reach);
                    intent.putExtra(PopEstimatorContract.AOILogEntry.RATE, rate);
                    intent.putExtra(HISTORICAL, true);
                    getActivity().startActivity(intent);
                }
            }
        });

        searchText = (EditText) rootView.findViewById(R.id.searchLog);
        rootView.findViewById(R.id.searchButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshLoader();
                    }
                }
        );

        rootView.findViewById(R.id.searchCancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();
                        if (fm.getBackStackEntryCount() > 0)
                            fm.popBackStack();
                        else
                            getActivity().finish();
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fabReport = (FloatingActionButton) getActivity().findViewById(R.id.fabReport);

        if(mTablet) {
            fabReport.setEnabled(true);
            fabReport.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);

        if(mTablet) {
            MenuItem item = menu.findItem(R.id.menu_item_save);
            item.setVisible(false);

            item = menu.findItem(R.id.menu_item_aoilog);
            item.setVisible(false);

            item = menu.findItem(R.id.menu_item_search);
            item.setVisible(false);

            item = menu.findItem(R.id.menu_item_share);
            item.setVisible(false);

            getActivity().invalidateOptionsMenu();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.menu_item_search:
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection = PopEstimatorContract.AOILogEntry.PLACE_NAME + " LIKE ? ";
        String searchString = searchText.getText().toString();

        if(searchString.length()>0){
            searchString = "%"+searchString+"%";
            return new CursorLoader(
                    getActivity(),
                    PopEstimatorContract.AOILogEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                PopEstimatorContract.AOILogEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        aoiLogAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            aoiList.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        aoiLogAdapter.swapCursor(null);
    }

    private void getDataAtCursor(Cursor cursor){

        placeId = cursor.getLong(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry._ID));

        placeName = cursor.getString(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.PLACE_NAME));

        placeData = cursor.getString(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.PLACE_DATA));

        latitude = cursor.getDouble(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.LATITUDE));

        longitude = cursor.getDouble(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.LONGITUDE));

        aoiType = cursor.getInt(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.AOITYPE));

        reach = cursor.getDouble(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.REACH));

        rate = cursor.getDouble(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.RATE));
    }

    private void refreshLoader(){
        getLoaderManager().restartLoader(1, null, this);
    }

    private void deleteCurrentItem(){

        Intent DeleteLogEntryIntent = new Intent(getActivity(), AOIDbService.class);

        DeleteLogEntryIntent.putExtra(PopEstimatorContract.AOILogEntry._ID, placeId);

        DeleteLogEntryIntent.setAction(AOIDbService.DELETE_LOG_ENTRY);

        getActivity().startService(DeleteLogEntryIntent);

        refreshLoader();

        Toast.makeText(getActivity(), getString(R.string.deleted), Toast.LENGTH_SHORT).show();
    }

}
