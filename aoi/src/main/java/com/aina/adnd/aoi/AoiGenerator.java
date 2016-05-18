package com.aina.adnd.aoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AoiGenerator {
    public AoiGenerator(){}

    LatLonUtils llu = new LatLonUtils();

    public List<PointUtils.Point2> getVerticesFromRadial(
            double lon, double lat, double d, int segments){

        List<PointUtils.Point2> nodes = new ArrayList<>();

        double sector = 360.0/segments;
        double scale = 4;

        for(int i=0; i<=segments;i++){

            PointUtils.Point2 pt = llu.getPointFromDistBrng(lat, lon, i*sector, d, scale);

            nodes.add(pt);
        }

        return nodes;
    }

    public List<PointUtils.Point2> getVerticesFromGrid(
            PointUtils.Point2 origin, Double reach, int rows, int cols){

        double dy = 2*reach/(rows-1);
        double dx = 2*reach/(cols-1);
        double east = 90.0;
        double north = 0.0;
        double south = 180.0;
        double west = 270.0;
        double scale = 3;

        PointUtils.Point2 left = llu.getPointFromDistBrng(origin.y, origin.x, west, reach, scale);
        left = llu.getPointFromDistBrng(left.y, left.x, north, reach, scale);

        List<PointUtils.Point2> nodes = new ArrayList<>();

        for (int i = 0; i < rows; i++){

            for (int j = 0; j < cols; j++){

                PointUtils.Point2 node = llu.getPointFromDistBrng(left.y, left.x, east, j*dx, scale);

//                System.out.println(Math.round(node.x*factor)/factor +
//                        "|" + Math.round(node.y*factor)/factor);

                nodes.add(node);
            }

            left = llu.getPointFromDistBrng(left.y, left.x, south, dy, scale);
        }

        return nodes;
    }

    public List<String> orderEdges(List<Double[]> edges){

        List<String> segmentList = new ArrayList<>();
        List<String> chain = new ArrayList<>();
        Double[] segment;
        String startAt;

        try {
            for (int k = 0; k < edges.size(); k++) {
                segment = edges.get(k);
                segmentList.add(segment[0] + "," + segment[1] + "|" + segment[2] + "," + segment[3]);
                chain.add(segment[0] + "," + segment[1]);
                chain.add(segment[2] + "," + segment[3]);
            }

            startAt = chain.get(0);

            Set<String> dedups = new HashSet<>();
            for (String a : chain) {
                dedups.add(a);
            }

            List<String> dangles = new ArrayList<>();
            for (String s : dedups) {
                int k = 0;
                for (String a : chain) {
                    if (s.equals(a)) k++;
                }

                if (k == 1) dangles.add(s);
            }

            chain.clear();
            int k = 0;

            while (chain.size() < segmentList.size()) {
                if (k == dangles.size()) k--;
                startAt = (dangles.size() > 0) ? dangles.get(k) : startAt;
                chain = getEdges(segmentList, startAt);
                k += 2;
                System.out.println("----------------> Chain: " + chain.size() + "  segmentList: " + segmentList.size());
            }
            return chain;
        }
        catch(Exception e){
            return null;
        }
    }

    public List<String> getEdges(List<String> segmentList, String startPt){

        int noEdges = segmentList.size();
        List<String> chain = new ArrayList<>();
        String link=null;

        try {
            for (int k = 0; k < segmentList.size(); k++) {
                if (segmentList.get(k).contains(startPt)) {
                    link = segmentList.get(k);
                    break;
                }
            }

            String[] nextSegment = new String[0];

            if (link != null) {
                nextSegment = link.split("\\|");
            }

            String fr = (nextSegment[0].compareTo(nextSegment[1]) > 0) ? nextSegment[0] : nextSegment[1];
            String to = (nextSegment[0].compareTo(nextSegment[1]) < 0) ? nextSegment[0] : nextSegment[1];

            chain.clear();
            chain.add(fr);
            chain.add(to);

            segmentList.remove(link);
            int segmentListCounter = 0;

            while (fr.compareTo(to) != 0) {
                for (int k = 0; k < segmentList.size(); k++) {

                    link = segmentList.get(k);

                    if (link.contains(to)) {

                        nextSegment = link.split("\\|");

                        to = (to.compareTo(nextSegment[0]) == 0) ? nextSegment[1] : nextSegment[0];

                        chain.add(to);

                        segmentList.remove(link);
                        //System.out.println("LINK ---> " + link + "segmentList.size" + segmentList.size());
                        break;
                    }
                }

                segmentListCounter++;

                if (segmentListCounter > noEdges) { // Signifies that no more links found
                    chain.add(fr);
                    break;
                }
            }

            return chain;
        }
        catch(Exception e){
           return null;
        }
    }
}
