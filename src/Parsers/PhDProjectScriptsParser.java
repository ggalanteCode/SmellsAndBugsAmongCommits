package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PhDProjectScriptsParser {

    String smell;
    ArrayList<String> classes = new ArrayList<>();

    public void execute(BufferedReader reader) {

        try {

            String line;

            boolean continueReading = true;

            do {

                line = reader.readLine().trim();
                System.err.println(line);

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

            switch (smell) {

                case "Switch Statement":

                    String[] switchSmell = line.split("[()']+");
                    String lineNumber = switchSmell[1].trim();
                    String method = switchSmell[3].trim();

                    //Scrivere su DB//

                    break;

                case "Speculative Generality":
                    break;

                case "Middle Man":
                    break;

                case "Message Chains":
                    break;

                case "Data Clumps":
                    break;
            }
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
