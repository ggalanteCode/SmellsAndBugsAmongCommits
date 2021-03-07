package tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Project;
import net.lingala.zip4j.exception.ZipException;
import utils.ToolUtils;

/**
 * Class for SourceMeter analysis tool
 * @author Francesco Florio
 */
public class SourceMeter implements Tool {

    private static final String RUNWIN ="/tools/toolsfolder/sourcemeter.zip"; 
    private static final String RUNLIN ="/tools/toolsfolder/sourcemeter.zip";
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
        this.defaultParam="-projectName="+p.getName()+" -resultsDir="+p.getPath()+File.separator+"sourcemeter"+" -projectBaseDir="+p.getPath()+" -runFB=false -runFaultHunter=false -runVulnerabilityHunter=false -runRTEHunter=false -runAndroidHunter=false";
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
     * Extract and unzip Source Meter executable file with his dependecies and run analysis for Windows o.s.
     * At the end of the process all extracted files are remove
     */
    @Override
    public void runWin() {
        try{
            System.out.println("Partito SourceMeter");
            String pathToZip = ToolUtils.extract(RUNWIN);
            ToolUtils.unzip(pathToZip);
            String path=pathToZip.replace("sourcemeter.zip","sourcemeter"+File.separator+"SourceMeterJava.exe");
            this.exitCode=Tool.run(path ,defaultParam.split(" "));
            Files.walk(Paths.get(path.replace(File.separator+"SourceMeterJava.exe","")))
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
     * Extract and unzip Source Meter executable file with his dependecies and run analysis for Linux o.s.
     * At the end of the process all extracted files are remove
     */
    @Override
    public void runLin() {
        try {
            System.out.println("SourceMeter Linux.");
            String pathToZip = ToolUtils.extract(RUNLIN);
            ToolUtils.unzip(pathToZip);
            String path=pathToZip.replace("sourcemeter.zip","sourcemeter"+File.separator+"SourceMeterJava.exe");
            this.exitCode=Tool.run(path ,defaultParam.split(" "));
            
            Files.walk(Paths.get(path.replace(File.separator+"SourceMeterJava.exe","")))
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

