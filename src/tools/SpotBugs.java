/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import models.Project;
import net.lingala.zip4j.exception.ZipException;
import utils.ToolUtils;

/**
 * Class for SpotBugs analysis tool , implements Tool
 * @see Tool
 * @author mefi
 */
public class SpotBugs implements Tool{
    private static final String RUNWIN ="/tools/toolsfolder/spotbugs.zip"; 
    private static final String RUNLIN ="/tools/toolsfolder/spotbugs.zip"; 
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
        try{
            System.out.println("Partito SpotBugs");
            String pathToZip = ToolUtils.extract(RUNWIN);
            ToolUtils.unzip(pathToZip);
            String path = pathToZip.replace("spotbugs.zip","spotbugs"+File.separator+"lib"+File.separator+"spotbugs.jar");
            String[] tmp = {"-jar",path};
            tmp = Stream.concat(Arrays.stream(tmp), Arrays.stream(defaultParam.split(" "))).toArray(String[]::new);
            this.exitCode = Tool.run("java",tmp);
            Files.walk(Paths.get(path.replace(File.separator+"lib"+File.separator+"spotbugs.jar","")))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
        }catch (IOException ex) {
            Logger.getLogger(SourceMeter.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (ZipException ex) {
            Logger.getLogger(SourceMeter.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SourceMeter.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    /**
     * Method to run SpotBugs in Linux o.s.
     * @author mefi
     */
    @Override
    public void runLin() {
        try {
            System.out.println("SpotBugs Lunux.");
            String pathToZip = ToolUtils.extract(RUNLIN);
            ToolUtils.unzip(pathToZip);
            
            String path=pathToZip.replace("spotbugs.zip","spotbugs"+File.separator+"lib"+File.separator+"spotbugs.jar");
            String[] tmp ={"-jar",path};
            tmp = Stream.concat(Arrays.stream(tmp), Arrays.stream(defaultParam.split(" "))).toArray(String[]::new);
            this.exitCode=Tool.run("java",tmp);
            
            
            Files.walk(Paths.get(path.replace(File.separator+"lib"+File.separator+"spotbugs.jar","")))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
            
        } catch (IOException ex) {
            Logger.getLogger(SourceMeter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ZipException ex) {
            Logger.getLogger(SourceMeter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SourceMeter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
