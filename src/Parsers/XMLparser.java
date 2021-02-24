/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

    /**
    * Class used to provide a general algorithm for reading xml files
    * @author mattia
    */

public class XMLparser {
    public static final boolean DEBUG = false ;
    private Extrapolator ex;
    /**
    * Constructor
    * @param ex extrapolator to use for reading the  xml file
    * @see Extrapolator
    * @author mattia
    */
    public XMLparser(Extrapolator ex) {
        this.ex=ex;
    }
    
    /**
    * This method is used to visit the tree in the xml file and parse the data 
    * @param path String that represent the path to the xml file
    * @return boolean true if the operation is successfull
    * @author mattia
    * @throws Exception path
    */
    public boolean parse(String path) throws Exception{
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(path);
            Element docEle = dom.getDocumentElement();
            Thread.sleep(300);
            recursiveNode(docEle);
            Thread.sleep(200);
            ex.finalOperations();
            
            return true;    
            
        } catch (@SuppressWarnings("LocalVariableHidesMemberVariable") ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLparser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void recursiveNode(Node n){
        ex.extrapolate(n , n.getAttributes()); //visita nodo
        if(n.hasChildNodes()){
            NodeList nodeList = n.getChildNodes();
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++)
                recursiveNode(nodeList.item(i));
        }
    }
    
    /**
    * This method is used to set the extrapolator for a different parsing 
    * @param ex ex extrapolator to use for reading the  xml file
    * @author mattia
    */
    public void setExtrapolator(Extrapolator ex) {
        this.ex = ex;
    }   
    
}
