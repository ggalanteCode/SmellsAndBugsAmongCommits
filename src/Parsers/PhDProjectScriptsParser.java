package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PhDProjectScriptsParser {

    String smell;
    ArrayList<String> classes = new ArrayList<>();
    String messageChainsTempory;

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
            String[] paths = line.split("&");

            //Obtain class path in project's source.
            for (String p : paths) {
                classPath = "";
                beginWritePath = false;

                String[] path = (p.replace(File.separatorChar,'§')).split("§");

                for (String s : path) {
                    if (s.equals("src") || beginWritePath) {
                        beginWritePath = true;
                        classPath += s+".";
                    }
                }

                classPath = classPath.substring(0, classPath.length()-6);
                classPath = classPath.replace("src.","");
                classes.add(classPath);
            }

        } else {
            String lineNumber = "";
            String method = "";
            String variable = "";
            switch (smell){

                case "Switch Statement" :
                    String[] switchSmell = line.split("[()']+");
                    lineNumber = switchSmell[1].trim();
                    method = switchSmell[3].trim();

                    //Scrivere su DB//
                    break;

                case "Speculative Generality":
                    String[] speculativeSmell = line.split("[()']+");
                    lineNumber = speculativeSmell[1].trim();
                    if(line.startsWith("Speculative Generality unused parameter")) {
                        variable = speculativeSmell[3].trim();
                        method = speculativeSmell[5].trim();
                    }

                    //Scrivere su DB//
                    break;

                case "Middle Man":
                    String[] middleSmell = line.split("[()']+");
                    lineNumber = middleSmell[1].trim();

                    //Scrivere su DB//
                    break;

                case "Message Chains":
                    if(line.startsWith("Message Chains"))
                        messageChainsTempory = line;
                    String[] strings = line.split("[//(//)]+", 3);
                    String[] chainsSmell = strings[2].split("[\\[\\]]+");
                    lineNumber = strings[1].trim();
                    method = chainsSmell[1].trim();

                    //Scrivere su DB//
                    break;

                case "Data Clumps":
                    String[] clumpsSmell;
                    if(line.startsWith("Parameters in method")) {
                        clumpsSmell = line.split("Parameters in method | and | was found duplicated");
                        method = clumpsSmell[1].substring(clumpsSmell[1].substring(0, clumpsSmell[1].lastIndexOf(".")).lastIndexOf(".") + 1) + " & " + clumpsSmell[2].substring(clumpsSmell[2].substring(0, clumpsSmell[2].lastIndexOf(".")).lastIndexOf(".") + 1);

                        //Scrivere su DB//
                    } else if(line.startsWith("Fields")) {
                        clumpsSmell = line.split("Fields  | was found duplicated");
                        variable = clumpsSmell[1];

                        //Scrivere su DB//
                    }
                    break;
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
}



    /* Switch Statement (numeroriga) in method ‘nomemetodo’.
    public void prova() {
        String stringa = "";
        if(stringa.equals("Switch Statement Bad Smell was found in:")) {
            String classe = "";
            String prossimastringa = "";
            if(prossimastringa.startsWith("FILE")) {
                prossimastringa = prossimastringa.replace(File.separatorChar,'§');
                String[] directory = prossimastringa.split("§");
                classe = directory[directory.length-2];
                if(prossimastringa.startsWith("Switch Statement")) {
                    String[] switchSmell = prossimastringa.split("[()']+");
                    String lineNumber = switchSmell[1];
                    String method = switchSmell[3];
                    Fornisci dati a SBAC
                }
            }
        }
        */
