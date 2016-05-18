package com.aina.adnd.popestimator.data;

import android.content.res.Resources;
import android.os.Bundle;

import com.aina.adnd.popestimator.R;

/**
 * Created by Tunde Aina on 5/16/2016.
 */
public class DataExport {

    public DataExport(){}

    public static final String AMERINDIAN = "American Indian";
    public static final String ASIAN  = "Asian";
    public static final String BLACK  = "Black";
    public static final String HISPANIC  = "Hispanic";
    public static final String PACISLANDER = "Pac. Islander";
    public static final String OTHERS = "Others";
    public static final String WHITE = "White";
    public static final String WIDGET_TITLE = "Population Estimates For";

    public static String getSharedData(Bundle args) {

        String aoiDef = args.getString("AOI_DESC");
        String placeName = args.getString("PLACE_NAME");
        int Amerindian = args.getInt("_Amerindian");
        int Asian = args.getInt("_Asian");
        int Black = args.getInt("_Black");
        int Hispanic = args.getInt("_Hispanic");
        int PacIslander = args.getInt("_PacIslander");
        int White = args.getInt("_White");
        int Other = args.getInt("_Other");
        double Amerindian2 = args.getDouble("_Amerindian2");
        double Asian2 = args.getDouble("_Asian2");
        double Black2 = args.getDouble("_Black2");
        double Hispanic2 = args.getDouble("_Hispanic2");
        double PacIslander2 = args.getDouble("_PacIslander2");
        double White2 = args.getDouble("_White2");
        double Other2 = args.getDouble("_Other2");

        StringBuilder sb = new StringBuilder();

        sb.append(WIDGET_TITLE).append("\n")
                .append(placeName).append("\n").append(aoiDef).append("\n");

        sb.append(AMERINDIAN).append("\t\t\t\t")
                .append(String.format("%,d", Amerindian)).append("\t\t\t\t")
                .append(String.format("%,4.1f", Amerindian2)).append("%\n");

        sb.append(ASIAN).append("\t\t\t\t")
                .append(String.format("%,d", Asian)).append("\t\t\t\t")
                .append(String.format("%,4.1f", Asian2)).append("%\n");

        sb.append(BLACK).append("\t\t\t\t")
                .append(String.format("%,d", Black)).append("\t\t\t\t")
                .append(String.format("%,4.1f", Black2)).append("%\n");

        sb.append(HISPANIC).append("\t\t\t\t")
                .append(String.format("%,d", Hispanic)).append("\t\t\t\t")
                .append(String.format("%,4.1f", Hispanic2)).append("%\n");

        sb.append(PACISLANDER).append("\t\t\t\t")
                .append(String.format("%,d", PacIslander)).append("\t\t\t\t")
                .append(String.format("%,4.1f", PacIslander2)).append("%\n");

        sb.append(WHITE).append("\t\t\t\t")
                .append(String.format("%,d", White)).append("\t\t\t\t")
                .append(String.format("%,4.1f", White2)).append("%\n");

        sb.append(OTHERS).append("\t\t\t\t")
                .append(String.format("%,d", Other)).append("\t\t\t\t")
                .append(String.format("%,4.1f", Other2)).append("%\n");

        return sb.toString();
    }
}
