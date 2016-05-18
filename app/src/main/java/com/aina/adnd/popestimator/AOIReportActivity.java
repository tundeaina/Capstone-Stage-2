package com.aina.adnd.popestimator;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;


public class AOIReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aoireport);

        if (savedInstanceState == null) {
            AOIReportFragment aoiReportFragment = new AOIReportFragment();
            aoiReportFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.aoireport_container, aoiReportFragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_aoilog:
                    Intent intent = new Intent(AOIReportActivity.this, AOILogActivity.class);
                    startActivity(intent);
                return true;
            default:
                break;
        }
        return false;
    }
}
