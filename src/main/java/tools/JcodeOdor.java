
package tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import models.Project;
//import utils.ToolUtils;

/**
 * Class for JCodeOdor analysis tool
 * @author Francesco Florio
 */
public class JcodeOdor implements Tool{
    private static final String RUNWIN ="..\\src\\main\\java\\tools\\toolsfolder\\jcodeodor\\JCodeOdor-Launcher-1.0-standalone.jar"; 
    private static final String RUNLIN ="..\\src\\main\\java\\tools\\toolsfolder\\jcodeodor\\JCodeOdor-Launcher-1.0-standalone.jar";
    private final String outputPath;
    private String defaultParam;
    private int exitCode;

    /**
     * Create a JCodeOdor object with the default launch parameter:
     * <code>-source <i>path_progetto_da_analizzare</i> -output-type XML -output <i>path_file_risultati</i></code>
     * @param p project under study
     */
    public JcodeOdor(Project p) {
        //DEFAULT ARGUMENTS -source <path_progetto_da_analizzare> -output-type XML -output <path_file_risultati>
        this.outputPath = p.getPath()+File.separator+"jcodeodor.xml";
        this.defaultParam="-source "+p.getPath()+" -output-type XML -output "+outputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getDefaultParam() {
        return defaultParam;
    }

    public void setDefaultParam(String defaultParam) {
        this.defaultParam = defaultParam;
    }
    
    @Override
    public int getExitCode() {
        return exitCode;
    }
    
    /**
     * Extract JCodeOdor executable file and run analysis for Windows o.s 
     * At the end of the process all extracted files are remove
     */
    @Override
    public void runWin() {
            System.out.println("Partito JcodeOdor");
            String[] tmp = {"-jar",RUNWIN};
            tmp = Stream.concat(Arrays.stream(tmp), Arrays.stream(defaultParam.split(" "))).toArray(String[]::new);
            this.exitCode = Tool.run("java",tmp);
    }

    /**
     * Extract JCodeOdor executable file and run analysis for Linux o.s 
     * At the end of the process all extracted files are remove
     */
    @Override
    public void runLin() {
        String[] tmp = {"-jar",RUNLIN};
        tmp = Stream.concat(Arrays.stream(tmp), Arrays.stream(defaultParam.split(" "))).toArray(String[]::new);
        this.exitCode = Tool.run("java",tmp);
    }
    
    
    
       
    }

