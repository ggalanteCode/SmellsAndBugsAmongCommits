/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;

import java.util.ArrayList;
import database.DbHandler;

/**
 * Class used to parse MetricCollection output files
 * @author alessio
 */
public class MetricCollectionReader implements Reader{
    
    private String commitId;
    private String projectUrl;
    private String path;
    
    /**
     * Constructor 
     * @param commitId commit hash
     * @param projectUrl project url
     * @param path path to the file
     */
    public MetricCollectionReader(String commitId,String projectUrl,String path){
        this.commitId=commitId;
        this.projectUrl=projectUrl;
        this.path=path;
    }
    
    /**
     * this method takes the read line, breaks it according to the specified regular expression, 
     * and adds the values to a list
     * @param row line read
     * @return ArrayList containing the values of the line read
     */
    public ArrayList<String> splitRow(String row){
        ArrayList<String> list=new ArrayList<>();
        String[] s=row.split("(\")");
        for(int i=0;i<s.length;i++)
            if(!(s[i].equals("")) && !(s[i].equals(","))){
                if(s[i].endsWith(",")){
                    String d=s[i].substring(0,s[i].length()-1);
                    list.add(d);
                }else
                    list.add(s[i]);
            }
        return list;
    }
    
    /**
     * This method is used to process a line of the csv file and this method is used to process a line in the .csv 
     * file and insert the read values into the database
     * @param row line to read
     */
    @Override
    public void read(String row){
        ArrayList<String> list=splitRow(row);

        //int paramNum = list.size();
        //System.out.println("MetricCollectionReader: " + "N. of parameters " + paramNum + ", row >> " + row);

        if(path.matches("(.*)(-Class.csv)"))
            DbHandler.insertMetricClass(list, commitId, projectUrl);
        else
            if(path.matches("(.*)(-Method.csv)"))
                DbHandler.insertMetricMethod(list, commitId, projectUrl);
            else
                DbHandler.insertMetricPackage(list, commitId, projectUrl);
    }
    
}
