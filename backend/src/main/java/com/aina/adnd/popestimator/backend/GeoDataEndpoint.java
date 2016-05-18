/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.aina.adnd.popestimator.backend;

import com.aina.adnd.aoi.AoiGenerator;
import com.aina.adnd.aoi.Conrec;
import com.aina.adnd.aoi.DistanceMatrixElement;
import com.aina.adnd.aoi.PointUtils;
import com.aina.adnd.spatialdata.Interval;
import com.aina.adnd.spatialdata.Interval2D;
import com.aina.adnd.spatialdata.PointCloud;
import com.aina.adnd.spatialdata.QuadTree;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "popEstimatorAPI",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.popestimator.adnd.aina.com",
    ownerName = "backend.popestimator.adnd.aina.com",
    packagePath=""
  )
)
public class GeoDataEndpoint {

    double MAX_RATE = 75.0;
    QuadTree<Double, String> qt = null;
    PointCloud pc = null;

    @ApiMethod(name = "getGeoData")
    public GeoData getGeoData(
            @Named("latitude") double phi,
            @Named("longitude") double lambda,
            @Named("reach") int reach,
            @Named("rate") double rate,
            @Named("aoi") int aoi) {

        if(null==qt && null==pc){
            pc = new PointCloud("bgdata.txt");
            qt = pc.getQuadTree();
        }

        GeoData response = new GeoData();

        if(0 == aoi) {
            response.setGeoDemoData(getRadialAOI(phi, lambda, (double)reach));
        }
        else{
            try {
                List<List<List<Object>>> geoDemoData = getDriveTimeAOI(phi, lambda, reach, rate);

                if(null != geoDemoData) {
                    response.setGeoDemoData(geoDemoData);
                }
                else{
                    response.setGeoDemoData(getDriveTimeAOI(phi, lambda, reach, MAX_RATE));
                }
            }
            catch(IOException e){
                response.setGeoDemoData(null);
            }
        }

        return response;
    }

    protected List<List<List<Object>>> getRadialAOI(
            double lat, double lon, double radius){

        List<List<Object>> coords = new ArrayList<>();

        int segs = 60;

        AoiGenerator aoi = new AoiGenerator();
        List<Double> vxList = new ArrayList<>();
        List<Double> vyList = new ArrayList<>();

        List<PointUtils.Point2> vertices = aoi.getVerticesFromRadial(lon, lat, radius, segs);

        for(int i=0; i < vertices.size();i++) {
            PointUtils.Point2 vrtx =  vertices.get(i);
            vxList.add(vrtx.x);
            vyList.add(vrtx.y);

            List<Object> v = new ArrayList<>();
            v.add(i);
            v.add(vrtx.x);
            v.add(vrtx.y);
            coords.add(v);
        }

        Double[] Vx = vxList.toArray(new Double[vxList.size()]);
        Double[] Vy = vyList.toArray(new Double[vyList.size()]);

        //Compute MBR of Test Polygon
        Collections.sort(vxList);
        Collections.sort(vyList);

        double xmin = vxList.get(0);
        double xmax = vxList.get(vxList.size()-1);
        double ymin = vyList.get(0);
        double ymax = vyList.get(vyList.size()-1);

        List<List<Object>> bounds = new ArrayList<>();
        List<Object> min = new ArrayList<>();
        min.add(xmin);
        min.add(ymin);
        bounds.add(min);
        List<Object> max = new ArrayList<>();
        max.add(xmax);
        max.add(ymax);
        bounds.add(max);

        Interval<Double> intX = new Interval<>(xmin, xmax);
        Interval<Double> intY = new Interval<>(ymin, ymax);

        Interval2D<Double> rect = new Interval2D<>(intX, intY);

        List<String> found = qt.query2D(rect, Vx, Vy);

        int[] aggs = popAggregates(found);

        List<List<Object>> data = popData(aggs);
        List<List<List<Object>>> results = new ArrayList<>();

        results.add(bounds);
        results.add(coords);
        results.add(data);

        return results;
    }

    protected List<List<List<Object>>> getDriveTimeAOI(
            double lat, double lon, int reach, double mph)throws IOException {

        double mps = 0.44704;
        int rows = 11;
        int cols = 9;
        double factor = 10000.0;
        double maxReach = mph*mps*reach; //metres

        double[] targetTime = new double[1];
        targetTime[0] = (double)reach;

        AoiGenerator aoi = new AoiGenerator();

        PointUtils pu = new PointUtils();
        PointUtils.Point2 origin =  pu.new Point2(lon, lat);

        StringBuilder sb = new StringBuilder();

        List<PointUtils.Point2> vertices = aoi.getVerticesFromGrid(origin, maxReach, rows, cols);

        double[] Vx = new double[cols];
        double[] Vy = new double[rows];

        int m = -1;
        for(int n=0; n < vertices.size(); n++){
            PointUtils.Point2 vrtx =  vertices.get(n);

            sb.append(vrtx.y).append(",").append(vrtx.x).append("|");

            if(n%cols == 0) {
                m++;
                Vy[m] = vrtx.y;
            }

            if(n < cols)
                Vx[n] = vrtx.x;
        }

        sb.setLength(sb.length() - 1);

        String urlStr = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                + "origins="
                + Math.round(origin.y*factor)/factor + ","
                + Math.round(origin.x*factor)/factor
                + "&destinations=" + sb.toString().replace("|",URLEncoder.encode("|", "UTF-8"))
                + "&mode=driving&language=en-US"
                + "&key=API_KEY";

        URL url = new URL(urlStr);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream response = connection.getInputStream();

        BufferedReader br;

        sb.setLength(0);

        String line;

        br = new BufferedReader(new InputStreamReader(response));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        String gdm = sb.toString();

        DistanceMatrixElement dme = new DistanceMatrixElement();

        List<DistanceMatrixElement> distanceMatrixElements = dme.ParseDistanceMatrix(gdm);

        double[][] timeMatrix = new double[rows][cols];

        int i = 0;
        int j = -1;
        for (int k = 0; k < distanceMatrixElements.size(); k++) {

            if(k > 0 && k%cols ==0){
                i++;
                j = 0;
            } else j++;

            timeMatrix[i][j] = distanceMatrixElements.get(k).getDurationValue();
        }

        Conrec conrec = new Conrec();

        List<Double[]> edges = conrec.contour(
                timeMatrix, 0, Vy.length - 1, 0, Vx.length - 1,
                Vy, Vx, targetTime.length, targetTime);

        List<String> orderedEdges = aoi.orderEdges(edges);

        if(null != orderedEdges)
        {
            Collections.reverse(orderedEdges);

            List<Double> vxList = new ArrayList<>();
            List<Double> vyList = new ArrayList<>();
            List<List<Object>> coords = new ArrayList<>();

            i = 0;
            for (String edge : orderedEdges) {

                double x = Double.parseDouble(edge.split(",")[0]);
                double y = Double.parseDouble(edge.split(",")[1]);

                vxList.add(x);
                vyList.add(y);

                List<Object> v = new ArrayList<>();
                v.add(i);
                v.add(x);
                v.add(y);
                coords.add(v);
                i++;
            }

            Double[] Ex = vxList.toArray(new Double[vxList.size()]);
            Double[] Ey = vyList.toArray(new Double[vyList.size()]);

            //Compute MBR of Test Polygon
            Collections.sort(vxList);
            Collections.sort(vyList);

            Double xmin = vxList.get(0);
            Double xmax = vxList.get(vxList.size() - 1);
            Double ymin = vyList.get(0);
            Double ymax = vyList.get(vyList.size() - 1);

            List<List<Object>> bounds = new ArrayList<>();
            List<Object> min = new ArrayList<>();
            min.add(xmin);
            min.add(ymin);
            bounds.add(min);
            List<Object> max = new ArrayList<>();
            max.add(xmax);
            max.add(ymax);
            bounds.add(max);

            Interval<Double> intX = new Interval<>(xmin, xmax);
            Interval<Double> intY = new Interval<>(ymin, ymax);
            Interval2D<Double> rect = new Interval2D<>(intX, intY);

            List<String> found = qt.query2D(rect, Ex, Ey);

            int[] aggs = popAggregates(found);

            List<List<Object>> data = popData(aggs);
            List<List<List<Object>>> results = new ArrayList<>();

            results.add(bounds);
            results.add(coords);
            results.add(data);

            return results;
        }
        else{
            return null;
        }

    }

    public static int[] popAggregates(List<String> found){

        int[] popAggregates = new int[8];

        String[] token;

        for(String f: found){
            token = f.split("_");
            popAggregates[0]+= Integer.parseInt(token[1]);
            popAggregates[1]+= Integer.parseInt(token[2]);
            popAggregates[2]+= Integer.parseInt(token[3]);
            popAggregates[3]+= Integer.parseInt(token[4]);
            popAggregates[4]+= Integer.parseInt(token[5]);
            popAggregates[5]+= Integer.parseInt(token[6]);
            popAggregates[6]+= Integer.parseInt(token[7]);
            popAggregates[7]+= Integer.parseInt(token[8]);
        }

        return popAggregates;
    }

    public static List<List<Object>> popData(int[] aggs){

        Double factor = 100.0;

        List<List<Object>> data = new ArrayList<>();

        String[] races = {"Amerindian","Asian","Black","Hispanic"
                ,"Other","Pac.Islander","White","Total"};

        for(int i=0;i<aggs.length-1;i++) {
            List<Object> race = new ArrayList<>();
            race.add(races[i]);
            race.add(aggs[i]);
            race.add(Math.round(((double)aggs[i]*100.0/(double)aggs[aggs.length-1])*factor)/factor);
            data.add(race);
        }
        return data;

    }

}
