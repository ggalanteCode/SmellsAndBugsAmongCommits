package tools;

import models.Class;
import models.Commit;
import models.Method;
import models.Project;

import java.io.File;

/**
 * class for method-level source code histories tool (CodeShovel)
 * @author Giovanni Galante
 */
public class CodeShovel implements Tool {

    private static final String RUNWIN = "src/tools/toolsfolder/codeshovel";
    private static final String RUNLIN = "src/tools/toolsfolder/codeshovel";
    private final String toolName = "codeshovel-1.1.jar";
    private int exitcode;

    public String getDefaultParam() {
        return defaultParam;
    }

    private String defaultParam;

    public void setDefaultParam(String defaultParam) {
        this.defaultParam = defaultParam;
    }

    public String getToolPath() {
        return toolPath;
    }

    private String toolPath;

    public String getProjectPath() {
        return projectPath;
    }

    private String projectPath;

    public String getStartCommit() {
        return startCommit;
    }

    public String getProjectName() {
        return projectName;
    }

    private String projectName;

    public void setStartCommit(String startCommit) {
        this.startCommit = startCommit;
    }

    private String startCommit = "";

    public String getTargetFilePath() {
        return targetFilePath;
    }

    private String targetFilePath = "";

    public String getMethodName() {
        return methodName;
    }

    private String methodName = "";

    private int methodStartLine;

    public String getOutputFilePath() {
        return outputFilePath;
    }

    private String outputFilePath;

    public CodeShovel(Project project) {
        this.projectPath = System.getProperty("user.home")+ File.separator + project.getName();
        this.toolPath = new File(RUNWIN).getAbsolutePath();
        this.defaultParam = toolPath + " && java -jar " + toolName + " " + projectPath + " " + "HEAD";
    }


    /**
     * run for windows
     */
    @Override
    public void runWin() {
        System.out.println("CodeShovel started");
        String[] param = {"cmd.exe", "/c", "cd " + defaultParam};
        this.exitcode = Tool.run("java", param);
    }

    /**
     * run for linux
     */
    @Override
    public void runLin() {
        System.out.println("CodeShovel started");
        String[] param = {"/bin/bash", "-c", "cd " + defaultParam};
        this.exitcode = Tool.run("java", param);
    }

    /**
     * returns the exit code
     * @return
     */
    @Override
    public int getExitCode() {
        return exitcode;
    }
}
