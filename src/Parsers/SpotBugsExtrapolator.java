/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;
import database.DbHandler;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.*;
import models.Class;
import models.Package;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class used to parse data given as output from SpotBugs analyze, this class implement Extrapolator 
 * @see Extrapolator
 * @author mattia
 */
public class SpotBugsExtrapolator implements Extrapolator{
    private ArrayList<BugIstance> toInsBugIstance;
    private ArrayList<models.Error> toInsErrors;
    private BugCollection bc;
    private BugIstance bi ;
    private Method m ;
    private Variable v ;
    private models.Error e;
    private String commit;
    
    public SpotBugsExtrapolator(String commit) {
        this.commit=commit;
        toInsBugIstance = new ArrayList<>();
        toInsErrors = new ArrayList<>();
    }
    /**
    * This method is used to visit a node in the xml tree and parse the data into objects
    * @see Extrapolator
    * @param n node to visit
    * @param attributes attributes of the node
    * @author mattia
    */
    @Override
    public void extrapolate(Node n, NamedNodeMap attributes) {
        String nodeName = n.getNodeName().toUpperCase();
        if(XMLparser.DEBUG && attributes!= null)
            printAtt(attributes);
        
        switch(nodeName){
            case "BUGCOLLECTION": 
                bugcollection(attributes);
                break;
            case "BUGINSTANCE": 
                bugistance(attributes);
                break;
            case "METHOD": 
                method(attributes);
                break;
            case "SOURCELINE":
                sourceline(attributes);
                break;
            case "FIELD": 
                field(attributes);
                break;
            case "ERROR": 
                e = new models.Error();
                break;
            case "ERRORMESSAGE": 
                if(XMLparser.DEBUG)
                    System.out.println("   "+n.getTextContent());
                if(e!=null)
                    e.setErrormsg(n.getTextContent());
                break;
            case "EXCEPTION": 
                if(XMLparser.DEBUG)
                    System.out.println("   "+n.getTextContent());
                if(e!=null)
                    e.setExceptionmsg(n.getTextContent());
                if(e!=null){
                    toInsErrors.add(e);
                    System.out.println("\u001B[31m"+e+"\u001B[0m"); //ins Error
                }
                break;    
            case "MISSINGCLASS": 
                if(XMLparser.DEBUG)
                    System.out.println("   "+n.getTextContent());
                if(bc!=null)
                    bc.addMissingClass(n.getTextContent());
                break;
            case "FINDBUGSSUMMARY": 
                fbsummary(attributes);
                break;   
            //case "LOCALVARIABLE" o "STRING"  è un sottoinsieme di field non utile perciò tralasciati
                
        }
    }
    /**
    * This method is used to do all the final operations of the extrapolator
    * @see Extrapolator
    * @author mattia
    */
    @Override
    public void finalOperations() {
        //System.out.println("\u001B[31m"+"       HERE FINAL OPERATIONS       "+"\u001B[0m"); 
        try {
            int bcid = -1;
            if(bc!=null)
                bcid = DbHandler.insertBugCollection(bc,commit);
            if(!toInsBugIstance.isEmpty() && bcid != -1)
            for(BugIstance bugistance : toInsBugIstance)
                DbHandler.insertBugIstance(bugistance,bcid);
            if(!toInsErrors.isEmpty() && bcid != -1)
            for(models.Error error : toInsErrors)
                DbHandler.insertError(error,bcid);
        } catch (SQLException ex) {
            Logger.getLogger(SpotBugsExtrapolator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void method(NamedNodeMap attributes){
        m = new Method();
        if(attributes != null && bi.getM() == null){
            int l = attributes.getLength();
            for(int j=0 ; j<l ; j++){
                String name = attributes.item(j).getNodeName();
                switch(name){
                    case "isStatic": 
                        m.setIsStatic(attributes.item(j).getNodeValue().equals("true"));
                        break;  
                    case "name": 
                        m.setName(attributes.item(j).getNodeValue());
                        break; 
                    case "signature":
                        m.setSignature(attributes.item(j).getNodeValue());
                        break; 
                }    
            }
            bi.setM(m);
        }
    }
    
    private void field(NamedNodeMap attributes){
        v = new Variable();
        if(attributes != null && bi.getV()== null){
            int l = attributes.getLength();
            for(int j=0 ; j<l ; j++){
                String name = attributes.item(j).getNodeName();
                switch(name){
                    case "isStatic": 
                        v.setIsStatic(attributes.item(j).getNodeValue().equals("true"));
                        break;  
                    case "name": 
                        v.setName(attributes.item(j).getNodeValue());
                        break; 
                    case "signature":
                        v.setRole(attributes.item(j).getNodeValue());
                        break; 
                }    
            }
            bi.setV(v);
        }
    }
    
    private void bugistance(NamedNodeMap attributes){
        if(bi!=null){
            toInsBugIstance.add(bi);
            System.out.println("\u001B[31m"+bi+"\u001B[0m");}
        bi = new BugIstance();
        if(attributes != null){
            int l = attributes.getLength();
            for(int j=0 ; j<l ; j++){
                String name = attributes.item(j).getNodeName();
                switch(name){
                    case "abbrev": 
                        bi.setAbbrev(attributes.item(j).getNodeValue());
                        break;  
                    case "category": 
                        bi.setCategory(attributes.item(j).getNodeValue());
                        break; 
                    case "priority": 
                        bi.setPriority(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break; 
                    case "rank": 
                        bi.setRank(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break; 
                    case "type": 
                        bi.setType(attributes.item(j).getNodeValue());
                        break; 
                }    
            }
        }
    }
    
    private void fbsummary(NamedNodeMap attributes){
        if(bc==null)
            bc = new BugCollection();
        if(attributes != null){
            int l = attributes.getLength();
            for(int j=0 ; j<l ; j++){
                String name = attributes.item(j).getNodeName();
                switch(name){
                    case "total_classes": 
                        bc.setTotalclasses(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break;
                    case "referenced_classes": 
                        bc.setReferencedclasses(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break;
                    case "total_bugs": 
                        bc.setTotalbugs(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break;    
                }    
            }
        }
    }
    private void bugcollection(NamedNodeMap attributes){
        if(bc==null)
            bc = new BugCollection();
        if(attributes != null){
            int l = attributes.getLength();
            for(int j=0 ; j<l ; j++){
                String name = attributes.item(j).getNodeName();
                switch(name){
                    case "version": 
                        bc.setVersion(attributes.item(j).getNodeValue());
                        break;
                    case "timestamp": 
                        bc.setTime(new Date(Long.valueOf(attributes.item(j).getNodeValue())));
                        break;
                }    
            }
        }
    }
    
    private void sourceline(NamedNodeMap attributes){
        if(attributes != null && bi != null){
            int l = attributes.getLength();
            for(int j=0 ; j<l ; j++){
                String name = attributes.item(j).getNodeName();
                switch(name){
                    case "classname":
                        String fullclassname = attributes.item(j).getNodeValue();
                        String packagePath, className, packageName;
                        if(fullclassname.lastIndexOf(".") != -1) {
                            int indexlastpointclass = fullclassname.lastIndexOf(".");
                            className = fullclassname.substring(indexlastpointclass + 1);
                            packagePath = fullclassname.substring(0, indexlastpointclass);
                            if(packagePath.lastIndexOf(".") != -1) {
                                int indexlastpointpackage = packagePath.lastIndexOf(".");
                                packageName = packagePath.substring(indexlastpointpackage + 1);
                            } else {
                                packageName = packagePath;
                            }
                        } else {
                            className = fullclassname;
                            packagePath = className;
                            packageName = packagePath;
                        }
                        Class cl = new Class(className, fullclassname);
                        Package pac = new Package(packageName);
                        pac.setPath(packagePath);
                        bi.setClassname(fullclassname);
                        bi.setCl(cl);
                        bi.setPac(pac);
                        break;
                    case "end": 
                        bi.setEndsourceline(Integer.parseInt(attributes.item(j).getNodeValue()));   
                        break;
                    case "start": 
                        bi.setStartsourceline(Integer.parseInt(attributes.item(j).getNodeValue())); 
                        break;
                    case "endBytecode": 
                        bi.setEndsbytecode(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break;
                    case "startBytecode": 
                        bi.setStartbytecode(Integer.parseInt(attributes.item(j).getNodeValue()));
                        break;
                    case "sourcepath": 
                        bi.setSourcepath(attributes.item(j).getNodeValue());
                        break;    
                }    
            }
        }
    }
    private void printAtt(NamedNodeMap attributes){
        if(attributes!=null){
                    int l=attributes.getLength();
                    for(int j=0; j<l ;j++){
                            System.out.print("   "+attributes.item(j).getNodeName());
                            System.out.println(" : "+attributes.item(j).getNodeValue());
                    }
                }
    }
    
}
