package com.aina.adnd.popestimator.backend;

import java.util.List;

public class GeoData {

    private List<List<List<Object>>> geoDemoData;

    public List<List<List<Object>>> getGeoDemoData(){
        return geoDemoData;
    }

    public void setGeoDemoData(List<List<List<Object>>> data){
        geoDemoData = data;
    }
}
