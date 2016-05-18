package com.aina.adnd.popestimator.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aina.adnd.popestimator.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tunde Aina on 5/1/2016.
 */
public class AOILogAdapter extends CursorAdapter {

    public AOILogAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    final double METERS = 1609.344;
    final double SECONDS = 60;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.aoi_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String placename = cursor.getString(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.PLACE_NAME));
        viewHolder.placeName.setText(placename);

        int aoitype = cursor.getInt(cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.AOITYPE));
        double reach = cursor.getInt(cursor.getColumnIndex(PopEstimatorContract.AOILogEntry.REACH));

        String aoidescription;

        if(0==aoitype){
            aoidescription = (int)Math.round(reach/METERS) + " mile radius";
        }
        else{
            aoidescription = (int)Math.round(reach/SECONDS)+ " minute drive time";
        }

        viewHolder.aoiDescription.setText(aoidescription);

        long datetime = cursor.getLong(
                cursor.getColumnIndex(PopEstimatorContract.AOILogEntry._ID));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

        viewHolder.dateTime.setText(sdf.format(datetime));
    }

    class ViewHolder {
        public final TextView placeName;
        public final TextView aoiDescription;
        public final TextView dateTime;

        public ViewHolder(View view) {
            placeName = (TextView) view.findViewById(R.id.placename);
            aoiDescription = (TextView) view.findViewById(R.id.aoidesc);
            dateTime = (TextView) view.findViewById(R.id.datetime);
        }
    }
}
