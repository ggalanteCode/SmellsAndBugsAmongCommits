package Parsers;

import database.DbHandler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.MetricHunter;
import models.Class;
import models.Method;

/**
 * Class used to parse MetricHunter output file
 * @author Francesco Florio
 */
public class MetricHunterReader implements Reader{

    private MetricHunter mh;
    private Class c;
    private Method m;
    private String commitId;
    private String projectUrl;

    /**
     * Costructor
     * @param commitId analyzed commit hash
     * @param projectUrl analyzed project url
     */
    public MetricHunterReader(String commitId, String projectUrl) {
        this.commitId = commitId;
        this.projectUrl=projectUrl;
    }
    
    /**
     * This method is used to process a line of the txt file and parse the data into metricHunter objects
     * @param row line to read
     */ 
    @Override
    public void read(String row) {
        if(row.contains("warning_Class")){
            String[] MHInfo=row.split("\\(");
            String[] MHInfo2=MHInfo[1].split("\\)");
            // System.out.println("WARNING CLASS: " + MHInfo2[1]);
            String codice=(MHInfo2[1].substring(1)).split("_")[0];
            String valore=MHInfo[2].split("'")[3];
            String classPath =MHInfo[0];
            classPath = classPath.substring(0, classPath.lastIndexOf(".java"));
            String className =Paths.get(classPath).getName(Paths.get(classPath).getNameCount()-1).toString();
            classPath = classPath.replace(File.separator, ".");
            String[] classPathSplit = classPath.split(".src.");
            classPath = classPathSplit[classPathSplit.length-1];

            try {
                int exists = DbHandler.classInProject(className, classPath);
                c= new Class(className, classPath);
                if(exists==0) {
                    DbHandler.insertClass(c);
                }else{
                    String path=DbHandler.getClassPath(exists);
                    if(path==null){
                        DbHandler.updateClassPath(classPath,exists);
                        c.setId(exists);
                    }
                    else{
                        if(!DbHandler.getClassPath(exists).equals(classPath)) {
                            DbHandler.insertClass(c);
                        }else
                            c.setId(exists);
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
                mh= new MetricHunter(Integer.parseInt(MHInfo2[0]), Double.parseDouble(valore), codice);
                DbHandler.insertMetricHunter(mh,commitId,(int)c.getId(),0);
            } catch (SQLException ex) {
                Logger.getLogger(MetricHunterReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            if(row.contains("warning_Method")){
                String[] MHInfo=row.split("\\(");
                String[] MHInfo2=MHInfo[1].split("\\)");
                String[] MHInfo3=row.split("'");
                // System.out.println("WARNING METHOD: " + MHInfo[0]);
                String classPath =MHInfo[0];
                classPath = classPath.substring(0, classPath.lastIndexOf(".java"));
                String className =Paths.get(classPath).getName(Paths.get(classPath).getNameCount()-1).toString();
                classPath = classPath.replace(File.separator, ".");
                String[] classPathSplit = classPath.split(".src.");
                classPath = classPathSplit[classPathSplit.length-1];
                try {
                    int existsClass = DbHandler.classInProject(className,classPath);
                    c= new Class(className,classPath);
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
                            }else
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

                    String methodSignature = MHInfo3[1];
                    int i=methodSignature.indexOf(" ");
                    if(i==-1) i=0;
                    
                    String methodName=methodSignature.split("\\(")[0].substring(methodSignature.split("\\(")[0].indexOf(" ")+1);
                    m= new Method();
                    m.setName(methodName);
                    m.setSignature(methodSignature);
                    int existsMethod = DbHandler.MethodInClass(m.getName(), (int)c.getId());
                    if(existsMethod==0)
                        DbHandler.insertMethod(m,(int)c.getId());
                    else
                        m.setId(existsMethod);
                    
                    int line=Integer.parseInt(MHInfo2[0]);
                    String codice=(MHInfo2[1].substring(1)).split("_")[0];
                    double valore=Double.parseDouble(MHInfo3[3]);
                    mh= new MetricHunter(line, valore, codice);
                    DbHandler.insertMetricHunter(mh,commitId,0,(int)m.getId());
                    
                } catch (SQLException ex) {
                    Logger.getLogger(MetricHunterReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }

    }
}

