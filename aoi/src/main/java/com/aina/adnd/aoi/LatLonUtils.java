package com.aina.adnd.aoi;

public class LatLonUtils
{
    public LatLonUtils(){}

    final double EARTH_RADIUS = 6378137;
    final double PI = 3.14159265359;

    /*
        phi : latitude,
        lambda : longitude,
        theta : bearing (in radians, clockwise from north),
        delta : angular distance (in radians) d/R;
        d : distance travelled, R the earth’s radius
     */

    public PointUtils.Point2 getPointFromDistBrng(
            double phi1, double lambda1, double theta, double distance, double scale)
    {
        double FACTOR = Math.pow(10, scale);

        phi1 = Deg2Rad(phi1);

        lambda1 = Deg2Rad(lambda1);

        theta = Deg2Rad(theta);

        double delta = distance / EARTH_RADIUS;

        double phi2 = Math.asin(Math.sin(phi1) * Math.cos(delta) +
                Math.cos(phi1) * Math.sin(delta) * Math.cos(theta));

        double lambda2 = lambda1 + Math.atan2(Math.sin(theta) * Math.sin(delta) * Math.cos(phi1),
                Math.cos(delta) - Math.sin(phi1) * Math.sin(phi2));

//        System.out.println(Rad2Deg(theta) + " " + Rad2Deg(phi2) + " " + Rad2Deg(lambda2));

        PointUtils pu = new PointUtils();
        return pu.new Point2(
                Math.round(Rad2Deg(lambda2)*FACTOR)/FACTOR,
                Math.round(Rad2Deg(phi2)*FACTOR)/FACTOR);
    }

    public double Deg2Rad(double deg)
    {
        return deg * PI / 180;
    }

    public double Rad2Deg(double rad)
    {
        return rad * 180 / PI;
    }

}
