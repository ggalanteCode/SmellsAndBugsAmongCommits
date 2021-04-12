package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class PhDProjectScriptsParser {

    String smell, messageChainsTemp, idCommit, projectUrl;
    ArrayList<String> classes = new ArrayList<>();
    String analyzableFileName = "result.txt";

    public PhDProjectScriptsParser(String idCommit, String projectUrl) {
        this.idCommit = idCommit;
        this.projectUrl = projectUrl;
    }

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
            String[] paths = line.split("&");

            //Obtain class path in project's source.
            for (String p : paths) {
                classPath = "";
                beginWritePath = false;

                p = p.replace(File.separatorChar,'§');
                String[] path = p.split("§");

                if (p.contains("§src§")) {

                    for (String s : path) {
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
            String method = "";
            String variable = "";

            switch (smell) {

                case "Switch Statement" -> {
                    String[] switchSmell = line.split("[()']+");
                    lineNumber = switchSmell[1].trim();
                    method = switchSmell[3].trim();
                    //Scrivere su DB//
                }
                case "Speculative Generality" -> {
                    String[] speculativeSmell = line.split("[()']+");
                    lineNumber = speculativeSmell[1].trim();
                    if (line.startsWith("Speculative Generality unused parameter")) {
                        variable = speculativeSmell[3].trim();
                        method = speculativeSmell[5].trim();
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
                        method = chainsSmell[1].trim();
                        //Scrivere su DB//
                    }
                }
                case "Data Clumps" -> {
                    String[] clumpsSmell;
                    if (line.startsWith("Parameters in method")) {
                        clumpsSmell = line.split("Parameters in method | and | was found duplicated");
                        method = clumpsSmell[1].substring(clumpsSmell[1].substring(0, clumpsSmell[1].lastIndexOf(".")).lastIndexOf(".") + 1) + " & " + clumpsSmell[2].substring(clumpsSmell[2].substring(0, clumpsSmell[2].lastIndexOf(".")).lastIndexOf(".") + 1);

                        //Scrivere su DB//
                    } else if (line.startsWith("Fields")) {
                        clumpsSmell = line.split("Fields  | was found duplicated");
                        variable = clumpsSmell[1];

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
            System.out.print(" PRINTLN VARIABLE: " + variable);
            System.out.print(" PRINTLN METHOD: " + method);
            System.out.print(" PRINTLN LINENUMBER: " + lineNumber + "\n");
        }
    }

    public String getAnalyzableFileName() {
        return analyzableFileName;
    }
}
