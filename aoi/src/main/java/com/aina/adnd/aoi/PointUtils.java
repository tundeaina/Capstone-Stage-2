package com.aina.adnd.aoi;

public class PointUtils {

    public PointUtils(){}

    public class Point2{

        public final Double x;
        public final Double y;

        public Point2(Double x, Double y){
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "[" + x + ", " + y + "]";
        }
    }

    public class Point3{

        public final Double x;
        public final Double y;
        public final Double z;

        public Point3(Double x, Double y, Double z){
            this.x = x;
            this.y = y;
            this.z = z;

        }

        public String toString() {
            return "[" + x + ", " + y +  ", " + z + "]";
        }
    }

}
