package utils;

import com.google.common.primitives.Bytes;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command Line Interface wrapper. Provides utility methods for starting tools integrated in Sbac
 * @author Francesco Florio
 */

public class CliUtils {

    protected final String[] args;
    protected String command;
    protected final File directory;

    /**
     * Creates a CliUtils for run a command with his specific arguments.
     * @param command command to execute
     * @param args list of specific arguments of the command
     */
    public CliUtils(String command, String ... args ) {
        this(command,new File("."), args);
        if (command.equals("PhDSmells")) {
            this.command=command;
        }
    }
    
    /**
     * Creates a CliUtils for run a command with his specific arguments. The command will be executed from the chosen directory 
     * @param command command to execute
     * @param directory directory of execution
     * @param args list of specific arguments of the command 
     */
    public CliUtils(String command, File directory, String... args) {
        this.command = command;
        this.directory = directory;
        this.args = args;
    }
    
    /**
     * Make a new process of the o.s. to execute the command
     * @return Results with output of execution and exit code
     * @throws Exception incorrect commands
     * @throws IOException input/output exception
     */
    public Result execute() throws Exception,IOException {

        //The field COMMAND contains the name of the tool ONLY for PhDProjectScripts.
        if (command.equals("PhDSmells")){

            ProcessBuilder promptBuilder = new ProcessBuilder(args);
            promptBuilder.redirectErrorStream(true);

            Process prompt = promptBuilder.start();

            /*

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(prompt.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(prompt.getErrorStream()));

            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            */


            String output = output(prompt);

            int exitCode = prompt.waitFor();
            prompt.destroy();
            System.out.println(command + " = " + exitCode);
            return new Result(exitCode, output);


        } else {

            List<String> args = new ArrayList<>();
            args.add(command);
            String[] params = this.args;
            if (this.args != null) {
                args.addAll(Arrays.asList(params));
            }

            ProcessBuilder promptBuilder = new ProcessBuilder(args);
            promptBuilder.directory(directory);
            promptBuilder.redirectErrorStream(true);

            final Process process = promptBuilder.start();

            /*

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            */

            String output = output(process);


            int exitCode = process.waitFor();
            process.destroy();
            System.out.println(command + " = " + exitCode);
            return new Result(exitCode, output);


        }

    }

    /**
     * Read output of the process
     * @param process
     * @return output of the process
     * @throws IOException input/output exception
     */
    public String output(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int readBytes;
        byte[] buffer = new byte[2048];
        while ((readBytes = inputStream.read(buffer)) >= 0) {
        //    bytes.write(buffer, 0, readBytes);
        }
        // return new String(bytes.toByteArray());
        return "ggg";
    }


    /**
     * Read errors generated by the process
     * @param process
     * @return error of the process
     * @throws IOException input/output exception
     */
    public String error(Process process) throws IOException {
        InputStream inputStream = process.getErrorStream();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int readBytes;
        byte[] buffer = new byte[2048];
        while ((readBytes = inputStream.read(buffer)) >= 0) {
            bytes.write(buffer, 0, readBytes);
        }
        return new String(bytes.toByteArray());
    }

    public class Result {
        public final int code;
        public final String output;

        public Result(int code, String output) {
            this.code = code;
            this.output = output;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", command, String.join(" ", args));
    }

}