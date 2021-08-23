package Parsers;

import database.DbHandler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Clone;
import models.CloneIstance;
import models.Class;

/**
 *  Class used to parse Clones output file
 * @author Francesco Florio
 */
public class ClonesReader implements Reader {
    private Clone c;
    private CloneIstance ci;
    private Class cl;
    private String commitId;
    private String projectUrl;

    /**
     * Costructor
     * @param commitId analyzed commit hash
     * @param projectUrl analyzed project url
     */
    public ClonesReader(String commitId, String projectUrl) {
        this.commitId = commitId;
        this.projectUrl=projectUrl;
    }
    
    /**
     * This method is used to process a line of the txt file and parse the data into clone and cloneistance objects
     * @param row line
     */ 
    @Override
    public void read(String row) {
        String[] cloneInfo=null;
        try{
            if(!row.startsWith("Number")){
                if(row.substring(0, 1).matches("[0-9]")){
                    cloneInfo=(row.substring(0, row.length()-1).split("\\["))[1].split(",");
                        c= new Clone(Integer.parseInt(cloneInfo[0].split(":")[1].trim()),Integer.parseInt(cloneInfo[1].split(":")[1].trim()),"????");
                        DbHandler.insertClone(c, commitId);
                }
                else{
                    cloneInfo=row.split("\\(");

                    String classPath =cloneInfo[0];
                    classPath = classPath.substring(0, classPath.lastIndexOf(".java"));
                    String className =Paths.get(classPath).getName(Paths.get(classPath).getNameCount()-1).toString();
                    classPath = classPath.replace(File.separator, ".");
                    String[] classPathSplit = classPath.split(".src.");
                    classPath = classPathSplit[classPathSplit.length-1];

                    int exists = DbHandler.classInProject(className, classPath);
                    cl= new Class(className, classPath);
                    if(exists==0) {
                        DbHandler.insertClass(cl);
                    } else{
                        String path=DbHandler.getClassPath(exists);
                        if(path==null){
                            DbHandler.updateClassPath(classPath,exists);
                            cl.setId(exists);
                        }
                        else{
                            if(!DbHandler.getClassPath(exists).equals(classPath)) {
                                DbHandler.insertClass(cl);
                            } else
                                cl.setId(exists);
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
                    String[] tmp=(cloneInfo[1].substring(0, cloneInfo[1].length()-1)).split("\\[")[1].split("-");
                    String[] tmp2=tmp[0].trim().split(",");
                    String[] tmp3=tmp[1].trim().split(",");
                    ci= new CloneIstance(Integer.parseInt(tmp2[0].trim().split(":")[1]),Integer.parseInt(tmp2[1].trim().split(":")[1]),Integer.parseInt(tmp3[0].trim().split(":")[1]),Integer.parseInt(tmp3[1].trim().split(":")[1]));
                    DbHandler.insertCloneIstance(ci, (int)c.getId(), (int)cl.getId());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClonesReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
