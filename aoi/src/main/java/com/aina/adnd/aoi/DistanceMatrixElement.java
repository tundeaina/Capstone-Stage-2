package com.aina.adnd.aoi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DistanceMatrixElement
{
    private String dist_txt;
    private int dist_val;
    private String dur_txt;
    private int dur_val;
    private String status;

    public DistanceMatrixElement()
    {
    }

    public void setDistanceText(String value){
        this.dist_txt = value;
    }
    public String getDistanceText()
    {
        return dist_txt;
    }

    public void setDistanceValue(int value){
        this.dist_val = value;
    }
    public int getDistanceValue()
    {
        return dist_val;
    }

    public void setDurationText(String value){
        this.dur_txt = value;
    }
    public String getDurationText()
    {
        return dur_txt;
    }

    public void setDurationValue(int value){
        this.dur_val = value;
    }
    public int getDurationValue()
    {
        return dur_val;
    }

    public void setStatus(String value){
        this.status = value;
    }
    public String getStatus()
    {
        return status;
    }

    public List<DistanceMatrixElement> ParseDistanceMatrix(String json){

        List<DistanceMatrixElement> distanceElements = new ArrayList<>();

        try {
            JSONObject jo = new JSONObject(json);
            JSONArray elements = jo
                    .getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements");

            JSONObject element;

            for(int i=0; i < elements.length(); i++){

                DistanceMatrixElement distanceElement = new DistanceMatrixElement();

                if (0 == elements.getJSONObject(i).getString("status").compareTo("OK")) {

                    element = elements.getJSONObject(i).getJSONObject("duration");
                    distanceElement.setDurationText(element.getString("text"));
                    distanceElement.setDurationValue(element.getInt("value"));

                    element = elements.getJSONObject(i).getJSONObject("distance");
                    distanceElement.setDistanceText(element.getString("text"));
                    distanceElement.setDistanceValue(element.getInt("value"));

                    distanceElement.setStatus(elements.getJSONObject(i).getString("status"));
                }
                else{
                    distanceElement.setDurationText("");
                    distanceElement.setDurationValue(9999);
                    distanceElement.setDistanceText("");
                    distanceElement.setDistanceValue(9999);
                    distanceElement.setStatus("");
                }

                distanceElements.add(distanceElement);
            }
        }
        catch(Exception e){
        }

        return distanceElements;
    }


}
