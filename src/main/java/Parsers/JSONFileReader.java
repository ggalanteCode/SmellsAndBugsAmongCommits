
package Parsers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import database.DbHandler;
import gui.Download;
import models.CodeShovelClass;
import models.CodeShovelMethod;
import models.CodeShovelOutputFileRoot;

import java.io.*;
import java.sql.SQLException;

import org.postgresql.util.PSQLException;

/**
 * this class parses the results in the JSON output file produced by the tool for calculating
 *  differences among successive releases (CodeShovel) and writes the parsed results on the 
 *  'sbac' database
 * @author Giovanni Galante
 */
public class JSONFileReader implements JSONReader {

    public File getResultJSON() {
        return resultJSON;
    }

    public void setResultJSON(File resultJSON) {
        this.resultJSON = resultJSON;
    }

    private File resultJSON;

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    private String outputFilePath;

    //private JsonObject object;

    public JSONFileReader() {

    }
    
    //private boolean csClassAlreadyExists;

    @Override
    public CodeShovelOutputFileRoot readFromJSONFile(File file) {
    	JsonReader jsonReader = null; CodeShovelOutputFileRoot csofr= null;
        try {
            jsonReader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
        	jsonReader.setLenient(true);
            Gson csGson = new Gson();
            csofr = csGson.fromJson(jsonReader, CodeShovelOutputFileRoot.class);
            jsonReader.close();
        } catch(JsonSyntaxException | IOException e) {
        	e.printStackTrace();
        }
        return csofr;
    }

    @Override
    public void writeInDB(CodeShovelMethod method, CodeShovelClass csClass) {
    	int i, csClassInt = 0, csMethodInt;
    	try {
    		i = DbHandler.codeShovelClassAlreadyExists(csClass.getStartCommitName(), csClass.getPreviousCommitName(), csClass.getCompleteSourceFileName());
    		if(DbHandler.codeShovelClassAlreadyExists(csClass.getStartCommitName(), csClass.getPreviousCommitName(), csClass.getCompleteSourceFileName())==0) {
    			csClassInt = DbHandler.insertCodeShovelClass(csClass);
    	    	csMethodInt = DbHandler.insertCodeShovelMethod(method);
    		} else {
    			csMethodInt = DbHandler.insertCodeShovelMethod(method);
    		}
    		if(Download.isCodeShovelPrintLines()) {
    			this.printJSONFileReaderPrintLines(csClass, i, csClassInt, csMethodInt);
    		}
        } catch (SQLException | NullPointerException e) {
        	if(e instanceof NullPointerException) {
        		
        	} else {
        		e.printStackTrace();
        	}
        }
    }
    
    private void printJSONFileReaderPrintLines(CodeShovelClass csClass, int i, int csClassInt, int csMethodInt) {
    	System.out.println("JSONFileReader: WRITE IN DB");
    	System.out.println("JSONFileReader: CS CLASS ALREADY EXISTS (" + csClass.getStartCommitName() + "," + csClass.getPreviousCommitName() + ","+ csClass.getCompleteSourceFileName() + ") = " +i);
    	System.out.println("JSONFileReader: CS CLASS INT = " + csClassInt);
    	System.out.println("JSONFileReader: CS CLASS WRITTEN");
    	System.out.println("JSONFileReader: CS METHOD INT = " + csMethodInt);
    	System.out.println("JSONFileReader: CS METHOD WRITTEN");
    }

}
