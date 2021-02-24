package Parsers;

/**
 * Strategy pattern to read and process data from the txt File
 * @author Francesco Florio
 */
public interface Reader {
    
    /**
     * Method to read the data of a txt line into objects
     * @param readLine line to read
     */
    public void read(String readLine);
}
