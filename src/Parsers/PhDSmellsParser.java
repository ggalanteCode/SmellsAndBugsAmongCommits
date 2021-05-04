package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import models.Method;
import models.Class;
import models.Package;
import models.Variable;
import database.DbHandler;
import models.Smell;
import models.DataClumps;

/**
 *This class parses the "result.txt" file produced by the "PhDProjectScripts" tool,
 *started by the PhDSmells class and inserts the results into the "sbac" DB.
 * @author Federico Caspani & Davide Altieri
 */
public class PhDSmellsParser {

    String analyzableFileName = "result.txt";
    String smell, messageChainsTemp, idCommit, projectUrl;
    ArrayList<String> classesPath, packages;
    int[] existsClass, existsPackage;
    Class c1, c2;
    Method m;
    Package pac1, pac2;
    Variable v;

    public PhDSmellsParser(String idCommit, String projectUrl) {
        this.idCommit = idCommit;
        this.projectUrl = projectUrl;
    }

    /**
     * Read the entire document "result.txt" via the BufferedReader reader passed as a parameter.
     * @param reader BufferedReader
     */
    public void execute(BufferedReader reader) {

        try {

            String line;

            boolean continueReading = true;
            boolean startAnalysis = false;

            do {

                line = reader.readLine().trim();
                System.out.println(line);

                switch (line) {

                    case "***START BAD SMELLS TRANSCRIPTION***" -> startAnalysis = true;

                    case "Switch Statement Bad Smell was found in:" -> smell = "Switch Statement";
                    case "Speculative Generality Bad Smell was found in:" -> smell = "Speculative Generality";
                    case "Middle Man Bad Smell was found in:" -> smell = "Middle Man";
                    case "Message Chains Bad Smell was found in:" -> smell = "Message Chains";
                    case "Data Clumps Bad Smell was found in:" -> smell = "Data Clumps";

                    case "***END BAD SMELLS TRANSCRIPTION***" -> continueReading = false;

                    default -> {
                        if (!line.isBlank() && startAnalysis) {
                            analyzeAndWrite(line);
                        }
                    }
                }

            } while (continueReading);

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method parses the row passed as a parameter.
     * If the line shows class paths, the classes and their relative paths are saved in specific fields of the class.
     * If the line represents a smell instance, the analysis results are saved to the DB.
     * @param line String
     */
    private void analyzeAndWrite(String line) {

        String classPath;
        boolean beginWritePath;

        if (line.startsWith("FILE")){

            //Delete previous paths and split paths in case of more directoy per string.
            classesPath = new ArrayList<>();
            packages = new ArrayList<>();
            existsClass = new int[2];
            existsPackage = new int[2];
            String[] paths = line.split("&");

            //Obtain class path in project's source.
            for (String p : paths) {
                classPath = "";
                beginWritePath = false;

                p = p.replace(File.separatorChar,'§');
                String[] path = p.split("§");

                if (p.contains("§src§")) {

                    for (String s : path) {
                        packages.add(path[path.length-2]);
                        if (!beginWritePath & s.equals("src")) {
                            beginWritePath = true;
                        } else if (beginWritePath) {
                            classPath += s+".";
                        }
                    }

                } else {

                    for (String s : path) {
                        packages.add(path[path.length-2]);
                        if (!beginWritePath & s.contains("SBAC")) {
                            beginWritePath = true;
                        } else if (beginWritePath) {
                            classPath += s+".";
                        }
                    }
                }

                classPath = classPath.substring(0, classPath.lastIndexOf(".java"));
                classesPath.add(classPath);
                int i;
                if(classesPath.size() == 1)
                    i=0;
                else
                    i=1;
                try {
                    //idClass
                    int lastpoint = (classesPath.get(i)).lastIndexOf(".");
                    String className = classesPath.get(i).substring(lastpoint + 1);
                    existsClass[i] = DbHandler.classInProject(className, classesPath.get(i));
                    Class c0 = new Class(className, classesPath.get(i));
                    if (existsClass[i] == 0) {
                        existsClass[i] = DbHandler.insertClass(c0);
                    } else {
                        String Cpath = DbHandler.getClassPath(existsClass[i]);
                        if (Cpath == null) {
                            DbHandler.updateClassPath(classesPath.get(i), existsClass[i]);
                        } else {
                            if (!DbHandler.getClassPath(existsClass[i]).equals(classesPath.get(i)))
                                existsClass[i] = DbHandler.insertClass(c0);
                            else
                                c0.setId(existsClass[i]);
                        }
                    }

                    //idPackage
                    Package pac0 = new Package(packages.get(i));
                    int indexOfClass = (classesPath.get(i)).indexOf(className);
                    String packagePath = (classesPath.get(i)).substring(0, indexOfClass-1);
                    pac0.setPath(packagePath);
                    existsPackage[i] = DbHandler.packageExist(packages.get(i));
                    if(existsPackage[i] == 0) {
                        existsPackage[i] = DbHandler.insertPackage(pac0);
                        pac0.setId(existsPackage[i]);
                    } else {
                        pac0.setId(existsPackage[i]);
                    }

                    if(i==0) {
                        c1 = c0;
                        pac1 = pac0;
                    } else {
                        c2 = c0;
                        pac2 = pac0;
                    }



                } catch (SQLException e) {e.printStackTrace();}
            }

        } else {

            String lineNumber = "";
            String methodName = "";
            String variableName = "";
            String variableType = "";
            Smell sm;
            DataClumps dc;

            switch (smell) {

                case "Switch Statement" -> {
                    String[] switchSmell = line.split("[()']+");
                    lineNumber = switchSmell[1].trim();
                    if(!(switchSmell[4].equals("."))) {
                        methodName = switchSmell[3].trim() + "(" + switchSmell[4].trim() + ")";
                    } else {
                        methodName = switchSmell[3].trim() + "()";
                    }

                    try {
                        //idMethod
                        int idMethod = InsertMethod(methodName, c1);

                        //Writing on DB
                        sm = new Smell("Switch Statement", 0.0);
                        DbHandler.insertSmell(sm, idCommit, 0, idMethod, existsClass[0], existsPackage[0], 0);

                    } catch(SQLException e) {e.printStackTrace();}

                }
                case "Speculative Generality" -> {
                    String[] speculativeSmell = line.split("[()']+");
                    lineNumber = speculativeSmell[1].trim();
                    if (line.startsWith("Speculative Generality unused parameter")) {
                        variableName = speculativeSmell[3].trim();
                        variableType = speculativeSmell[5].trim();
                        methodName = speculativeSmell[7].trim() + "(" + speculativeSmell[8].trim() + ")";
                    }

                    try {
                        //idVariable
                        int idVariable = InsertVariable(variableName, variableType, c1);

                        //idMethod
                        int idMethod = InsertMethod(methodName, c1);

                        //Writing on DB
                        sm = new Smell("Speculative Generality", 0.0);
                        DbHandler.insertSmell(sm, idCommit, idVariable, idMethod, existsClass[0], existsPackage[0], Integer.valueOf(lineNumber));

                    } catch(SQLException e) {e.printStackTrace();}
                    //Scrivere su DB//
                }
                case "Middle Man" -> {
                    String[] middleSmell = line.split("[()']+");
                    lineNumber = middleSmell[1].trim();

                    try {
                        //Writing on DB
                        sm = new Smell("Middle Man", 0.0);
                        DbHandler.insertSmell(sm, idCommit, 0, 0, existsClass[0], existsPackage[0], Integer.valueOf(lineNumber));
                    } catch(SQLException e) {e.printStackTrace();}
                }
                case "Message Chains" -> {
                    if (line.startsWith("Message Chains")) {
                        messageChainsTemp = line;
                    } else {
                        line = messageChainsTemp + line;
                    }
                    if (line.endsWith("]")) {

                        String[] strings = line.split("[//(//)]+", 3);
                        String[] chainsSmell = strings[2].split("[\\[\\]]+");
                        lineNumber = strings[1].trim();
                        methodName = chainsSmell[1].trim();

                        try {
                            //idMethod
                            int idMethod = InsertMethod(methodName, c1);

                            //Writing on DB
                            sm = new Smell("Message Chains", 0.0);
                            DbHandler.insertSmell(sm, idCommit, 0, idMethod, existsClass[0], existsPackage[0], Integer.valueOf(lineNumber));
                        } catch(SQLException e) {e.printStackTrace();}
                    }
                }
                case "Data Clumps" -> {
                    String[] clumpsSmell;
                    if (line.startsWith("Parameters in method")) {
                        clumpsSmell = line.split("Parameters in method | and | was found duplicated");
                        methodName = clumpsSmell[1].substring(clumpsSmell[1].substring(0, clumpsSmell[1].lastIndexOf(".")).lastIndexOf(".") + 1) + " & " + clumpsSmell[2].substring(clumpsSmell[2].substring(0, clumpsSmell[2].lastIndexOf(".")).lastIndexOf(".") + 1);
                        String[] methodsName = methodName.split("[&.]+");

                        try {
                            //idMethod
                            int idMethod1 = InsertMethod(methodsName[1].trim(), c1);
                            int idMethod2 = InsertMethod(methodsName[3].trim(), c2);

                            //Writing on DB
                            dc = new DataClumps(methodName, "not required", idCommit, existsPackage[0] + ", " + existsPackage[1], existsClass[0] + ", " + existsClass[1], idMethod1 + ", " + idMethod2, "not required");
                            DbHandler.insertDataClumps(dc);
                        } catch(SQLException e) {e.printStackTrace();}
                    } else if (line.startsWith("Fields")) {
                        clumpsSmell = line.split("Fields  | was found duplicated|,");

                        //idVariables
                        try {
                            String idVariables = "";
                            for (int i=1; i<clumpsSmell.length; i+=2) {
                                idVariables += InsertVariable(clumpsSmell[i], clumpsSmell[i+1].substring(6), c1) + ", ";
                                idVariables += InsertVariable(clumpsSmell[i], clumpsSmell[i+1].substring(6), c2) + ", ";
                                variableName += clumpsSmell[i] + ", ";
                            }
                            variableName = variableName.substring(0, variableName.length()-2);
                            idVariables = idVariables.substring(0, idVariables.length()-2);


                            //Writing on DB
                            dc = new DataClumps("not required", variableName, idCommit, existsPackage[0] + ", " + existsPackage[1], existsClass[0] + ", " + existsClass[1], "not required", idVariables);
                            DbHandler.insertDataClumps(dc);
                        } catch(SQLException e) {e.printStackTrace();}
                    }
                }
            }
            assert !classesPath.isEmpty();
            System.out.print("CLASSI: ");
            for(String s : classesPath) {
                System.out.print(s + " ");
            }
            System.out.print(" SMELL: " + smell);
            System.out.print(" PRINTLN VARIABLE: " + variableName);
            System.out.print(" PRINTLN METHOD: " + methodName);
            System.out.print(" PRINTLN LINENUMBER: " + lineNumber + "\n");
        }
    }

    public int InsertMethod(String nameMethod, Class c) throws SQLException {
        //idMethod
        m = new Method();
        m.setName(nameMethod);
        int existsMethod = DbHandler.MethodInClass(m.getName(), (int) c.getId());
        if (existsMethod == 0) {
            existsMethod = DbHandler.insertMethod(m, (int) c.getId());
            m.setId(existsMethod);
        } else {
            m.setId(existsMethod);
        }
        return existsMethod;
    }

    public int InsertVariable(String nameVariable, String typeVariable, Class c) throws SQLException {
        //idVariable
        v = new Variable();
        v.setName(nameVariable);
        v.setRole(typeVariable);
        int existsVariable = DbHandler.VariableInClass(v.getName(), (int) c.getId());
        if (existsVariable == 0) {
            existsVariable = DbHandler.insertVariable(v, (int) c.getId());
            v.setId(existsVariable);
        } else {
            v.setId(existsVariable);
        }
        return existsVariable;
    }

    public void InsertCommPac(int idp, String idc) throws SQLException {
        DbHandler.insertCommPac(idp, idc);
    }

    public void InsertClassPac(int idp, int idcl) throws SQLException {
        DbHandler.insertClassPac(idp, idcl);
    }

    public String getAnalyzableFileName() {
        return analyzableFileName;
    }
}
