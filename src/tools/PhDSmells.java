
package tools;

import models.Project;
import org.aopalliance.reflect.ProgramUnit;
import utils.CliUtils;
import utils.ToolUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Class for PhDSmells analysis tool.
 * @author Federico Caspani
 */
public class PhDSmells implements Tool {
    private static final String RUNWIN ="src/tools/toolsfolder/PhDProjectScripts";
    private static final String RUNLIN ="src/tools/toolsfolder/PhDProjectScripts";
    private final String toolPath;
    private final String projectPath;
    private String defaultParam;
    private final String toolName = "PhDProjectScripts.jar";
    private int exitCode;

    /**
     * Create a phDSmells object with the default launch parameter:
     * @param p project under study.
     */
    public PhDSmells(Project p) {
        this.projectPath = p.getPath();
        this.toolPath = new File(RUNWIN).getAbsolutePath();
        this.defaultParam = toolPath + " && java -jar " + toolName + " " + projectPath +" -t";
    }

    public String getToolPath() {
        return toolPath;
    }

    public String getToolName() {
        return toolName;
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
     * Run analysis for Windows o.s.
     * At the end of the process all extracted files are removed.
     */
    @Override
    public void runWin() {
        System.out.println("Partito PhDSmells");
        String[] param = {"cmd.exe", "/c", "cd " + defaultParam};
        this.exitCode = Tool.run("PhDSmells", param);
    }

    /**
     * Extract JCodeOdor executable file and run analysis for Linux o.s 
     * At the end of the process all extracted files are removed.
     * NOT TESTED YET!
     */
    @Override
    public void runLin() {
        System.out.println("Partito PhDSmells");
        String[] param = {"cmd.exe", "/c", "cd " + defaultParam};//Questo dev'essere variato.
        this.exitCode = Tool.run("PhDSmells", param);
        
    }

}

