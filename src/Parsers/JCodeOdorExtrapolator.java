package Parsers;
import database.DbHandler;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class used to parse JCodeOdor output file
 * @author Francesco Florio
 */
public class JCodeOdorExtrapolator implements Extrapolator {
    private models.Package p;
    private models.Class c ;
    private Method m ;
    private Metric mm ;
    private Smell s;
    private String commitId;
    private String projectUrl;
    private double value;

    /**
     * Costructor
     * @param commitId analyzed commit hash
     * @param projectUrl analyzed project url
     */
    public JCodeOdorExtrapolator(String commitId, String projectUrl) {
        this.commitId = commitId;
        this.projectUrl=projectUrl;
    }
    
    /**
     * This method is used to visit a node in the xml tree and parse the data into smell and metric objects
     * @param n node to visit
     * @param attributes node attributes
     */
    @Override
    public void extrapolate(Node n, NamedNodeMap attributes) {
        String nodeName = n.getNodeName().toUpperCase();
        
        try{
            switch(nodeName){
                case "NAME": 
                    if(n.getParentNode().getNodeName().equals("package")){
                        //System.out.println("   Package "+n.getTextContent().toUpperCase() );
                        int exists = DbHandler.packageExist(n.getTextContent());
                        p=new models.Package(n.getTextContent());
                        if(exists==0)
                            DbHandler.insertPackage(p);
                        else
                            p.setId(exists);
                    }
                    else if(n.getParentNode().getNodeName().equals("types")){
                        //System.out.println("      Classe "+n.getTextContent().toUpperCase() );
                        int exists = DbHandler.classInProject(n.getTextContent()+".java",this.projectUrl);
                        c=new models.Class(n.getTextContent()+".java");
                        if(exists==0){
                            DbHandler.insertClass(c);
                            DbHandler.insertClassPac((int)p.getId(), (int)c.getId());
                        }
                        else
                            c.setId(exists);
                    }
                    else {
                        //System.out.println("         Metodo "+n.getTextContent().toUpperCase() );
                        m= new Method();
                        m.setName(n.getTextContent());
                    }
                    break;
                case "SIGNATURE": 
                    int exists = DbHandler.MethodInClass(m.getName(), (int)c.getId());
                    if(exists==0){
                        m.setSignature(n.getTextContent());
                        DbHandler.insertMethod(m,(int)c.getId());}
                    else
                        m.setId(exists);
                    break;
                case "VALUE":
                    value = Double.parseDouble(n.getTextContent());
                    break;
                case "KEY": 
                    if(n.getParentNode().getNodeName().equals("metrics")){
                        //System.out.println("             Metric "+n.getTextContent().toUpperCase()+":"+value );
                        mm =new Metric(n.getTextContent(),value);
                        if(n.getParentNode().getParentNode().getParentNode().getNodeName().equals("package"))
                            DbHandler.insertMetric(mm, commitId,0,0,(int)p.getId() );
                        else if(n.getParentNode().getParentNode().getParentNode().getNodeName().equals("types"))
                            DbHandler.insertMetric(mm, commitId,0,(int)c.getId() ,0);
                        else 
                            DbHandler.insertMetric(mm, commitId,(int)m.getId(),0,0);
                    }
                    else {
                        //System.out.println("             Smell "+n.getTextContent().toUpperCase()+":"+value );
                        s = new Smell(n.getTextContent(),value);
                        if(n.getParentNode().getParentNode().getParentNode().getNodeName().equals("package"))
                            DbHandler.insertSmell(s, commitId,0,0,0,(int)p.getId(), 0);
                        else if(n.getParentNode().getParentNode().getParentNode().getNodeName().equals("types"))
                            DbHandler.insertSmell(s, commitId,0,0,(int)c.getId(),0, 0);
                        else 
                            DbHandler.insertSmell(s, commitId,0,(int)m.getId(),0,0, 0);
                    }

                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JCodeOdorExtrapolator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void finalOperations() {
        
    }
}
