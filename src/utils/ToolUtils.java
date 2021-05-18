package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Objects;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Utility class for extract files from .jar. 
 * Needed because is impossible to execute a file store in a jar archive, it must be extract first to grant correct execution
 * @author Francesco Florio
 */
public class ToolUtils {
    
    /**
     * Utility method that: 
     * <ul>
     * <li>locate the resource to extract in the .jar file</li>
     * <li>write a copy of the resource outside the .jar file (same directory)</li>
     * <li>return the path of the copy</li>
     * </ul>
     * @param file file to extract
     * @return absolutepath of the file
     * @throws URISyntaxException incorrect syntax
     * @throws IOException input/output exception
     */
    synchronized public static String extract(String file) throws URISyntaxException, IOException{
        OutputStream os;
        try (InputStream is = Objects.requireNonNull(ToolUtils.class.getResource(file)).openStream()) {
            File f = new File(file.substring(file.lastIndexOf("/")+1));
            f.deleteOnExit();
            os = new FileOutputStream(f);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            os.close();
            return f.getAbsolutePath();
        }
    }
    
    /**
     * Utility method for unzip file.
     * Needed because some tool are stored as .zip along with their dependencies in Sbac
     * @param file file to unzip
     * @throws IOException    
     * @throws ZipException   
     */
    synchronized public static void unzip(String file) throws IOException, ZipException{
        ZipFile f=new ZipFile(file);
        f.extractAll(file.replace(file.substring(file.lastIndexOf(File.separator)+1),""));
    }
}
