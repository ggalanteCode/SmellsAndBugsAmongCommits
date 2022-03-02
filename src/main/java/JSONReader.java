package Parsers;

import java.io.File;

import com.google.gson.JsonObject;

import models.CodeShovelClass;
import models.CodeShovelMethod;
import models.CodeShovelOutputFileRoot;

/**
 * this strategy pattern provides methods for parsing data from a JSON file and writing the parsed results in
 * a database
 * @author Giovanni Galante
 */
public interface JSONReader {

    public CodeShovelOutputFileRoot readFromJSONFile(File file);

    public void writeInDB(CodeShovelMethod method, CodeShovelClass csClass);

}
