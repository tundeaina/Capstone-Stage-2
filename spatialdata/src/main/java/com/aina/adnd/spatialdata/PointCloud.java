package com.aina.adnd.spatialdata;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PointCloud {

    public QuadTree<Double, String> quadTree = new QuadTree<>();
    private String src;

    public PointCloud(String dataSource){
        this.src = dataSource;
//    }
//
//    public void init(){

        try
        {
            System.out.println("Start INIT");

            InputStream inputStream = this.getClass().getClassLoader()
                    .getResourceAsStream(src);

            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            while (( line = buffreader.readLine()) != null) {
                String[] tok = line.split("\\|");
                quadTree.insert(Double.parseDouble(tok[2]), Double.parseDouble(tok[1]), tok[0]);
            }


            System.out.println("Done INIT");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public QuadTree<Double, String> getQuadTree(){
        return quadTree;
    }
}
