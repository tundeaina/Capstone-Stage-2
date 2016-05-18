package com.aina.adnd.spatialdata;

/**
 * Copyright (c) 1970-2003, Wm. Randolph Franklin
 */

public class PnP {
    public PnP(){}

    public boolean process(int nvert, Double[] vertx, Double[] verty, Double testx, Double testy){
        int i, j;
        boolean c = false;
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
                c = !c;
        }
        return c;
    }

}
