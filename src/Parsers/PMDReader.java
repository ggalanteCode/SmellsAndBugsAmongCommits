package Parsers;

import database.DbHandler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Pmd;
import models.Class;


/**
 * Class used to parse PMD output file
 * @author Francesco Florio
 */
public class PMDReader implements Reader{

    private Pmd pmd;
    private Class c;
    private String commitId;
    private String projectUrl;

    /**
     * Costructor
     * @param commitId analyzed commit hash
     * @param projectUrl analyzed project url
     */
    public PMDReader(String commitId, String projectUrl) {
       this.commitId= commitId;
       this.projectUrl=projectUrl;
    }
    
    /**
     * This method is used to process a line of the txt file and parse the data into PMD objects
     * @param row line to read
     */ 
    @Override
    public void read(String row) {
        String[] PMDInfo=row.split("\\(");
        String[] PMDInfo2=PMDInfo[1].split("\\)");
        String codice=(PMDInfo2[1].substring(5)).split(":")[0];
        String soluzione=row.split(":")[3].trim();
        // System.out.println("PMD: " + PMDInfo[0]);
        String classPath =PMDInfo[0];
        classPath = classPath.substring(0, classPath.lastIndexOf(".java"));
        String className =Paths.get(classPath).getName(Paths.get(classPath).getNameCount()-1).toString();
        classPath = classPath.replace(File.separator, ".");
        String[] classPathSplit = classPath.split(".src.");
        classPath = classPathSplit[classPathSplit.length-1];
        try {
            c= new Class(className, classPath);
            int existsClass = DbHandler.classInProject(className, classPath);
            if(existsClass==0) {
                DbHandler.insertClass(c);
            }else{
                String path=DbHandler.getClassPath(existsClass);
                if(path==null){
                    DbHandler.updateClassPath(classPath,existsClass);
                    c.setId(existsClass);
                }
                else{
                    if(!DbHandler.getClassPath(existsClass).equals(classPath)) {
                        DbHandler.insertClass(c);
                    } else
                        c.setId(existsClass);
                }
            }
            String packagePath = classPath.substring(0, classPath.lastIndexOf("."));
            String packageName;
            if(packagePath.lastIndexOf(".") != -1)
                packageName = packagePath.substring(packagePath.lastIndexOf(".") + 1);
            else
                packageName = packagePath;
            models.Package pac = new models.Package(packageName);
            pac.setPath(packagePath);
            int existsPackage = DbHandler.packageExist(packageName);
            if(existsPackage==0) {
                DbHandler.insertPackage(pac);
            }
            pmd= new Pmd(Integer.parseInt(PMDInfo2[0]), soluzione, codice);
            DbHandler.insertPMD(pmd, commitId, (int)c.getId());
        } catch (SQLException ex) {
            Logger.getLogger(PMDReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
