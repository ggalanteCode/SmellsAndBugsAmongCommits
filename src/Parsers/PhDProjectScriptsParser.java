package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import models.Method;
import models.Class;
import models.Package;
import models.Variable;
import database.DbHandler;
import models.Smell;

public class PhDProjectScriptsParser {

    String smell, messageChainsTemp, idCommit, projectUrl;
    ArrayList<String> classes, pathClasses, packages;
    Class c1, c2;
    Method m;
    Package p;
    Variable v;

    public PhDProjectScriptsParser(String idCommit, String projectUrl) {
        this.idCommit = idCommit;
        this.projectUrl = projectUrl;
    }

    public void execute(BufferedReader reader) {

        try {

            String line;

            boolean continueReading = true;

            do {

                line = reader.readLine().trim();
                System.out.println(line);

                switch (line) {

                    case "Switch Statement Bad Smell was found in:" -> smell = "Switch Statement";
                    case "Speculative Generality Bad Smell was found in:" -> smell = "Speculative Generality";
                    case "Middle Man Bad Smell was found in:" -> smell = "Middle Man";
                    case "Message Chains Bad Smell was found in:" -> smell = "Message Chains";
                    case "Data Clumps Bad Smell was found in:" -> smell = "Data Clumps";

                    case "***END BAD SMELLS TRANSCRIPTION***" -> continueReading = false;

                    default -> {
                        if (!line.isBlank()) {
                            analyzeAndWrite(line);
                        }
                    }
                }

            } while (continueReading);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeAndWrite(String line) {

        String classPath;
        boolean beginWritePath;

        if (line.startsWith("FILE")){

            //Delete previous paths and split paths in case of more directoy per string.
            classes = new ArrayList<>();
            pathClasses = new ArrayList<>();
            packages = new ArrayList<>();
            String[] paths = line.split("&");

            //Obtain class path in project's source.
            for (String p : paths) {
                pathClasses.add(p);
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
                        if (!beginWritePath & s.contains("SBAC")) {
                            beginWritePath = true;
                        } else if (beginWritePath) {
                            classPath += s+".";
                        }
                    }
                }

                classPath = classPath.substring(0, classPath.lastIndexOf(".java"));
                classes.add(classPath);
            }

        } else {

            String lineNumber = "";
            String methodName = "";
            String variableName = "";
            Smell sm;

            switch (smell) {

                case "Switch Statement" -> {
                    String[] switchSmell = line.split("[()']+");
                    lineNumber = switchSmell[1].trim();
                    methodName = switchSmell[3].trim();

                    try {

                        //idClass
                        int existsClass = DbHandler.classInProject(classes.get(0), projectUrl);
                        c1 = new Class(classes.get(0), pathClasses.get(0));
                        if(existsClass==0) {
                            c1.setId(DbHandler.insertClass(c1));
                        } else {
                            String path = DbHandler.getClassPath(existsClass);
                            if(path==null) {
                                DbHandler.updateClassPath(pathClasses.get(0), existsClass);
                                c1.setId(existsClass);
                            } else {
                                if(!DbHandler.getClassPath(existsClass).equals(classes.get(0)))
                                    c1.setId(DbHandler.insertClass(c1));
                                else
                                    c1.setId(existsClass);
                            }
                        }

                        //idMethod
                        m = new Method();
                        m.setName(methodName);
                        int existsMethod = DbHandler.MethodInClass(m.getName(), (int) c1.getId());
                        if (existsMethod == 0) {
                            existsMethod = DbHandler.insertMethod(m, (int) c1.getId());
                            m.setId(existsMethod);
                        } else {
                            m.setId(existsMethod);
                        }

                        //idPackage
                        p = new Package(packages.get(0));
                        int indexOfClass = (pathClasses.get(0)).indexOf(classes.get(0));
                        String packagePath = (pathClasses.get(0)).substring(0, indexOfClass);
                        p.setPath(packagePath);
                        int existsPackage = DbHandler.packageExist(packages.get(0));
                        if(existsPackage == 0) {
                            existsPackage = DbHandler.insertPackage(p);
                            p.setId(existsPackage);
                        } else {
                            p.setId(existsPackage);
                        }


                        //Writing on DB
                        sm = new Smell("Switch Statement", 0.0);
                        DbHandler.insertSmell(sm, idCommit, existsMethod, existsClass, existsPackage);

                    } catch(SQLException e) {e.printStackTrace();}

                }
                case "Speculative Generality" -> {
                    String[] speculativeSmell = line.split("[()']+");
                    lineNumber = speculativeSmell[1].trim();
                    if (line.startsWith("Speculative Generality unused parameter")) {
                        variableName = speculativeSmell[3].trim();
                        methodName = speculativeSmell[5].trim();
                    }
                    //Scrivere su DB//
                }
                case "Middle Man" -> {
                    String[] middleSmell = line.split("[()']+");
                    lineNumber = middleSmell[1].trim();
                    //Scrivere su DB//
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
                        //Scrivere su DB//
                    }
                }
                case "Data Clumps" -> {
                    String[] clumpsSmell;
                    if (line.startsWith("Parameters in method")) {
                        clumpsSmell = line.split("Parameters in method | and | was found duplicated");
                        methodName = clumpsSmell[1].substring(clumpsSmell[1].substring(0, clumpsSmell[1].lastIndexOf(".")).lastIndexOf(".") + 1) + " & " + clumpsSmell[2].substring(clumpsSmell[2].substring(0, clumpsSmell[2].lastIndexOf(".")).lastIndexOf(".") + 1);

                        //Scrivere su DB//
                    } else if (line.startsWith("Fields")) {
                        clumpsSmell = line.split("Fields  | was found duplicated");
                        variableName = clumpsSmell[1];

                        //Scrivere su DB//
                    }
                }
            }
            assert !classes.isEmpty();
            System.out.print("CLASSI: ");
            for(String s : classes) {
                System.out.print(s + " ");
            }
            System.out.print(" SMELL: " + smell);
            System.out.print(" PRINTLN VARIABLE: " + variableName);
            System.out.print(" PRINTLN METHOD: " + methodName);
            System.out.print(" PRINTLN LINENUMBER: " + lineNumber + "\n");
        }
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\Federico\\Desktop\\Programmi\\Intelli J\\sbac\\src\\tools\\toolsfolder\\PhDProjectScripts\\result.xml";
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
