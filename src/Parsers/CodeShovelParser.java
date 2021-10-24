package Parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import database.DbHandler;
import models.CodeShovelClass;
import models.CodeShovelMethod;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * this class parses the results in the output file produced by the tool for method-level source code histories
 * (CodeShovel) and writes the parsed results on the 'sbac' database
 * @author Giovanni Galante
 */
public class CodeShovelParser {

    public String getAnalyzableFile() {
        return analyzableFile;
    }

    private String analyzableFile = "results.json";
    private String urlProject;

    public void setAnalyzableFile(String analyzableFile) {
        this.analyzableFile = analyzableFile;
    }

    public String getUrlProject() {
        return urlProject;
    }

    public void setUrlProject(String urlProject) {
        this.urlProject = urlProject;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    private String commitId;

    public JSONReader getJsonReader() {
        return jsonReader;
    }

    public void setJsonReader(JSONReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    private JSONReader jsonReader;

    public CodeShovelParser(String urlProject, String commitId, JSONReader jsonReader) {
        setJsonReader(jsonReader);
        this.urlProject = urlProject;
        this.commitId = commitId;
    }

    /**
     * reads the output file
     */
    public void execute() {
            JsonObject object = jsonReader.readFromJSONFile(this.getAnalyzableFile());
            JsonObject changeHistoryDetails = object.get("changeHistoryDetails").getAsJsonObject();
            String mostRecentCommit = object.get("changeHistory").getAsJsonArray().get(0).getAsString();
            JsonObject currentVersion;
            if (object.get("startCommitName").getAsString().equals(mostRecentCommit)) {
                currentVersion = object.get("startCommitName").getAsJsonObject();
            }else currentVersion = changeHistoryDetails.get(mostRecentCommit).getAsJsonObject();
            CodeShovelMethod method = getCodeShovelMethodFromJsonObject(object, currentVersion);
            CodeShovelClass csClass = getCodeShovelClassFromJsonObject(object,method);
            writeinSBACDataBase(method, csClass);
    }

    private CodeShovelMethod getCodeShovelMethodFromJsonObject(JsonObject object, JsonObject currentVersion) {
        CodeShovelMethod method = new CodeShovelMethod();
        method.setSourceFileName(object.get("sourceFileName").getAsString());
        method.setFunctionName(object.get("functionName").getAsString());
        method.setStartCommitName(object.get("startCommitName").getAsString());
        method.setPreviousCommitName(object.get("changeHistory").getAsJsonArray().get(0).getAsString());
        String diff[] = calculateDiff(currentVersion);
        if (diff==null) {
            method.setChangeType(newOldOrModified(currentVersion));
            return method;
        }
        switch (diff[0]) {
            case "added":
                method.setLinesOfCodeAdded(Integer.parseInt(diff[1]));
                method.setLinesOfCodeModified(Integer.parseInt(diff[2]));
                break;
            case "deleted":
                method.setLinesOfCodeDeleted(Integer.parseInt(diff[1]));
                method.setLinesOfCodeModified(Integer.parseInt(diff[2]));
                break;
            case "modified":
                method.setLinesOfCodeModified(Integer.parseInt(diff[1]));
                break;
        }
        method.setChangeType(newOldOrModified(currentVersion));
        return method;
    }

    private String newOldOrModified(JsonObject currentVersion) {
        if (currentVersion.get("type").getAsString().startsWith("Yintroduced")) {
            return "NEW";
        } else if (currentVersion.get("type").getAsString().startsWith("Ynochange")) {
            return "OLD";
        } else {
            return "MODIFIED(" + currentVersion.get("type").getAsString() + ")";
        }
    }

    private CodeShovelClass getCodeShovelClassFromJsonObject(JsonObject object, CodeShovelMethod method) {
        CodeShovelClass csClass = new CodeShovelClass();
        csClass.setSourceFileName(object.get("sourceFileName").getAsString());

        csClass.setChangeType(object.get("type").getAsString());
        csClass.setStartCommitName(object.get("startCommitName").getAsString());
        csClass.setPreviousCommitName(object.get("changeHistory").getAsJsonArray().get(0).getAsString());
        return csClass;
    }

    private String[] calculateDiff(JsonObject currentVersion) {
        Stream<String> minuslines = null, plusLines = null;
        if (currentVersion.get("type").getAsString().startsWith("Yfilerename")) return null;   //empty diff string
        else if (currentVersion.get("type").getAsString().startsWith("Ymultichange")) {
            long totalPlusLines = 0, totalMinusLines = 0;
            JsonArray subchanges = currentVersion.get("subchanges").getAsJsonArray();
            for (Iterator<JsonElement> it = subchanges.iterator(); it.hasNext(); ) {
                JsonObject object1 = it.next().getAsJsonObject();
                Stream<String> lines = object1.get("diff").getAsString().lines();
                plusLines = lines.filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.startsWith("+");
                    }
                });
                minuslines = lines.filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.startsWith("-");
                    }
                });
                totalMinusLines+= minuslines.count(); totalPlusLines+=plusLines.count();
            }
            return comparePlusAndMinusLines(totalPlusLines, totalMinusLines);
        } else {
            Stream<String> lines = currentVersion.get("diff").getAsString().lines();
            plusLines = lines.filter(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.startsWith("+");
                }
            });
            minuslines = lines.filter(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.startsWith("-");
                }
            });
            return comparePlusAndMinusLines(plusLines.count(), minuslines.count());
        }
    }

    private String[] comparePlusAndMinusLines(long nPlusLines, long nMinusLines) {
        String diff[] = new String[3];
        if (nPlusLines > nMinusLines) {
            diff[0] = "added";
            diff[1]= String.valueOf(nPlusLines-nMinusLines);  //LOC added from previousVersion -> currentVersion
            diff[2] = String.valueOf(nMinusLines); //LOC modified
        } else if (nMinusLines > nPlusLines) {
            diff[0] = "deleted";
            diff[1]= String.valueOf(nMinusLines - nPlusLines);//LOC deleted from previousVersion -> currentVersion
            diff[2] =String.valueOf(nPlusLines); //LOC modified
        } else {
            diff[0] = "modified";
            diff[1] = diff[2] = String.valueOf(nPlusLines); //LOC modified
        }
        return diff;
    }

    /**
     * analyze and write the output file's results in the database
     * @param method
     * @param csClass
     */
    public void writeinSBACDataBase(CodeShovelMethod method, CodeShovelClass csClass) {
        jsonReader.writeInDB();
        try {
            DbHandler.insertCodeShovelMethod(method, (int) method.getIdentifier());
            DbHandler.insertCodeShovelClass(csClass, (int) csClass.getIdentifier());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
