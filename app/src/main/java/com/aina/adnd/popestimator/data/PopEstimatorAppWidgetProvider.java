package com.aina.adnd.popestimator.data;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.aina.adnd.popestimator.MainActivity;
import com.aina.adnd.popestimator.R;
import com.aina.adnd.popestimator.UserPreferences;


public class PopEstimatorAppWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.aoi_widget);

            views.setTextViewText(R.id.widget_subtitle,
                    UserPreferences.getUserPlaceName(context));
            views.setTextViewText(R.id.widget_subsubtitle,
                    UserPreferences.getUserAoi_Desc(context));

            String[] estimates = UserPreferences.getUserEstimates(context).split("\\|");

            views.setTextViewText(R.id.amerindian2,
                    String.format("%,d",Integer.parseInt(estimates[0])));
            views.setTextViewText(R.id.asian2,
                    String.format("%,d",Integer.parseInt(estimates[1])));
            views.setTextViewText(R.id.black2,
                    String.format("%,d",Integer.parseInt(estimates[2])));
            views.setTextViewText(R.id.hispanic2,
                    String.format("%,d",Integer.parseInt(estimates[3])));
            views.setTextViewText(R.id.pacislander2,
                    String.format("%,d",Integer.parseInt(estimates[4])));
            views.setTextViewText(R.id.white2,
                    String.format("%,d",Integer.parseInt(estimates[5])));
            views.setTextViewText(R.id.others2,
                    String.format("%,d",Integer.parseInt(estimates[6])));

            views.setTextViewText(R.id.amerindian1,
                    String.format("%,4.1f",Double.parseDouble(estimates[7]))+"%");
            views.setTextViewText(R.id.asian1,
                    String.format("%,4.1f",Double.parseDouble(estimates[8]))+"%");
            views.setTextViewText(R.id.black1,
                    String.format("%,4.1f",Double.parseDouble(estimates[9]))+"%");
            views.setTextViewText(R.id.hispanic1,
                    String.format("%,4.1f",Double.parseDouble(estimates[10]))+"%");
            views.setTextViewText(R.id.pacislander1,
                    String.format("%,4.1f",Double.parseDouble(estimates[11]))+"%");
            views.setTextViewText(R.id.white1,
                    String.format("%,4.1f",Double.parseDouble(estimates[12]))+"%");
            views.setTextViewText(R.id.others1,
                    String.format("%,4.1f",Double.parseDouble(estimates[13]))+"%");

            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
