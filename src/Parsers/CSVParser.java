/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class used to provide a general algorithm for reading csv files
 * @author alessio
 */
public class CSVParser {
    
    private Reader reader;
    
    /**
     * Constructor
     * @param reader reader used for reading the txt file
     */
    public CSVParser(Reader reader){
        this.reader=reader;
    }
    
    /**
     * This method is used to read all lines in the csv file and parse the data
     * @param path path to the file
     * @return boolean true if the operation is successfull
     */
    public boolean parse(String path){
        try{
            BufferedReader br=new BufferedReader(new FileReader(path));
            String row;
            int nrighe=0;
            while((row=br.readLine())!=null){
                nrighe++;
                 if(!row.equals(""))
                     if(nrighe>1)
                        reader.read(row);
            }
            
            return true;
        }catch(FileNotFoundException ex){
            return false;
        }catch(IOException e){
            return false;
        }
    }
    
}
