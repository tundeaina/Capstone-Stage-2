package com.aina.adnd.popestimator;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.aina.adnd.popestimator.data.DataExport;

import java.io.FileOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class AOIReportFragment extends Fragment {


    public AOIReportFragment() {
        // Required empty public constructor
    }

    private boolean mTablet;
    private ShareActionProvider mShareActionProvider;
    private String sharedString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!mTablet)
            setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();

        String _aoiDef = null;
        String _placeName = null;

        int _Amerindian = 0;
        int _Asian = 0;
        int _Black = 0;
        int _Hispanic = 0;
        int _PacIslander = 0;
        int _White = 0;
        int _Other = 0;

        double _Amerindian2 = 0;
        double _Asian2 = 0;
        double _Black2 = 0;
        double _Hispanic2 = 0;
        double _White2 = 0;
        double _PacIslander2 = 0;
        double _Other2 = 0;

        if (args != null) {
            mTablet = (args.getInt("TABLET") == 1);
            _aoiDef = args.getString("AOI_DESC");
            _placeName = args.getString("PLACE_NAME");
            _Amerindian = args.getInt("_Amerindian");
            _Asian = args.getInt("_Asian");
            _Black = args.getInt("_Black");
            _Hispanic = args.getInt("_Hispanic");
            _PacIslander = args.getInt("_PacIslander");
            _White = args.getInt("_White");
            _Other = args.getInt("_Other");
            _Amerindian2 = args.getDouble("_Amerindian2");
            _Asian2 = args.getDouble("_Asian2");
            _Black2 = args.getDouble("_Black2");
            _Hispanic2 = args.getDouble("_Hispanic2");
            _PacIslander2 = args.getDouble("_PacIslander2");
            _White2 = args.getDouble("_White2");
            _Other2 = args.getDouble("_Other2");

            sharedString = DataExport.getSharedData(args);
        }

        View view = inflater.inflate(R.layout.fragment_aoireport, container, false);

        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
        TextView subsubtitle = (TextView) view.findViewById(R.id.subsubtitle);

        TextView amerindian2 = (TextView) view.findViewById(R.id.amerindian2);
        TextView asian2 = (TextView) view.findViewById(R.id.asian2);
        TextView black2 = (TextView) view.findViewById(R.id.black2);
        TextView hispanic2 = (TextView) view.findViewById(R.id.hispanic2);
        TextView pacislander2 = (TextView) view.findViewById(R.id.pacislander2);
        TextView white2 = (TextView) view.findViewById(R.id.white2);
        TextView others2 = (TextView) view.findViewById(R.id.others2);

        TextView amerindian1 = (TextView) view.findViewById(R.id.amerindian1);
        TextView asian1 = (TextView) view.findViewById(R.id.asian1);
        TextView black1 = (TextView) view.findViewById(R.id.black1);
        TextView hispanic1 = (TextView) view.findViewById(R.id.hispanic1);
        TextView pacislander1 = (TextView) view.findViewById(R.id.pacislander1);
        TextView white1 = (TextView) view.findViewById(R.id.white1);
        TextView others1 = (TextView) view.findViewById(R.id.others1);

        subtitle.setText(_placeName);
        subsubtitle.setText(_aoiDef);

        amerindian1.setText(String.format("%,d",_Amerindian));
        asian1.setText(String.format("%,d",_Asian));
        black1.setText(String.format("%,d",_Black));
        hispanic1.setText(String.format("%,d",_Hispanic));
        pacislander1.setText(String.format("%,d",_PacIslander));
        white1.setText(String.format("%,d",_White));
        others1.setText(String.format("%,d",_Other));

        amerindian2.setText(String.format("%,4.1f",_Amerindian2));
        asian2.setText(String.format("%,4.1f",_Asian2));
        black2.setText(String.format("%,4.1f",_Black2));
        hispanic2.setText(String.format("%,4.1f",_Hispanic2));
        pacislander2.setText(String.format("%,4.1f",_PacIslander2));
        white2.setText(String.format("%,4.1f",_White2));
        others2.setText(String.format("%,4.1f",_Other2));

        WebView webview = (WebView) view.findViewById(R.id.webView1);

        String html = "\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <script type=\"text/javascript\" src=\"loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "\n" +
                "        google.charts.load('current', {'packages':['corechart','bar']});\n" +
                "        google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "        function drawChart() {\n" +
                "          var data = google.visualization.arrayToDataTable([\n" +
                "            ['', ''],\n" +
                "            ['Amerindian', " + _Amerindian + "],\n" +
                "            ['Asian', " + _Asian + "],\n" +
                "            ['Black', " + _Black + "],\n" +
                "            ['Hispanic', " + _Hispanic + "],\n" +
                "            ['Pac.Islander', " + _PacIslander + "],\n" +
                "            ['Others', " + _Other + "],\n" +
                "            ['White', " + _White + "],\n" +
                "          ]);\n" +
                "\n" +
                "          var options = {\n" +
                "\n" +
                "            bars: 'horizontal',\n" +
                "            height: 280,\n" +
                "            legend: {position: 'none'},\n" +
                "            colors :['#512DA8']\n" +
                "          };\n" +
                "\n" +
                "            var chart = new google.charts.Bar(document.getElementById('barchart_material'));\n" +
                "\n" +
                "            chart.draw(data, options);\n" +
                "        }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body></br>\n" +
                "    <div id=\"barchart_material\"></div></br>\n" +
                "  </body>\n" +
                "</html>";

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.requestFocusFromTouch();
        webview.loadDataWithBaseURL( "file:///android_asset/", html, "text/html", "utf-8", null );

        Log.d("REPORT VIEW...", html);

        // Inflate the layout for this fragment
        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fabReport = (FloatingActionButton) getActivity().findViewById(R.id.fabReport);

        if(mTablet) {
            fabReport.setEnabled(false);
            fabReport.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);

        if(!mTablet) {
            MenuItem item = menu.findItem(R.id.menu_item_share);
            Intent shareIntent = createShareIntent(sharedString);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
    }

    private Intent createShareIntent(String shared) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shared);
        intent.setType("text/plain");
        return intent;
    }

}
