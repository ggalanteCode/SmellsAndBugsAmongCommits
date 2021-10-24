package Parsers;

import com.google.gson.JsonObject;

/**
 * this strategy pattern provides methods for parsing data from a JSON file and writing the parsed results in
 * a database
 * @author Giovanni Galante
 */
public interface JSONReader {

    public JsonObject readFromJSONFile(String file);

    public void writeInDB();

}
