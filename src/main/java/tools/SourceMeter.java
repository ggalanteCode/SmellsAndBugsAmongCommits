package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import models.Project;
import net.lingala.zip4j.exception.ZipException;
//import utils.ToolUtils;

/**
 * Class for SourceMeter analysis tool
 * @author Francesco Florio
 */
public class SourceMeter implements Tool {

    private static final String RUNWIN ="..\\src\\main\\java\\tools\\toolsfolder\\sourcemeter\\sourcemeter\\SourceMeterJava.exe"; 
    private static final String RUNLIN ="..\\src\\main\\java\\tools\\toolsfolder\\sourcemeter\\sourcemeter\\SourceMeterJava.exe";
    private final String outputPath; //crea all'interno directory nome=timestamp analisi
    private String defaultParam;
    private int exitCode;
    private String projectName;
    
    /**
     * Create a SourceMeter object with the default launch parameter:
     * <code>-projectName=<i>nome_file_risultati</i> -resultsDir=<i>path_directory_risultati</i> -projectBaseDir=<i>path_progetto_da_analizzare</i> -runFb=false -runFaultHunter=false -runVulnerabilityHunter=false -runRTEHunter=false -runAndroidHunter=false</code>
     * @param p project under study
     */
    public SourceMeter(Project p) {
        //DEFAULT ARGUMENTS // -projectName=<nome_file_risultati> -resultsDir=<path_directory_risultati> -projectBaseDir=<path_progetto_da_analizzare> -runFb"false
        this.projectName=p.getName();
        this.outputPath = p.getPath()+File.separator+"sourcemeter"+File.separator+p.getName()+File.separator+"java"+File.separator;
        this.defaultParam="SourceMeterJava -projectName="+p.getName()+" -resultsDir="+p.getPath()+File.separator+"sourcemeter"+" -projectBaseDir="+p.getPath()+" -runFB=false -runFaultHunter=false -runVulnerabilityHunter=false -runRTEHunter=false -runAndroidHunter=false";
    }

    public void setDefaultParam(String defaultParam) {
        this.defaultParam = defaultParam;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getDefaultParam() {
        return defaultParam;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public String getProjectName() {
        return projectName;
    }
    
    /**
     * execute Source Meter .exe file with his dependecies and run analysis for Windows o.s.
     *
     */
    @Override
    public void runWin() {
            System.out.println("Partito SourceMeter");
            this.exitCode=Tool.run(RUNWIN ,defaultParam.split(" "));
    }
    
    /**
     * execute Source Meter .exe file with his dependecies and run analysis for Linux o.s.
     *
     */
    @Override
    public void runLin() {
            System.out.println("SourceMeter Linux.");
            this.exitCode=Tool.run(RUNLIN ,defaultParam.split(" "));
    }
    
    
    
    }

