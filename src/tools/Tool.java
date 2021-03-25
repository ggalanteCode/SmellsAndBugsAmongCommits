package tools;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.CliUtils;

/**
 * Analysis tool interface. Implementation of the Strategy pattern
 */
public interface Tool  {
    
    /**
     * Run tool on Windows o.s.
     */
    public abstract void runWin();
    
    /**
     * Run tool on Linux o.s.
     */
    public abstract void runLin();
    
    /**
     * Get the exit code value of the analysis. Value 0 means that the analysis completed successfully
     * @return exitCode
     */
    public abstract int getExitCode();
    
    /**
     * start analysis process through CliUtils
     * @param Command command to run
     * @param args arguments
     * @return exitCode
     */
    public static int run(String Command, String ... args){
        //System.out.println(Thread.currentThread().getName()+" è entrato nel medoto run di Tool");

        CliUtils c = new CliUtils(Command ,args);
        CliUtils.Result result;
        try {
            result = c.execute();
            return result.code; // exit code
            
        } catch (Exception ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }  
}
