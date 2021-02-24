package Parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class used to provide a general algorithm for reading txt files
 * @author Francesco Florio
 */
public class TxtParser {
    
    private Reader reader;

    /**
     * Constructor
     * @param reader reader used for reading the txt file
     * @see Reader
     */
    public TxtParser(Reader reader){
        this.reader = reader;
    }
    
    /**
     * This method is used to read all lines in the txt file and parse the data 
     * @param path path file
     * @return boolean true if the operation is successfull
     */
    public  boolean parse(String path){
        
        try {
            BufferedReader br=new BufferedReader(new FileReader(path));
            String row;
            
            while((row=br.readLine())!=null)
                if(!row.equals(""))
                     reader.read(row);
            
                
            
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    
    }
    
}
