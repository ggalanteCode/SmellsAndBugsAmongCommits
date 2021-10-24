
package Parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.*;

/**
 * this class parses the results in the JSON output file produced by the tool for method-level source code
 * histories (CodeShovel) and writes the parsed results on the 'sbac' database
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

    private JsonObject object;

    public JSONFileReader() {

    }

    @Override
    public JsonObject readFromJSONFile(String file) {
        JsonReader jsonReader = null;
        object = new JsonObject();
        try {
            jsonReader = new JsonReader(new FileReader(resultJSON.getName()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        jsonReader.setLenient(true);
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                    String arrayName = jsonReader.nextName();
                    object.add(arrayName, readJSONArray(jsonReader, arrayName));
                } else if (jsonReader.peek()== JsonToken.BEGIN_OBJECT) {
                    String objectName = jsonReader.nextName();
                    object.add(objectName, readJSONObject(jsonReader, objectName));
                } else object.addProperty(jsonReader.nextName(), jsonReader.nextString());

            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }

    private JsonArray readJSONArray(JsonReader jsonReader, String member) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                    String arrayName = jsonReader.nextName();
                    object.getAsJsonArray(member).addAll(readJSONArray(jsonReader, arrayName));
                    return object.getAsJsonArray(member);
                } else if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                    String objectName = jsonReader.nextName();
                    object.getAsJsonArray(member).add(readJSONObject(jsonReader,objectName));
                    return object.getAsJsonArray(member);
                } else {object.getAsJsonArray(member).add(jsonReader.nextString());}
            }
            jsonReader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object.getAsJsonArray(member);
    }

    private JsonObject readJSONObject(JsonReader jsonReader, String member) {
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                    String arrayName = jsonReader.nextName();
                    object.getAsJsonObject(member).add(arrayName, readJSONArray(jsonReader, arrayName));
                } else if (jsonReader.peek()== JsonToken.BEGIN_OBJECT) {
                    String objectName = jsonReader.nextName();
                    object.getAsJsonObject(member).add(objectName, readJSONObject(jsonReader,objectName));
                } else object.getAsJsonObject(member).addProperty(jsonReader.nextName(), jsonReader.nextString());

            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object.getAsJsonObject(member);
    }

    @Override
    public void writeInDB() {

    }

}
