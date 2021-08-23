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
import models.MultiCodeSmell;

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
    int dataclumpblockmaxid;

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
            String nextLine;

            boolean continueReading = true;
            boolean startAnalysis = false;

            dataclumpblockmaxid = DbHandler.getLastIDBlock();
            nextLine = reader.readLine().trim();

            do {
                line = nextLine.trim();
                if((nextLine = reader.readLine()) == null)
                    line = "***END BAD SMELLS TRANSCRIPTION***";

                switch (line) {

                    case "***START BAD SMELLS TRANSCRIPTION***":
                        startAnalysis = true;
                        break;

                    case "Switch Statement Bad Smell was found in:":
                        smell = "Switch Statement";
                        break;

                    case "Speculative Generality Bad Smell was found in:":
                        smell = "Speculative Generality";
                        break;

                    case "Middle Man Bad Smell was found in:":
                        smell = "Middle Man";
                        break;

                    case "Message Chains Bad Smell was found in:":
                        smell = "Message Chains";
                        break;

                    case "Data Clumps Bad Smell was found in:":
                        smell = "Data Clumps";
                        break;

                    case "***END BAD SMELLS TRANSCRIPTION***":
                        continueReading = false;
                        break;

                    default:
                        if (!line.isBlank() && startAnalysis) {
                            analyzeAndWrite(line);
                        }
                        break;

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
                    try {
                        int indexOfClass = (classesPath.get(i)).indexOf(className);
                        String packagePath = (classesPath.get(i)).substring(0, indexOfClass-1);
                        pac0.setPath(packagePath);
                        existsPackage[i] = DbHandler.packageExist(packages.get(i));
                        if(existsPackage[i] == 0) {
                            existsPackage[i] = DbHandler.insertPackage(pac0);
                            InsertCommPac(existsPackage[i], idCommit);
                            InsertClassPac(existsPackage[i], existsPackage[i]);
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


                    } catch (StringIndexOutOfBoundsException oobException) {
                        packages.add(i,null);

                        if(i==0) {
                            c1 = c0;
                            pac1 = new Package(null);
                            pac1.setId(0);
                        } else {
                            c2 = c0;
                            pac2 = new Package(null);
                            pac2.setId(0);
                        }
                    }

                } catch (SQLException e) {e.printStackTrace();}
            }

        } else {

            String lineNumber = "";
            String methodName = "";
            String variableName = "";
            String variableType = "";
            Smell sm;
            MultiCodeSmell mcs;

            switch (smell) {

                case "Switch Statement":
                    String[] switchSmell = line.split("[()']+");
                    lineNumber = switchSmell[1].trim();
                    if(switchSmell[3].equals(".")) {
                        methodName = "";
                    } else {
                        if (!(switchSmell[4].equals("."))) {
                            methodName = switchSmell[3].trim() + "(" + switchSmell[4].trim() + ")";
                        } else {
                            methodName = switchSmell[3].trim() + "()";
                        }
                    }

                    try {
                        //idMethod
                        int idMethod = InsertMethod(methodName, c1);

                        //Writing on DB
                        sm = new Smell("Switch Statement", 0.0);
                        DbHandler.insertSmell(sm, idCommit, 0, idMethod, existsClass[0], existsPackage[0], Integer.valueOf(lineNumber));

                    } catch(SQLException e) {e.printStackTrace();}
                    break;


                case "Speculative Generality":
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
                    break;

                case "Middle Man":
                    String[] middleSmell = line.split("[()']+");
                    lineNumber = middleSmell[1].trim();

                    try {
                        //Writing on DB
                        sm = new Smell("Middle Man", 0.0);
                        DbHandler.insertSmell(sm, idCommit, 0, 0, existsClass[0], existsPackage[0], Integer.valueOf(lineNumber));
                    } catch(SQLException e) {e.printStackTrace();}
                    break;

                case "Message Chains":
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
                    break;

                case "Data Clumps":
                    String[] clumpsSmell;
                    if (line.startsWith("Parameters in method")) {
                        clumpsSmell = line.split("Parameters in method | and | was found duplicated");
                        String methodName1 = clumpsSmell[1].substring(clumpsSmell[1].lastIndexOf(".") + 1).trim();
                        String methodName2 = clumpsSmell[2].substring(clumpsSmell[2].lastIndexOf(".") + 1).trim();

                        try {
                            //idMethod
                            int idMethod1 = InsertMethod(methodName1, c1);
                            int idMethod2 = InsertMethod(methodName2, c2);

                            //idMultiCodeBlock & Writing on DB
                            int IDBlock = DbHandler.MethodExistMultiCodeSmell("Data Clumps", existsClass[0], idMethod1);
                            if(IDBlock==0) {
                                IDBlock = DbHandler.MethodExistMultiCodeSmell("Data Clumps", existsClass[1], idMethod2);
                                if(IDBlock==0) {
                                    IDBlock = ++dataclumpblockmaxid;
                                    mcs = new MultiCodeSmell(methodName1, "not required", idCommit, existsPackage[0], existsClass[0], idMethod1, 0);
                                    DbHandler.insertMultiCodeSmell(IDBlock, "Data Clumps", mcs);
                                    mcs = new MultiCodeSmell(methodName2, "not required", idCommit, existsPackage[1], existsClass[1], idMethod2, 0);
                                    DbHandler.insertMultiCodeSmell(IDBlock, "Data Clumps", mcs);
                                } else {
                                    mcs = new MultiCodeSmell(methodName1, "not required", idCommit, existsPackage[0], existsClass[0], idMethod1, 0);
                                    DbHandler.insertMultiCodeSmell(IDBlock, "Data Clumps", mcs);
                                }
                            } else {
                                mcs = new MultiCodeSmell(methodName2, "not required", idCommit, existsPackage[1], existsClass[1], idMethod2, 0);
                                DbHandler.insertMultiCodeSmell(IDBlock, "Data Clumps", mcs);
                            }

                        } catch(SQLException e) {e.printStackTrace();}
                    } else if (line.startsWith("Fields")) {
                        clumpsSmell = line.split("Fields  | was found duplicated|,");

                        //idVariables
                        try {
                            ArrayList<Integer> idVariables = new ArrayList<Integer>();
                            ArrayList<String> variablesName = new ArrayList<String>();
                            int currentIdVariable;
                            for (int i=1; i<clumpsSmell.length; i+=2) {
                                currentIdVariable = InsertVariable(clumpsSmell[i].trim(), clumpsSmell[i+1].substring(6).trim(), c1);
                                idVariables.add(currentIdVariable);
                                currentIdVariable = InsertVariable(clumpsSmell[i].trim(), clumpsSmell[i+1].substring(6).trim(), c2);
                                idVariables.add(currentIdVariable);
                                variablesName.add(clumpsSmell[i].trim());
                            }

                            //Writing on DB
                            for(int i=0; i<variablesName.size(); i++) {
                                dataclumpblockmaxid++;
                                mcs = new MultiCodeSmell("not required", variablesName.get(i), idCommit, existsPackage[0], existsClass[0], 0, idVariables.get(i*2));
                                DbHandler.insertMultiCodeSmell(dataclumpblockmaxid, "Data Clumps", mcs);
                                mcs = new MultiCodeSmell("not required", variablesName.get(i), idCommit, existsPackage[1], existsClass[1], 0, idVariables.get((i*2)+1));
                                DbHandler.insertMultiCodeSmell(dataclumpblockmaxid, "Data Clumps", mcs);
                            }
                        } catch(SQLException e) {e.printStackTrace();}
                    }
                    break;

            }
        }
    }

    /**
     * Insert into the table "Method" of the Database
     * @param nameMethod String
     * @param c Class
     * @return the method id
     */
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

    /**
     * Insert into the table "Variable" of the Database
     * @param nameVariable String
     * @param typeVariable String
     * @param c Class
     * @return the variable id
     */
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

    /**
     * Insert into the table "CommPac" of the Database
     * @param idp int
     * @param idc String
     */
    public void InsertCommPac(int idp, String idc) throws SQLException {
        DbHandler.insertCommPac(idp, idc);
    }

    /**
     * Insert into the table "ClassPac" of the Database
     * @param idp int
     * @param idcl int
     */
    public void InsertClassPac(int idp, int idcl) throws SQLException {
        DbHandler.insertClassPac(idp, idcl);
    }

    public String getAnalyzableFileName() {
        return analyzableFileName;
    }
}
