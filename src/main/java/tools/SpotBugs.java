/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import models.Project;
import net.lingala.zip4j.exception.ZipException;
//import utils.ToolUtils;

/**
 * Class for SpotBugs analysis tool , implements Tool
 * @see Tool
 * @author mefi
 */
public class SpotBugs implements Tool{
    private static final String RUNWIN ="..\\src\\main\\java\\tools\\toolsfolder\\spotbugs\\lib\\spotbugs.jar"; 
    private static final String RUNLIN ="..\\src\\main\\java\\tools\\toolsfolder\\spotbugs\\lib\\spotbugs.jar"; 
    private final String outputPath;
    private String defaultParam;
    private int exitCode;
    /**
     * Constructor to set output path and default parameters
     * @param p Project to analyze
     * @see Project
     * @author mefi
     */
    public SpotBugs(Project p) {
        this.outputPath = p.getPath()+"\\spotbugs.xml";
        // -textui -xml -output <path_file_risultati> "<path_progetto_da_analizzare>"
        this.defaultParam="-textui -xml -output "+outputPath+" \""+p.getPath()+"\\\"";
    }

    public String getDefaultParam() {
        return defaultParam;
    }

    public String getOutputPath() {
        return outputPath;
    }
    
    public void setDefaultParam(String defaultParam) {
        this.defaultParam = defaultParam;
    }

    public int getExitCode() {
        return exitCode;
    }
    
    /**
     * Method to run SpotBugs in Windows o.s.
     * @author mefi
     */
    @Override
    public void runWin() {
            System.out.println("Partito SpotBugs");
            String[] tmp = {"-jar",RUNWIN};
            tmp = Stream.concat(Arrays.stream(tmp), Arrays.stream(defaultParam.split(" "))).toArray(String[]::new);
            this.exitCode = Tool.run("java",tmp);
    }
    /**
     * Method to run SpotBugs in Linux o.s.
     * @author mefi
     */
    @Override
    public void runLin() {
            System.out.println("SpotBugs Lunux.");
            String[] tmp ={"-jar",RUNLIN};
            tmp = Stream.concat(Arrays.stream(tmp), Arrays.stream(defaultParam.split(" "))).toArray(String[]::new);
            this.exitCode=Tool.run("java",tmp);
    }
    
    
}
