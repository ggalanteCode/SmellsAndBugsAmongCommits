package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PhDProjectScriptsParser {

    String smell = "Switch Statement Bad Smell was found in:";
    ArrayList<String> classes = new ArrayList<>();

    public void execute(BufferedReader reader) {

        try {

            String line;

            while (true) {

                line = reader.readLine();

                if (line.equals("Speculative Generality Bad Smell was found in:") || line.equals("Middle Man Bad Smell was found in:")
                        || line.equals("Message Chains Bad Smell was found in:") || line.equals("Data Clumps Bad Smell was found in:")) {

                    smell = line;

                } else if (!line.isBlank()){
                    analyzeAndWrite(line);
                }

                analyzeAndWrite(line);

                if (line == null) { break; }
                System.err.println(line.trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void analyzeAndWrite(String line) {

        String classPath;
        boolean beginWritePath;

        if (line.startsWith("FILE")){

            classes = new ArrayList<>();
            String[] paths = line.split("&");

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

            switch (smell){

                case "Switch Statement Bad Smell was found in:" :

                    String[] switchSmell = line.split("[()']+");
                    String lineNumber = switchSmell[1].trim();
                    String method = switchSmell[3].trim();

                    //Scrivere su DB//

                    break;

                case "Speculative Generality Bad Smell was found in:":
                    break;

                case "Middle Man Bad Smell was found in:":
                    break;

                case "Message Chains Bad Smell was found in:":
                    break;

                case "Data Clumps Bad Smell was found in:":
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
