package Parsers;

import database.DbHandler;
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
        Path p= Paths.get(PMDInfo[0]);
        try {
            c= new Class(p.getName(p.getNameCount()-1).toString(),PMDInfo[0]);
            int existsClass = DbHandler.classInProject(p.getName(p.getNameCount()-1).toString(),this.projectUrl);
            if(existsClass==0)
                    DbHandler.insertClass(c);
            else{
                String path=DbHandler.getClassPath(existsClass);
                if(path==null){
                    DbHandler.updateClassPath(PMDInfo[0],existsClass);
                    c.setId(existsClass);
                }
                else{
                    if(!DbHandler.getClassPath(existsClass).equals(PMDInfo[0]))
                        DbHandler.insertClass(c);
                    else
                        c.setId(existsClass);
                }
            }
            pmd= new Pmd(Integer.parseInt(PMDInfo2[0]), soluzione, codice);
            DbHandler.insertPMD(pmd, commitId, (int)c.getId());
        } catch (SQLException ex) {
            Logger.getLogger(PMDReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
